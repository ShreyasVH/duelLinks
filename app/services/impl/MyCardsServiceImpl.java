package services.impl;

import com.google.inject.Inject;
import dao.MyCardsDao;
import models.MyCard;
import play.libs.concurrent.HttpExecutionContext;
import requests.MyCardRequest;
import responses.CardSnippet;
import services.MyCardsService;
import utils.Utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MyCardsServiceImpl implements MyCardsService
{
    private final MyCardsDao myCardsDao;

    @Inject
    MyCardsServiceImpl
    (
        MyCardsDao myCardsDao
    )
    {
        this.myCardsDao = myCardsDao;
    }

    @Override
    public CompletionStage<MyCard> create(MyCardRequest myCardRequest)
    {
        MyCard myCard = new MyCard();
        myCard.setCardId(myCardRequest.getCardId());
        myCard.setCardGlossType(myCardRequest.getGlossType());
        myCard.setObtainedDate(Utils.getCurrentDate());

        return this.myCardsDao.save(myCard);
    }
}
