package services;

import models.Card;
import play.libs.concurrent.HttpExecutionContext;
import requests.CardRequest;
import requests.CardsFilterRequest;
import responses.CardSnippet;
import responses.ElasticResponse;

import java.util.concurrent.CompletionStage;

public interface CardsService
{
    CardSnippet get(Long id);

    ElasticResponse<CardSnippet> getWithFilters(CardsFilterRequest filterRequest);

    CompletionStage<Boolean> index(Long id);

    CardSnippet create(CardRequest request, HttpExecutionContext httpExecutionContext);

    CompletionStage<CardSnippet> update(CardRequest request, HttpExecutionContext httpExecutionContext);
}