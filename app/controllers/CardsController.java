package controllers;

import com.google.inject.Inject;
import enums.ErrorCode;
import exceptions.BadRequestException;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;
import play.libs.Json;

import requests.CardRequest;
import requests.CardsFilterRequest;
import requests.VersionRequest;
import services.CardsService;
import utils.ThreadUtils;
import utils.Utils;

public class CardsController extends BaseController
{
    private final CardsService cardsService;

    private final HttpExecutionContext httpExecutionContext;

    private final ThreadUtils threadUtils;

    @Inject
    public CardsController
    (
        CardsService cardsService,

        HttpExecutionContext httpExecutionContext,

        ThreadUtils threadUtils
    )
    {
        this.cardsService = cardsService;

        this.httpExecutionContext = httpExecutionContext;

        this.threadUtils = threadUtils;
    }

    public CompletionStage<Result> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> cardsService.get(id), httpExecutionContext.current()).thenApplyAsync(cardSnippet -> {
            if(null == cardSnippet)
            {
                return notFound(Json.toJson("Card Not Found"));
            }
            else
            {
                return ok(Json.toJson(cardSnippet));
            }
        }, httpExecutionContext.current());
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

    public CompletionStage<Result> getByKeyword(String keywordString)
    {
        return CompletableFuture.supplyAsync(() -> this.cardsService.getByKeyword(keywordString), this.httpExecutionContext.current()).thenApplyAsync(cards -> ok(Json.toJson(cards)), this.httpExecutionContext.current());
    }

    public CompletionStage<Result> index(Long id)
    {
        return CompletableFuture.supplyAsync(() -> ok(Json.toJson(cardsService.index(id))), httpExecutionContext.current());
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
            return cardsService.create(request);
        }, httpExecutionContext.current()).thenApplyAsync(response -> ok(Json.toJson(response)), httpExecutionContext.current());
    }

    public CompletionStage<Result> update(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            CardRequest cardRequest = null;
            try
            {
                cardRequest = Utils.convertObject(request.body().asJson(), CardRequest.class);
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }
            return cardsService.update(cardRequest);
        }, httpExecutionContext.current()).thenApplyAsync(cardSnippet -> ok(Json.toJson(cardSnippet)), httpExecutionContext.current());
    }

    public CompletionStage<Result> getAttributes()
    {
        return CompletableFuture.supplyAsync(() -> this.cardsService.getAttributes(), httpExecutionContext.current()).thenApplyAsync(attributes -> {
            Map<String, List> responseMap = new HashMap<>();
            responseMap.put("attributes", attributes);
            return ok(Json.toJson(responseMap));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getTypes()
    {
        return CompletableFuture.supplyAsync(() -> this.cardsService.getTypes(), httpExecutionContext.current()).thenApplyAsync(types -> {
            Map<String, List> responseMap = new HashMap<>();
            responseMap.put("types", types);
            return ok(Json.toJson(responseMap));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getCardTypes()
    {
        return CompletableFuture.supplyAsync(() -> this.cardsService.getCardTypes(), httpExecutionContext.current()).thenApplyAsync(cardTypes -> {
            Map<String, List> responseMap = new HashMap<>();
            responseMap.put("cardTypes", cardTypes);
            return ok(Json.toJson(responseMap));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getCardSubTypes()
    {
        return CompletableFuture.supplyAsync(() -> this.cardsService.getCardSubTypes(), httpExecutionContext.current()).thenApplyAsync(cardSubTypes -> {
            Map<String, List> responseMap = new HashMap<>();
            responseMap.put("cardSubTypes", cardSubTypes);
            return ok(Json.toJson(responseMap));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getRarities()
    {
        return CompletableFuture.supplyAsync(() -> this.cardsService.getRarities(), httpExecutionContext.current()).thenApplyAsync(rarities -> {
            Map<String, List> responseMap = new HashMap<>();
            responseMap.put("rarities", rarities);
            return ok(Json.toJson(responseMap));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> getLimitTypes()
    {
        return CompletableFuture.supplyAsync(() -> this.cardsService.getLimitTypes(), httpExecutionContext.current()).thenApplyAsync(limitTypes -> {
            Map<String, List> responseMap = new HashMap<>();
            responseMap.put("limitTypes", limitTypes);
            return ok(Json.toJson(responseMap));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> version(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            VersionRequest versionRequest = null;
            try
            {
                versionRequest = Utils.convertObject(request.body().asJson(), VersionRequest.class);
            }
            catch(Exception ex)
            {
                throw new BadRequestException(ErrorCode.INVALID_REQUEST.getCode(), ErrorCode.INVALID_REQUEST.getDescription());
            }

            return this.cardsService.version(versionRequest);
        }, httpExecutionContext.current()).thenApplyAsync(cardSnippet -> {
            if ((null != cardSnippet) && (null != cardSnippet.getId()))
            {
                this.threadUtils.schedule(() -> cardsService.index(cardSnippet.getId(), cardSnippet));
            }
            return ok(Json.toJson(cardSnippet));
        }, httpExecutionContext.current());
    }
}