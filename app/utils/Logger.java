package utils;

import com.google.inject.Inject;
import responses.HTTPResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class Logger
{
    private final Api api;

    @Inject
    public Logger(Api api)
    {
        this.api = api;
    }

    private CompletionStage<HTTPResponse> log(String content, String type)
    {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("content", content);
        payload.put("source", System.getenv("LOGGER_SOURCE"));

        String url = System.getenv("LOGGER_API_ENDPOINT") + "logs";
        return this.api.post(url, payload);
    }

    public CompletionStage<HTTPResponse> success(String content)
    {
        return log(content, "SUCCESS");
    }

    public CompletionStage<HTTPResponse> error(String content)
    {
        return log(content, "ERROR");
    }

    public CompletionStage<HTTPResponse> debug(String content)
    {
        return log(content, "DEBUG");
    }
}
