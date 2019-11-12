package services;

import models.Source;
import models.SourceCardMap;
import requests.SourceRequest;
import responses.SourceResponse;
import responses.SourceSnippet;

import java.util.List;

public interface SourceService
{
    SourceResponse create(SourceRequest request);

    SourceResponse update(SourceRequest request);

    boolean obtain(Long sourceId);

    boolean redeem(Long sourceId);

    SourceResponse get(Long sourceId);

    List<Source> getAll();

    List<SourceSnippet> getSourcesForCard(Long cardId);
}
