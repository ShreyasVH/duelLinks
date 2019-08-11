package services;

import play.libs.concurrent.HttpExecutionContext;
import requests.CardRequest;
import requests.CardsFilterRequest;
import responses.*;

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

    List<TypeSnippet> getTypes();

    List<CardTypeSnippet> getCardTypes();
}