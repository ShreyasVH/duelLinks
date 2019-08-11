package services;

import enums.Attribute;
import models.Card;
import play.libs.concurrent.HttpExecutionContext;
import requests.CardRequest;
import requests.CardsFilterRequest;
import responses.AttributeSnippet;
import responses.CardFilterResponse;
import responses.CardSnippet;
import responses.ElasticResponse;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface CardsService
{
    CardSnippet get(Long id);

    CardFilterResponse getWithFilters(CardsFilterRequest filterRequest);

    CompletionStage<Boolean> index(Long id);

    CardSnippet create(CardRequest request, HttpExecutionContext httpExecutionContext);

    CompletionStage<CardSnippet> update(CardRequest request, HttpExecutionContext httpExecutionContext);

    List<AttributeSnippet> getAttributes();
}