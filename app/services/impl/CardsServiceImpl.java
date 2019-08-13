package services.impl;

import dao.CardSubTypeMapDao;
import enums.*;
import models.Card;
import models.CardSubTypeMap;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import play.libs.concurrent.HttpExecutionContext;
import requests.CardRequest;
import requests.CardSubTypeMapFilterRequest;
import requests.CardsFilterRequest;
import responses.*;
import services.CardsService;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import dao.CardsDao;
import services.ElasticService;
import utils.Utils;

public class CardsServiceImpl implements CardsService
{
    private final CardsDao cardsDao;
    private final CardSubTypeMapDao cardSubTypeMapDao;

    private final ElasticService elasticService;

    @Inject
    public CardsServiceImpl
    (
        CardsDao cardsDao,
        CardSubTypeMapDao cardSubTypeMapDao,
        ElasticService elasticService
    )
    {
        this.cardsDao = cardsDao;
        this.cardSubTypeMapDao = cardSubTypeMapDao;

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

    @Override
    public CompletionStage<Boolean> index(Long id)
    {
        CompletionStage<Card> cardResponse = this.cardsDao.get(id);
        return cardResponse.thenApplyAsync(card -> {
            Boolean isSuccess = false;
            if(null != card)
            {
                isSuccess = elasticService.index(ElasticIndex.CARDS, id.toString(), cardSnippet(card));
            }
            return isSuccess;
        });
    }

    private CardSnippet cardSnippet(Card card)
    {
        return this.cardSnippet(card, null);
    }

    private CardSnippet cardSnippet(Card card, List<CardSubTypeMap> cardSubTypeMaps)
    {
        CardSnippet cardSnippet = new CardSnippet();
        cardSnippet.setId(card.getId());
        cardSnippet.setName(card.getName());
        cardSnippet.setLevel(card.getLevel());
        cardSnippet.setAttribute(new AttributeSnippet(card.getAttribute()));
        cardSnippet.setType(new TypeSnippet(card.getType()));
        cardSnippet.setAttack(card.getAttack());
        cardSnippet.setDefense(card.getDefense());
        cardSnippet.setCardType(card.getCardType());
        cardSnippet.setRarity(card.getRarity());
        cardSnippet.setLimitType(card.getLimitType());
        cardSnippet.setImageUrl(card.getImageUrl());

        CardSubTypeMapFilterRequest cardSubTypeMapFilterRequest = new CardSubTypeMapFilterRequest();
        cardSubTypeMapFilterRequest.setCardIds(Collections.singletonList(card.getId()));

        if(null == cardSubTypeMaps)
        {
            cardSubTypeMaps = this.cardSubTypeMapDao.list(cardSubTypeMapFilterRequest);
        }

        List<CardSubType> cardSubTypeList = new ArrayList<>();
        for(CardSubTypeMap cardSubTypeMap: cardSubTypeMaps)
        {
            cardSubTypeList.add(cardSubTypeMap.getCardSubType());
        }
        cardSnippet.setCardSubTypes(cardSubTypeList);

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

        builder.query(query);
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
    public CardSnippet create(CardRequest request, HttpExecutionContext httpExecutionContext)
    {
        CardSnippet cardSnippet = null;
        Card card = this.cardFromRequest(request);
        card = this.cardsDao.save(card);
        final Card cardForAsyncProcess = card;

        if(null != card.getId())
        {
            List<CardSubTypeMap> cardSubTypeMaps = new ArrayList<>();
            for(CardSubType cardSubType: request.getCardSubTypes())
            {
                CardSubTypeMap cardSubTypeMap = new CardSubTypeMap();
                cardSubTypeMap.setCardId(card.getId());
                cardSubTypeMap.setCardSubType(cardSubType);

                cardSubTypeMaps.add(cardSubTypeMap);
            }

            this.cardSubTypeMapDao.create(cardSubTypeMaps);
            cardSnippet = this.cardSnippet(card, cardSubTypeMaps);

            CompletableFuture.supplyAsync(() -> index(cardForAsyncProcess.getId()));
        }
        return cardSnippet;
    }

    @Override
    public CompletionStage<CardSnippet> update(CardRequest request, HttpExecutionContext httpExecutionContext)
    {
        CompletionStage<Card> existingCardPromise = this.cardsDao.get(request.getId());
        return existingCardPromise.thenApplyAsync(existingCard -> {
            CardSnippet cardSnippet = null;
            if(null != existingCard)
            {
                Card card = this.cardFromRequest(request, existingCard);
                card = this.cardsDao.save(card);

                final Card cardForAsyncProcess = card;

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
                CompletableFuture.supplyAsync(() -> index(cardForAsyncProcess.getId()));
            }
            return cardSnippet;
        }, httpExecutionContext.current());
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