package utils;

import com.google.inject.Inject;
import responses.HTTPResponse;
import play.libs.Json;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.libs.ws.WSBodyReadables;
import play.libs.ws.WSBodyWritables;
import play.libs.ws.WSClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class Api implements WSBodyReadables, WSBodyWritables
{
    private final WSClient ws;

    @Inject
    public Api(WSClient ws)
    {
        this.ws = ws;
    }

    public CompletionStage<HTTPResponse> get(String url)
    {
        return get(url, new HashMap<>(), new HashMap<>());
    }

    public CompletionStage<HTTPResponse> get(String url, Map<String, Object> params)
    {
        return get(url, params, new HashMap<>());
    }

    public CompletionStage<HTTPResponse> get(String url, Map<String, Object> params, Map<String, String> headers)
    {
        CompletionStage<HTTPResponse> finalResponse = CompletableFuture.supplyAsync(HTTPResponse::new);

        try
        {
            WSRequest request = buildRequest(url, params, headers, "GET");
            CompletionStage<WSResponse> response = request.get();
            finalResponse = buildResponse(response);
        }
        catch(Exception ex)
        {

        }

        return finalResponse;
    }

    public CompletionStage<HTTPResponse> post(String url, Map<String, Object> params)
    {
        return post(url, params, new HashMap<>());
    }

    public CompletionStage<HTTPResponse> post(String url, Map<String, Object> params, Map<String, String> headers)
    {
        CompletionStage<HTTPResponse> finalResponse = CompletableFuture.supplyAsync(HTTPResponse::new);

        try
        {
            WSRequest request = buildRequest(url, new HashMap<>(), headers, "POST");
            CompletionStage<WSResponse> response = request.post(Json.toJson(params));
            finalResponse = buildResponse(response);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return finalResponse;
    }

    private WSRequest buildRequest(String url, Map<String, Object> params, Map<String, String> headers, String methodType)
    {
        WSRequest request = this.ws.url(url);

        for (Map.Entry<String, String> entry : headers.entrySet())
        {
            String key = entry.getKey();
            String value = entry.getValue();

            request.addHeader(key, value);
        }

        switch (methodType)
        {
            case "GET":
                for (Map.Entry<String, Object> entry : params.entrySet())
                {
                    String key = entry.getKey();
                    String value = (String) entry.getValue();

                    request.addQueryParameter(key, value);
                }
                break;
        }


        return request;
    }

    private CompletionStage<HTTPResponse> buildResponse(CompletionStage<WSResponse> response)
    {
        return response.thenApplyAsync(wsResponse -> {
            HTTPResponse responseFromPromise;
            try
            {
                responseFromPromise = new HTTPResponse(wsResponse.getStatus(), wsResponse.asJson());
            }
            catch(Exception ex)
            {
                responseFromPromise = new HTTPResponse(wsResponse.getStatus(), Json.toJson(wsResponse.getBody()));
            }

            return responseFromPromise;
        });
    }
}
