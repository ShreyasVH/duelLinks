package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import requests.SourceRequest;
import services.SourceService;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;

public class SourceController
{
    private final SourceService sourceService;

    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public SourceController
    (
        SourceService sourceService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.sourceService = sourceService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> create(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            SourceRequest sourceRequest = null;
            try
            {
                sourceRequest = Utils.convertObject(request.body().asJson(), SourceRequest.class);
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }

            return this.sourceService.create(sourceRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(sourceResponse -> ok(Json.toJson(sourceResponse)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            SourceRequest sourceRequest = null;
            try
            {
                sourceRequest = Utils.convertObject(request.body().asJson(), SourceRequest.class);
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }

            return this.sourceService.update(sourceRequest);
        }, this.httpExecutionContext.current()).thenApplyAsync(sourceResponse -> ok(Json.toJson(sourceResponse)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> obtain(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.sourceService.obtain(id), this.httpExecutionContext.current()).thenApplyAsync(isSuccess -> {
            Map<String, Boolean> responseMap = new HashMap<>();
            responseMap.put("success", isSuccess);
            return ok(Json.toJson(responseMap));
        }, this.httpExecutionContext.current());
    }

    public CompletionStage<Result> redeem(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.sourceService.redeem(id), this.httpExecutionContext.current()).thenApplyAsync(isSuccess -> {
            Map<String, Boolean> responseMap = new HashMap<>();
            responseMap.put("success", isSuccess);
            return ok(Json.toJson(responseMap));
        }, this.httpExecutionContext.current());
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.sourceService.get(id), this.httpExecutionContext.current()).thenApplyAsync(sourceResponse -> ok(Json.toJson(sourceResponse)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> getAll()
    {
        return CompletableFuture.supplyAsync(this.sourceService::getAll, this.httpExecutionContext.current()).thenApplyAsync(sources -> ok(Json.toJson(sources)), this.httpExecutionContext.current());
    }
}
