package services.impl;

import com.google.inject.Inject;
import enums.ElasticIndex;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import play.libs.Json;
import responses.ElasticResponse;
import services.ElasticService;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticServiceImpl implements ElasticService
{
    private static RestHighLevelClient client = null;

    @Inject
    public ElasticServiceImpl()
    {
        if(null == client)
        {
            client = new RestHighLevelClient(
                RestClient.builder(
                    new HttpHost(System.getenv("ELASTIC_IP_HTTP"), Integer.parseInt(System.getenv("ELASTIC_PORT_HTTP")), "http")
                )
            );
        }
    }

    @Override
    public <T> ElasticResponse<T> search(SearchRequest request, Class<T> documentClass)
    {
        ElasticResponse<T> elasticResponse = new ElasticResponse<>();

        try
        {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            SearchHits hits = response.getHits();
            elasticResponse.setTotalCount(hits.getTotalHits().value);
            SearchHit[] searchHits = hits.getHits();
            List<T> documents = new ArrayList<>();
            for(SearchHit searchHit: searchHits)
            {
                Map<String, Object> document = searchHit.getSourceAsMap();
                T formattedDocument = Utils.convertObject(document, documentClass);
                documents.add(formattedDocument);
            }

            elasticResponse.setDocuments(documents);
        }
        catch(IOException ex)
        {
            String sh = "sh";
        }

        return elasticResponse;
    }

    @Override
    public Boolean index(ElasticIndex index, String id, Object document)
    {
        Boolean isSuccess = false;
        IndexRequest indexRequest = new IndexRequest(index.getName());
        indexRequest.id(id);
        indexRequest.source(Json.toJson(document).toString(), XContentType.JSON);
        try
        {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            Result result = indexResponse.getResult();
            isSuccess = ((Result.CREATED.getOp() == result.getOp()) || (Result.UPDATED.getOp() == result.getOp()));
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
        return isSuccess;
    }
}
