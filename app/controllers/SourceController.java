package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import requests.SourceRequest;
import responses.SourceResponse;
import services.CardsService;
import services.SourceService;
import utils.ThreadUtils;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;

public class SourceController
{
    private final CardsService cardsService;
    private final SourceService sourceService;

    private final HttpExecutionContext httpExecutionContext;

    private final ThreadUtils threadUtils;

    @Inject
    public SourceController
    (
        CardsService cardsService,
        SourceService sourceService,

        HttpExecutionContext httpExecutionContext,

        ThreadUtils threadUtils
    )
    {
        this.cardsService = cardsService;
        this.sourceService = sourceService;

        this.httpExecutionContext = httpExecutionContext;

        this.threadUtils = threadUtils;
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

            SourceResponse response = this.sourceService.create(sourceRequest);
            if((null != sourceRequest) && (null != sourceRequest.getCards()))
            {
                this.threadUtils.schedule(() -> cardsService.indexCards(response.getCards()));
            }
            return response;
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

            SourceResponse response = this.sourceService.update(sourceRequest);
            if((null != sourceRequest) && (null != sourceRequest.getCards()))
            {
                this.threadUtils.schedule(() -> cardsService.indexCards(response.getCards()));
            }
            return response;
        }, this.httpExecutionContext.current())
        .thenApplyAsync(sourceResponse -> ok(Json.toJson(sourceResponse)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> obtain(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.sourceService.obtain(id), this.httpExecutionContext.current()).thenApplyAsync(isSuccess -> {
            if(isSuccess)
            {
                this.threadUtils.schedule(() -> cardsService.indexCardsForSource(id));
            }

            Map<String, Boolean> responseMap = new HashMap<>();
            responseMap.put("success", isSuccess);
            return ok(Json.toJson(responseMap));
        }, this.httpExecutionContext.current());
    }

    public CompletionStage<Result> redeem(Long id)
    {
        return CompletableFuture.supplyAsync(() -> this.sourceService.redeem(id), this.httpExecutionContext.current()).thenApplyAsync(isSuccess -> {
            if(isSuccess)
            {
                this.threadUtils.schedule(() -> cardsService.indexCardsForSource(id));
            }

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
