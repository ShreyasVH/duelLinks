package services;

import models.Source;
import requests.SourceRequest;
import responses.SourceSnippet;

import java.util.List;

public interface SourceService
{
    SourceSnippet create(SourceRequest request);

    SourceSnippet update(SourceRequest request);

    boolean obtain(Long sourceId);

    boolean redeem(Long sourceId);

    SourceSnippet get(Long sourceId);

    List<Source> getAll();
}
