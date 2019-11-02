package services.impl;

import com.google.inject.Inject;
import enums.ElasticIndex;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import play.libs.Json;
import responses.ElasticResponse;
import services.ElasticService;
import utils.Logger;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ElasticServiceImpl implements ElasticService
{
    private static RestHighLevelClient client = null;

    private final Logger logger;

    @Inject
    public ElasticServiceImpl
    (
        Logger logger
    )
    {
        this.logger = logger;

        if(null == client)
        {
            try
            {

                RestClientBuilder builder = RestClient.builder(
                    new HttpHost(System.getenv("ELASTIC_IP_HTTP"), Integer.parseInt(System.getenv("ELASTIC_PORT_HTTP")), System.getenv("ELASTIC_SCHEME"))
                );


                if(1 == Integer.parseInt(System.getenv("ELASTIC_USE_CREDENTIALS")))
                {
                    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(System.getenv("ELASTIC_USERNAME"), System.getenv("ELASTIC_PASSWORD")));

                    builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
                }

                client = new RestHighLevelClient(builder);
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }
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
        catch(Exception ex)
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
            this.logger.error("Exception while indexing document. Index: " + index.getName() + ". Id: " + id + "Message: " + ex.getMessage());
        }
        return isSuccess;
    }
}
