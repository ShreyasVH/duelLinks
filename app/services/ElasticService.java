package services;

import enums.ElasticIndex;
import org.elasticsearch.action.search.SearchRequest;
import play.libs.Json;
import responses.ElasticResponse;

public interface ElasticService
{
    <T> ElasticResponse<T> search(SearchRequest request, Class<T> documentClass);

    Boolean index(ElasticIndex index, String id, Object document);
}
