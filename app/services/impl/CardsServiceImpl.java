package services.impl;

import enums.CardElasticAttribute;
import enums.ElasticIndex;
import models.Card;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import requests.CardsFilterRequest;
import responses.CardSnippet;
import responses.ElasticResponse;
import services.CardsService;
import com.google.inject.Inject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.CardsDao;
import services.ElasticService;
import utils.Utils;

public class CardsServiceImpl implements CardsService
{
    private final CardsDao cardsDao;

    private final ElasticService elasticService;

    @Inject
    public CardsServiceImpl
    (
        CardsDao cardsDao,
        ElasticService elasticService
    )
    {
        this.cardsDao = cardsDao;

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

        ElasticResponse<CardSnippet> cardResponse = getWithFilters(request);
        if(!cardResponse.getDocuments().isEmpty())
        {
            cardSnippet = cardResponse.getDocuments().get(0);
        }

        return cardSnippet;
    }


    @Override
    public ElasticResponse<CardSnippet> getWithFilters(CardsFilterRequest filterRequest)
    {
        SearchRequest searchRequest = buildElasticRequest(filterRequest);
        return elasticService.search(searchRequest, CardSnippet.class);
    }

    @Override
    public Boolean index(Long id)
    {
        Boolean isSuccess = false;
        Card card = this.cardsDao.get(id);
        if(null != card)
        {
            isSuccess = elasticService.index(ElasticIndex.CARDS, id.toString(), cardSnippet(card));
        }

        return isSuccess;
    }

    private CardSnippet cardSnippet(Card card)
    {
        return Utils.convertObject(card, CardSnippet.class);
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
                    query.must(QueryBuilders.termsQuery(key, valueList));
                }
            }
        }

        builder.query(query);
        request.source(builder);
        return request;
    }
}