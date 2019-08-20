package services;

import models.MyCard;
import play.libs.concurrent.HttpExecutionContext;
import requests.MyCardRequest;

import java.util.concurrent.CompletionStage;

public interface MyCardsService
{
    CompletionStage<MyCard> create(MyCardRequest myCardRequest);
}
