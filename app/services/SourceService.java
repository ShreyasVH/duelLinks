package services;

import models.Source;
import requests.SourceRequest;
import responses.SourceResponse;

import java.util.List;

public interface SourceService
{
    SourceResponse create(SourceRequest request);

    SourceResponse update(SourceRequest request);

    boolean obtain(Long sourceId);

    boolean redeem(Long sourceId);

    SourceResponse get(Long sourceId);

    List<Source> getAll();
}
