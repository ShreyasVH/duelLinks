package services.impl;

import dao.CardSubTypeMapDao;
import dao.MyCardsDao;
import enums.*;
import models.Card;
import models.CardSubTypeMap;
import models.MyCard;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import requests.CardRequest;
import requests.CardSubTypeMapFilterRequest;
import requests.CardsFilterRequest;
import responses.AttributeSnippet;
import responses.CardFilterResponse;
import responses.CardSnippet;
import responses.CardSubTypeSnippet;
import responses.CardTypeSnippet;
import responses.ElasticResponse;
import responses.LimitTypeSnippet;
import responses.RaritySnippet;
import responses.TypeSnippet;
import services.CardsService;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import dao.CardsDao;
import services.ElasticService;
import utils.Utils;

public class CardsServiceImpl implements CardsService
{
    private final CardsDao cardsDao;
    private final CardSubTypeMapDao cardSubTypeMapDao;
    private final MyCardsDao myCardsDao;

    private final ElasticService elasticService;

    @Inject
    public CardsServiceImpl
    (
        CardsDao cardsDao,
        CardSubTypeMapDao cardSubTypeMapDao,
        MyCardsDao myCardsDao,

        ElasticService elasticService
    )
    {
        this.cardsDao = cardsDao;
        this.cardSubTypeMapDao = cardSubTypeMapDao;
        this.myCardsDao = myCardsDao;

        this.elasticService = elasticService;
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
        if(!cardResponse.getCards().isEmpty())
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

    private Boolean index(Long id, CardSnippet cardSnippet)
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
        if(null != card)
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
        cardSnippet.setLevel(card.getLevel());
        cardSnippet.setAttribute(new AttributeSnippet(card.getAttribute()));
        cardSnippet.setType(new TypeSnippet(card.getType()));
        cardSnippet.setAttack(card.getAttack());
        cardSnippet.setDefense(card.getDefense());
        cardSnippet.setCardType(new CardTypeSnippet(card.getCardType()));
        cardSnippet.setRarity(new RaritySnippet(card.getRarity()));
        cardSnippet.setLimitType(new LimitTypeSnippet(card.getLimitType()));
        cardSnippet.setImageUrl(card.getImageUrl());

        CardSubTypeMapFilterRequest cardSubTypeMapFilterRequest = new CardSubTypeMapFilterRequest();
        cardSubTypeMapFilterRequest.setCardIds(Collections.singletonList(card.getId()));

        if(null == cardSubTypeMaps)
        {
            cardSubTypeMaps = this.cardSubTypeMapDao.list(cardSubTypeMapFilterRequest);
        }

        List<CardSubTypeSnippet> cardSubTypeList = new ArrayList<>();
        for(CardSubTypeMap cardSubTypeMap: cardSubTypeMaps)
        {
            cardSubTypeList.add(new CardSubTypeSnippet(cardSubTypeMap.getCardSubType()));
        }
        cardSnippet.setCardSubTypes(cardSubTypeList);

        if(null == myCards)
        {
            myCards = this.myCardsDao.getByCardId(card.getId());
        }
        cardSnippet.setIndividualCards(myCards);

        cardSnippet.setGlossTypeStats(this.getGlossTypeStatsMap(myCards));

        if(!myCards.isEmpty())
        {
            cardSnippet.setFirstObtainedDate(myCards.get(myCards.size() - 1).getObtainedDate());
            cardSnippet.setLastObtainedDate(myCards.get(0).getObtainedDate());
        }

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
                CardElasticAttribute cardElasticAttribute = CardElasticAttribute.fromString(key);
                if(null != cardElasticAttribute)
                {
                    if(FieldType.NORMAL.equals(cardElasticAttribute.getType()))
                    {
                        query.must(QueryBuilders.termsQuery(key, valueList));
                    }
                    else if(FieldType.NESTED.equals(cardElasticAttribute.getType()))
                    {
                        query.must(QueryBuilders.nestedQuery(cardElasticAttribute.getNestedLevel(), QueryBuilders.termsQuery(cardElasticAttribute.getNestedTerm(), valueList), ScoreMode.None));
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
                            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(key);

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
                builder.sort(key, order);
            }
        }

        if(!sortMap.containsKey("name"))
        {
            builder.sort("name", SortOrder.ASC);
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
        }

        return card;
    }

    @Override
    public CardSnippet create(CardRequest request)
    {
        CardSnippet cardSnippet = null;

        Card card = this.cardFromRequest(request);
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
}