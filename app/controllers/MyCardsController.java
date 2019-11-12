package controllers;

import com.google.inject.Inject;
import models.MyCard;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import requests.MyCardRequest;
import services.CardsService;
import services.MyCardsService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MyCardsController extends BaseController
{
    private final CardsService cardsService;
    private final MyCardsService myCardsService;

    private final HttpExecutionContext httpExecutionContext;

    @Inject
    MyCardsController
    (
        CardsService cardsService,
        MyCardsService myCardsService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.cardsService = cardsService;
        this.myCardsService = myCardsService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> create(Http.Request request)
    {
        return CompletableFuture.supplyAsync(() -> {
            MyCardRequest myCardRequest = null;
            try
            {
                myCardRequest = Utils.convertObject(request.body().asJson(), MyCardRequest.class);
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }
            MyCard myCard = myCardsService.create(myCardRequest);
            if(null != myCard.getCardId())
            {
                CompletableFuture.supplyAsync(() -> this.cardsService.index(myCard.getCardId()));
            }
            return myCard;
        }, httpExecutionContext.current()).thenApplyAsync(myCard -> ok(Json.toJson(myCard)));
    }

    public CompletionStage<Result> get(Long cardId)
    {
        return CompletableFuture.supplyAsync(() -> myCardsService.get(cardId), httpExecutionContext.current()).thenApplyAsync(myCards -> ok(Json.toJson(myCards)));
    }
}
