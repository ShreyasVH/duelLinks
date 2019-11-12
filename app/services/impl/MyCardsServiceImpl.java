package services.impl;

import com.google.inject.Inject;
import dao.MyCardsDao;
import enums.CardGlossType;
import enums.ElasticIndex;
import models.MyCard;
import play.libs.concurrent.HttpExecutionContext;
import requests.MyCardRequest;
import responses.CardSnippet;
import responses.MyCardIndividualSnippet;
import responses.MyCardResponse;
import responses.MyCardSnippet;
import services.CardsService;
import services.ElasticService;
import services.MyCardsService;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MyCardsServiceImpl implements MyCardsService
{
    private final MyCardsDao myCardsDao;

    private final CardsService cardsService;
    private final ElasticService elasticService;

    @Inject
    MyCardsServiceImpl
    (
        MyCardsDao myCardsDao,

        CardsService cardsService,
        ElasticService elasticService
    )
    {
        this.myCardsDao = myCardsDao;

        this.cardsService = cardsService;
        this.elasticService = elasticService;
    }

    @Override
    public MyCard create(MyCardRequest myCardRequest)
    {
        MyCard myCard = null;
        Long cardId = myCardRequest.getCardId();
        CardSnippet cardSnippet = this.cardsService.get(cardId);

        if(null != cardSnippet)
        {
            myCard = new MyCard();
            myCard.setCardId(cardId);
            myCard.setCardGlossType(myCardRequest.getGlossType());
            myCard.setStatus(myCardRequest.getStatus());
            myCard.setObtainedDate(Utils.getCurrentDate());

            this.myCardsDao.save(myCard);
        }


        return myCard;
    }

    @Override
    public List<MyCard> get(Long id)
    {
        return this.myCardsDao.getByCardId(id);
    }
}
