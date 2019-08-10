package controllers;

import com.google.inject.Inject;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;
import play.libs.Json;

import requests.CardRequest;
import requests.CardsFilterRequest;
import responses.CardSnippet;
import services.CardsService;
import utils.Utils;

public class CardsController extends BaseController
{
    private final CardsService cardsService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    public CardsController
    (
        CardsService cardsService,
        HttpExecutionContext httpExecutionContext
    )
    {
        this.cardsService = cardsService;
        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> cardsService.get(id), httpExecutionContext.current()).thenApplyAsync(response -> ok(Json.toJson(response)));
    }

    public CompletionStage<Result> getWithFilters()
    {
        return CompletableFuture.supplyAsync(() -> {
            CardsFilterRequest request = null;
            try
            {
                request = Utils.convertObject(request().body().asJson(), CardsFilterRequest.class);
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }
            return cardsService.getWithFilters(request);
        }, httpExecutionContext.current()).thenApplyAsync(response -> ok(Json.toJson(response)));
    }

    public CompletionStage<Result> index(Long id)
    {
        return cardsService.index(id).thenApplyAsync(response -> ok(Json.toJson(response)));
    }

    public CompletionStage<Result> create()
    {
        return CompletableFuture.supplyAsync(() -> {
            CardRequest request = null;
            try
            {
                request = Utils.convertObject(request().body().asJson(), CardRequest.class);
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }
            return cardsService.create(request, httpExecutionContext);
        }, httpExecutionContext.current()).thenApplyAsync(response -> ok(Json.toJson(response)), httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Http.Request request)
    {
        CardRequest cardRequest = null;
        try
        {
            cardRequest = Utils.convertObject(request.body().asJson(), CardRequest.class);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
        CompletionStage<CardSnippet> promise = cardsService.update(cardRequest, httpExecutionContext);
        return promise.thenApplyAsync(response -> ok(formatResponse(response)), httpExecutionContext.current());
    }
}