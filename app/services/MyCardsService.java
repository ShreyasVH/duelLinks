package services;

import models.MyCard;
import play.libs.concurrent.HttpExecutionContext;
import requests.MyCardRequest;
import responses.MyCardSnippet;

import java.util.concurrent.CompletionStage;

public interface MyCardsService
{
    CompletionStage<MyCard> create(MyCardRequest myCardRequest);

    CompletionStage<MyCardSnippet> index(Long cardId, HttpExecutionContext httpExecutionContext);
}
