package services;

import models.SourceCardMap;
import play.libs.concurrent.HttpExecutionContext;
import requests.CardRequest;
import requests.CardsFilterRequest;
import responses.AttributeSnippet;
import responses.CardFilterResponse;
import responses.CardSnippet;
import responses.CardSubTypeSnippet;
import responses.CardTypeSnippet;
import responses.LimitTypeSnippet;
import responses.RaritySnippet;
import responses.TypeSnippet;

import java.util.List;

public interface CardsService
{
    CardSnippet get(Long id);

    CardFilterResponse getWithFilters(CardsFilterRequest filterRequest);

    Boolean indexCardsForSource(Long sourceId);

    Boolean indexCards(List<SourceCardMap> cardMaps);

    Boolean index(Long id);

    CardSnippet create(CardRequest request);

    CardSnippet update(CardRequest request);

    List<AttributeSnippet> getAttributes();

    List<TypeSnippet> getTypes();

    List<CardTypeSnippet> getCardTypes();

    List<CardSubTypeSnippet> getCardSubTypes();

    List<RaritySnippet> getRarities();

    List<LimitTypeSnippet> getLimitTypes();

    List<CardSnippet> getByKeyword(String keywordString);
}