package services.impl;

import dao.CardSourceMapDao;
import dao.CardSubTypeMapDao;
import dao.CardsDao;
import dao.MyCardsDao;
import dao.SourceDao;
import enums.Attribute;
import enums.CardElasticAttribute;
import enums.CardGlossType;
import enums.CardSubType;
import enums.CardType;
import enums.ElasticIndex;
import enums.ErrorCode;
import enums.FieldType;
import enums.LimitType;
import enums.Rarity;
import enums.Type;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import models.Card;
import models.CardSubTypeMap;
import models.MyCard;
import models.Source;
import models.SourceCardMap;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import play.libs.Json;
import requests.CardRequest;
import requests.CardSubTypeMapFilterRequest;
import requests.CardsFilterRequest;
import requests.SourceCardMapFilterRequest;
import requests.VersionRequest;
import responses.*;
import services.CardsService;
import com.google.inject.Inject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.CompletableFuture;

import services.ElasticService;
import utils.Logger;
import utils.Utils;

public class CardsServiceImpl implements CardsService
{
    private final CardsDao cardsDao;
    private final CardSourceMapDao cardSourceMapDao;
    private final CardSubTypeMapDao cardSubTypeMapDao;
    private final MyCardsDao myCardsDao;
    private final SourceDao sourceDao;

    private final ElasticService elasticService;

    private final Logger logger;

    @Inject
    public CardsServiceImpl
    (
        CardsDao cardsDao,
        CardSourceMapDao cardSourceMapDao,
        CardSubTypeMapDao cardSubTypeMapDao,
        MyCardsDao myCardsDao,
        SourceDao sourceDao,

        ElasticService elasticService,

        Logger logger
    )
    {
        this.cardsDao = cardsDao;
        this.cardSourceMapDao = cardSourceMapDao;
        this.cardSubTypeMapDao = cardSubTypeMapDao;
        this.myCardsDao = myCardsDao;
        this.sourceDao = sourceDao;

        this.elasticService = elasticService;

        this.logger = logger;
    }

    @Override
    public CardSnippet get(Long id)
    {
        CardSnippet cardSnippet = null;
        Map<String, List<String>> filters = new HashMap<>();
        CardsFilterRequest request = new CardsFilterRequest();
        filters.put("id", Collections.singletonList(id.toString()));
        request.setFilters(filters);

        CardFilterResponse cardResponse = getWithFilters(request);
        if(cardResponse.getCards().isEmpty())
        {
            throw new NotFoundException(ErrorCode.CARD_NOT_FOUND.getCode(), ErrorCode.CARD_NOT_FOUND.getDescription());
        }
        else
        {
            cardSnippet = cardResponse.getCards().get(0);
        }

        return cardSnippet;
    }


    @Override
    public CardFilterResponse getWithFilters(CardsFilterRequest filterRequest)
    {
        CardFilterResponse response = new CardFilterResponse();
        SearchRequest searchRequest = buildElasticRequest(filterRequest);
        ElasticResponse<CardSnippet> elasticResponse = elasticService.search(searchRequest, CardSnippet.class);
        response.setTotalCount(elasticResponse.getTotalCount());
        response.setCards(elasticResponse.getDocuments());
        response.setOffset(Long.parseLong(String.valueOf(filterRequest.getOffset() + Math.min(filterRequest.getCount(), elasticResponse.getDocuments().size()))));
        return response;
    }

    public Boolean indexCardsForSource(Long sourceId)
    {
        Boolean isCompleteSuccess = true;
        SourceCardMapFilterRequest request = new SourceCardMapFilterRequest();
        request.setId(sourceId);

        List<SourceCardMap> cardMaps = this.cardSourceMapDao.get(request);
        for(SourceCardMap cardMap: cardMaps)
        {
            Boolean isSuccess = this.index(cardMap.getCardId());
            isCompleteSuccess = (isCompleteSuccess && isSuccess);
        }
        return isCompleteSuccess;
    }

    public Boolean indexCards(List<SourceCardMap> cardMaps)
    {
        Boolean isCompleteSuccess = true;

        for(SourceCardMap cardMap: cardMaps)
        {
            Boolean isSuccess = this.index(cardMap.getCardId());
            isCompleteSuccess = (isCompleteSuccess && isSuccess);
        }

        return isCompleteSuccess;
    }

    @Override
    public Boolean index(Long id, CardSnippet cardSnippet)
    {
        boolean isSuccess = false;
        if(null != cardSnippet.getId())
        {
            isSuccess = elasticService.index(ElasticIndex.CARDS, id.toString(), cardSnippet);
        }
        return isSuccess;
    }

    @Override
    public Boolean index(Long id)
    {
        Boolean isSuccess = false;
        Card card = this.cardsDao.get(id);
        if(null == card)
        {
            this.logger.error("Could not find card for indexing. Id: " + id);
        }
        else
        {
            isSuccess = this.index(id, this.cardSnippet(card));
        }
        return isSuccess;
    }

    private Map<String, Integer> getGlossTypeStatsMap(List<MyCard> myCardList)
    {
        Map<String, Integer> glossTypeStats = new HashMap<>();

        Integer normalCount = 0;
        Integer glossyCount = 0;
        Integer prismaticCount = 0;

        for(MyCard myCard: myCardList)
        {
            switch(myCard.getCardGlossType())
            {
                case NORMAL:
                    normalCount++;
                    break;
                case GLOSSY:
                    glossyCount++;
                    break;
                case PRISMATIC:
                    prismaticCount++;
                    break;
            }
        }

        glossTypeStats.put(CardGlossType.NORMAL.name(), normalCount);
        glossTypeStats.put(CardGlossType.GLOSSY.name(), glossyCount);
        glossTypeStats.put(CardGlossType.PRISMATIC.name(), prismaticCount);

        return glossTypeStats;
    }

    private CardSnippet cardSnippet(Card card)
    {
        return this.cardSnippet(card, null);
    }

    private CardSnippet cardSnippet(Card card, List<CardSubTypeMap> cardSubTypeMaps)
    {
        return this.cardSnippet(card, cardSubTypeMaps, null);
    }

    private CardSnippet cardSnippet(Card card, List<CardSubTypeMap> cardSubTypeMaps, List<MyCard> myCards)
    {
        CardSnippet cardSnippet = new CardSnippet();
        cardSnippet.setId(card.getId());
        cardSnippet.setName(card.getName());
        cardSnippet.setDescription(card.getDescription());
        CardType cardType = card.getCardType();
        cardSnippet.setCardType(cardType);
        cardSnippet.setCardTypeId(cardType.getValue());
        cardSnippet.setReleaseDate(card.getReleaseDate());

        if(CardType.MONSTER.equals(cardType))
        {
            cardSnippet.setLevel(card.getLevel());
            cardSnippet.setAttribute(card.getAttribute());
            cardSnippet.setAttributeId(card.getAttribute().getValue());
            cardSnippet.setType(card.getType());
            cardSnippet.setTypeId(card.getType().getValue());
            cardSnippet.setAttack(card.getAttack());
            cardSnippet.setDefense(card.getDefense());
        }

        cardSnippet.setRarity(card.getRarity());
        cardSnippet.setRarityId(card.getRarity().getValue());
        cardSnippet.setLimitType(card.getLimitType());
        cardSnippet.setLimitTypeId(card.getLimitType().getValue());
        cardSnippet.setImageUrl(card.getImageUrl());
        cardSnippet.setVersion(card.getVersion());

        if(null == cardSubTypeMaps)
        {
            CardSubTypeMapFilterRequest cardSubTypeMapFilterRequest = new CardSubTypeMapFilterRequest();
            cardSubTypeMapFilterRequest.setCardIds(Collections.singletonList(card.getId()));
            cardSubTypeMaps = this.cardSubTypeMapDao.list(cardSubTypeMapFilterRequest);
        }

        List<CardSubType> cardSubTypeList = new ArrayList<>();
        List<Integer> cardSubTypeIdList = new ArrayList<>();
        for(CardSubTypeMap cardSubTypeMap: cardSubTypeMaps)
        {
            cardSubTypeList.add(cardSubTypeMap.getCardSubType());
            cardSubTypeIdList.add(cardSubTypeMap.getCardSubType().getValue());
        }
        cardSnippet.setCardSubTypes(cardSubTypeList);
        cardSnippet.setCardSubTypeIds(cardSubTypeIdList);

        if(null == myCards)
        {
            myCards = this.myCardsDao.getByCardId(card.getId());
        }

        cardSnippet.setGlossTypeStats(Json.toJson(this.getGlossTypeStatsMap(myCards)).toString());

        if(!myCards.isEmpty())
        {
            cardSnippet.setFirstObtainedDate(myCards.get(myCards.size() - 1).getObtainedDate());
            cardSnippet.setLastObtainedDate(myCards.get(0).getObtainedDate());
        }

        SourceCardMapFilterRequest sourceCardMapFilterRequest = new SourceCardMapFilterRequest();
        sourceCardMapFilterRequest.setCardId(cardSnippet.getId());
        List<SourceCardMap> sourceCardMaps = this.cardSourceMapDao.get(sourceCardMapFilterRequest);
        List<Long> sourceIds = new ArrayList<>();

        for(SourceCardMap sourceCardMap: sourceCardMaps)
        {
            Source source = this.sourceDao.getById(sourceCardMap.getSourceId());
            if(null != source)
            {
                sourceIds.add(source.getId());
            }
        }

        cardSnippet.setSourceIds(sourceIds);
        return cardSnippet;
    }

    private SearchRequest buildElasticRequest(CardsFilterRequest filterRequest)
    {
        SearchRequest request = new SearchRequest("cards");

        Map<String, List<String>> filters = filterRequest.getFilters();

        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(filterRequest.getOffset());
        builder.size(filterRequest.getCount());

        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if(!filters.isEmpty())
        {
            for(Map.Entry<String, List<String>> entry: filters.entrySet())
            {
                String key = entry.getKey();
                List<String> valueList = entry.getValue();
                if(!valueList.isEmpty())
                {
                    CardElasticAttribute cardElasticAttribute = CardElasticAttribute.fromString(key);
                    if(null != cardElasticAttribute && (FieldType.NORMAL.equals(cardElasticAttribute.getType())))
                    {
                        query.must(QueryBuilders.termsQuery(cardElasticAttribute.getTerm(), valueList));
                    }
                }
            }
        }

        Map<String, Map<String, Long>> rangeFilters = filterRequest.getRangeFilters();
        if(!rangeFilters.isEmpty())
        {
            for(Map.Entry<String, Map<String, Long>> entry: rangeFilters.entrySet())
            {
                String key = entry.getKey();
                Map<String, Long> valueMap = entry.getValue();
                CardElasticAttribute cardElasticAttribute = CardElasticAttribute.fromString(key);
                if(null != cardElasticAttribute)
                {
                    if(FieldType.RANGE.equals(cardElasticAttribute.getType()))
                    {
                        if(valueMap.containsKey("min") || valueMap.containsKey("max"))
                        {
                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(cardElasticAttribute.getTerm());

                            if(valueMap.containsKey("min"))
                            {
                                rangeQueryBuilder.gte(valueMap.get("min"));
                            }

                            if(valueMap.containsKey("max"))
                            {
                                rangeQueryBuilder.lte(valueMap.get("max"));
                            }

                            query.must(rangeQueryBuilder);
                        }
                    }
                }
            }
        }
        builder.query(query);

        Map<String, SortOrder> sortMap = filterRequest.getSortMap();
        for(Map.Entry<String, SortOrder> sortField: sortMap.entrySet())
        {
            String key = sortField.getKey();
            SortOrder order = sortField.getValue();

            CardElasticAttribute cardElasticAttribute = CardElasticAttribute.fromString(key);
            if(null != cardElasticAttribute)
            {
                String sortKey = ((cardElasticAttribute.getName().equals("name")) ? (key + ".sort") : key);
                builder.sort(sortKey, order);
            }
        }

        if(!sortMap.containsKey("name"))
        {
            builder.sort("name.sort", SortOrder.ASC);
        }

        if(!sortMap.containsKey("version"))
        {
            builder.sort("version", SortOrder.ASC);
        }

        request.source(builder);
        return request;
    }

    private Card cardFromRequest(CardRequest cardRequest)
    {
        return this.cardFromRequest(cardRequest, null);
    }

    private Card cardFromRequest(CardRequest cardRequest, Card existingCard)
    {
        Card card;
        if(null == existingCard)
        {
            card = Utils.convertObject(cardRequest, Card.class);
        }
        else
        {
            card = existingCard;

            if(null != cardRequest.getName())
            {
                card.setName(cardRequest.getName());
            }

            if(null != cardRequest.getDescription())
            {
                card.setDescription(cardRequest.getDescription());
            }

            if(null != cardRequest.getLevel())
            {
                card.setLevel(cardRequest.getLevel());
            }

            if(null != cardRequest.getAttribute())
            {
                card.setAttribute(cardRequest.getAttribute());
            }

            if(null != cardRequest.getType())
            {
                card.setType(cardRequest.getType());
            }

            if(null != cardRequest.getAttack())
            {
                card.setAttack(cardRequest.getAttack());
            }

            if(null != cardRequest.getDefense())
            {
                card.setDefense(cardRequest.getDefense());
            }

            if(null != cardRequest.getCardType())
            {
                card.setCardType(cardRequest.getCardType());
            }

            if(null != cardRequest.getRarity())
            {
                card.setRarity(cardRequest.getRarity());
            }

            if(null != cardRequest.getLimitType())
            {
                card.setLimitType(cardRequest.getLimitType());
            }

            if(null != cardRequest.getImageUrl())
            {
                card.setImageUrl(cardRequest.getImageUrl());
            }

            if(null != cardRequest.getReleaseDate())
            {
                try
                {
                    Date releaseDate = (new SimpleDateFormat("yyyy-MM-dd").parse(cardRequest.getReleaseDate()));
                    card.setReleaseDate(releaseDate);
                }
                catch(Exception ex)
                {
                    throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), "Invalid Release Date");
                }
            }
        }

        return card;
    }

    @Override
    public CardSnippet create(CardRequest request)
    {
        CardSnippet cardSnippet = null;


        Card existingCard = this.cardsDao.getLatest(request.getName());
        if(null != existingCard)
        {
            throw new BadRequestException(ErrorCode.ALREADY_EXISTS.getCode(), "Card Already Exists");
        }

        Card card = this.cardFromRequest(request);
        if(null == card.getReleaseDate())
        {
            card.setReleaseDate(Utils.getCurrentDate());
        }
        card = this.cardsDao.save(card);

        List<CardSubTypeMap> cardSubTypeMaps = new ArrayList<>();
        if(null != card.getId())
        {
            final Long cardId = card.getId();
            for(CardSubType cardSubType: request.getCardSubTypes())
            {
                CardSubTypeMap cardSubTypeMap = new CardSubTypeMap();
                cardSubTypeMap.setCardId(card.getId());
                cardSubTypeMap.setCardSubType(cardSubType);

                cardSubTypeMaps.add(cardSubTypeMap);
            }

            this.cardSubTypeMapDao.create(cardSubTypeMaps);

            cardSnippet = this.cardSnippet(card, cardSubTypeMaps, new ArrayList<>());
            final CardSnippet finalCardSnippet = cardSnippet;
            CompletableFuture.supplyAsync(() -> index(cardId, finalCardSnippet));
        }

        return cardSnippet;
    }

    @Override
    public CardSnippet update(CardRequest request)
    {
        Card existingCard = this.cardsDao.get(request.getId());
        CardSnippet cardSnippet = null;
        if(null != existingCard)
        {
            Card card = this.cardFromRequest(request, existingCard);
            card = this.cardsDao.save(card);

            final Long cardId = card.getId();
            cardSnippet = this.cardSnippet(card);

            if(null != request.getCardSubTypes())
            {
                CardSubTypeMapFilterRequest cardSubTypeMapFilterRequest = new CardSubTypeMapFilterRequest();
                cardSubTypeMapFilterRequest.setCardIds(Collections.singletonList(card.getId()));
                List<CardSubTypeMap> existingCardSubTypeMaps = this.cardSubTypeMapDao.list(cardSubTypeMapFilterRequest);

                List<CardSubType> cardSubTypesFromRequest = request.getCardSubTypes();

                List<CardSubType> existingCardSubTypes = new ArrayList<>();
                List<CardSubTypeMap> removedCardSubTypeMaps = new ArrayList<>();
                List<CardSubTypeMap> newCardSubTypeMaps = new ArrayList<>();
                List<CardSubTypeMap> updatedCardSubTypeMaps = new ArrayList<>();
                for(CardSubTypeMap cardSubTypeMap: existingCardSubTypeMaps)
                {
                    existingCardSubTypes.add(cardSubTypeMap.getCardSubType());

                    if(cardSubTypesFromRequest.contains(cardSubTypeMap.getCardSubType()))
                    {
                        updatedCardSubTypeMaps.add(cardSubTypeMap);
                    }
                    else
                    {
                        removedCardSubTypeMaps.add(cardSubTypeMap);
                    }
                }

                cardSubTypesFromRequest.removeAll(existingCardSubTypes);

                for(CardSubType cardSubType: cardSubTypesFromRequest)
                {
                    CardSubTypeMap cardSubTypeMap = new CardSubTypeMap();
                    cardSubTypeMap.setCardId(card.getId());
                    cardSubTypeMap.setCardSubType(cardSubType);

                    newCardSubTypeMaps.add(cardSubTypeMap);
                    updatedCardSubTypeMaps.add(cardSubTypeMap);
                }

                if(!removedCardSubTypeMaps.isEmpty())
                {
                    this.cardSubTypeMapDao.delete(removedCardSubTypeMaps);
                }

                if(!newCardSubTypeMaps.isEmpty())
                {
                    this.cardSubTypeMapDao.create(newCardSubTypeMaps);
                }

                cardSnippet = this.cardSnippet(card, updatedCardSubTypeMaps);
            }

            final CardSnippet finalCardSnippet = cardSnippet;
            CompletableFuture.supplyAsync(() -> index(cardId, finalCardSnippet));
        }
        return cardSnippet;
    }

    @Override
    public List<AttributeSnippet> getAttributes()
    {
        List<AttributeSnippet> attributes = new ArrayList<>();

        for(Attribute attribute: Attribute.values())
        {
            AttributeSnippet attributeSnippet = new AttributeSnippet(attribute);
            attributes.add(attributeSnippet);
        }

        return attributes;
    }

    @Override
    public List<TypeSnippet> getTypes()
    {
        List<TypeSnippet> types = new ArrayList<>();

        for(Type type: Type.values())
        {
            TypeSnippet typeSnippet = new TypeSnippet(type);
            types.add(typeSnippet);
        }

        return types;
    }

    @Override
    public List<CardTypeSnippet> getCardTypes()
    {
        List<CardTypeSnippet> cardTypes = new ArrayList<>();

        for(CardType cardType: CardType.values())
        {
            CardTypeSnippet cardTypeSnippet = new CardTypeSnippet(cardType);
            cardTypes.add(cardTypeSnippet);
        }

        return cardTypes;
    }

    @Override
    public List<CardSubTypeSnippet> getCardSubTypes()
    {
        List<CardSubTypeSnippet> cardSubTypes = new ArrayList<>();

        for(CardSubType cardSubType: CardSubType.values())
        {
            CardSubTypeSnippet cardSubTypeSnippet = new CardSubTypeSnippet(cardSubType);
            cardSubTypes.add(cardSubTypeSnippet);
        }

        return cardSubTypes;
    }

    @Override
    public List<RaritySnippet> getRarities()
    {
        List<RaritySnippet> rarities = new ArrayList<>();

        for(Rarity rarity: Rarity.values())
        {
            RaritySnippet raritySnippet = new RaritySnippet(rarity);
            rarities.add(raritySnippet);
        }

        return rarities;

    }

    @Override
    public List<LimitTypeSnippet> getLimitTypes()
    {
        List<LimitTypeSnippet> limitTypes = new ArrayList<>();

        for(LimitType limitType: LimitType.values())
        {
            LimitTypeSnippet limitTypeSnippet = new LimitTypeSnippet(limitType);
            limitTypes.add(limitTypeSnippet);
        }

        return limitTypes;
    }

    @Override
    public List<CardSnippet> getByKeyword(String keywordString)
    {
        SearchRequest request = new SearchRequest("cards");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        keywordString = URLDecoder.decode(keywordString);
        String[] words = keywordString.split(" ");
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery();
        for(String word : words)
        {
            word = word.toLowerCase();
            if(word.length() >= 2)
            {
                finalQuery.must(QueryBuilders.termQuery(CardElasticAttribute.NAME.getName(), word));
            }
        }
        builder.query(finalQuery);

        builder.size(1000);
        builder.sort("name.sort", SortOrder.ASC);
        builder.sort("id", SortOrder.ASC);

        request.source(builder);
        ElasticResponse<CardSnippet> elasticResponse = this.elasticService.search(request, CardSnippet.class);
        return elasticResponse.getDocuments();
    }

    @Override
    public CardSnippet version(VersionRequest request)
    {
        CardSnippet cardSnippet = null;
        Card card = null;
        String name = request.getName();
        String imageUrl = request.getImageUrl();

        Card previousCard = this.cardsDao.getLatest(name);

        if(null != previousCard)
        {
            Integer previousVersion = previousCard.getVersion();

            card = new Card(previousCard);
            card.setVersion(previousVersion + 1);
            card.setImageUrl(imageUrl);
            card.setId(null);

            this.cardsDao.save(card);

            if(null != card.getId())
            {
                CardSubTypeMapFilterRequest cardSubTypeMapFilterRequest = new CardSubTypeMapFilterRequest();
                cardSubTypeMapFilterRequest.setCardIds(Collections.singletonList(previousCard.getId()));
                List<CardSubTypeMap> cardSubTypeMaps = this.cardSubTypeMapDao.list(cardSubTypeMapFilterRequest);

                List<CardSubTypeMap> cardSubTypeMapsForNewVersion = new ArrayList<>();
                for(CardSubTypeMap cardSubTypeMap: cardSubTypeMaps)
                {
                    CardSubTypeMap cardSubTypeMapForNewVersion = new CardSubTypeMap();
                    cardSubTypeMapForNewVersion.setCardId(card.getId());
                    cardSubTypeMapForNewVersion.setCardSubType(cardSubTypeMap.getCardSubType());

                    cardSubTypeMapsForNewVersion.add(cardSubTypeMapForNewVersion);
                }
                this.cardSubTypeMapDao.create(cardSubTypeMapsForNewVersion);

                cardSnippet = this.cardSnippet(card, cardSubTypeMapsForNewVersion, new ArrayList<>());
            }
        }
        return cardSnippet;
    }
}