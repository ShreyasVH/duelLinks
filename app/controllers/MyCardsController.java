package controllers;

import com.google.inject.Inject;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import requests.MyCardRequest;
import services.MyCardsService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MyCardsController extends BaseController
{
    private final MyCardsService myCardsService;
    private final HttpExecutionContext httpExecutionContext;

    @Inject
    MyCardsController
    (
        MyCardsService myCardsService,

        HttpExecutionContext httpExecutionContext
    )
    {
        this.myCardsService = myCardsService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> create(Http.Request request)
    {
        MyCardRequest myCardRequest = null;
        try
        {
            myCardRequest = Utils.convertObject(request.body().asJson(), MyCardRequest.class);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
        return myCardsService.create(myCardRequest).thenApplyAsync(card -> {
            CompletableFuture.supplyAsync(() -> myCardsService.index(card.getCardId(), httpExecutionContext));

            return ok(Json.toJson(card));
        }, httpExecutionContext.current());
    }

    public CompletionStage<Result> index(Long cardId)
    {
        return myCardsService.index(cardId, this.httpExecutionContext).thenApplyAsync(myCardSnippet -> ok(Json.toJson(myCardSnippet)), httpExecutionContext.current());
    }
}
