package controllers;

import com.google.inject.Inject;
import play.mvc.Result;
import java.util.concurrent.CompletionStage;

import play.libs.concurrent.HttpExecutionContext;
import play.libs.Json;

import services.CardsService;

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
        return computeAsync(cardsService.get(id)).thenApplyAsync(response -> ok(Json.toJson(response)));
    }

//    public CompletionStage<Result> getWithFilters()
//    {
//        return CompletableFuture.supplyAsync(() -> cardsService.getWithFilters()).thenApplyAsync(response -> ok(Json.toJson(response)));
//    }
}