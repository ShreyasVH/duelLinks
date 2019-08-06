package services;

import models.Card;
import requests.CardsFilterRequest;
import responses.CardSnippet;
import responses.ElasticResponse;

public interface CardsService
{
    CardSnippet get(Long id);

    ElasticResponse<CardSnippet> getWithFilters(CardsFilterRequest filterRequest);

    Boolean index(Long id);
}