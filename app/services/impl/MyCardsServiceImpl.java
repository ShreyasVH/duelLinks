package services.impl;

import com.google.inject.Inject;
import dao.MyCardsDao;
import enums.CardGlossType;
import enums.ElasticIndex;
import models.MyCard;
import play.libs.concurrent.HttpExecutionContext;
import requests.MyCardRequest;
import responses.CardSnippet;
import responses.MyCardSnippet;
import services.CardsService;
import services.ElasticService;
import services.MyCardsService;
import utils.Utils;

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
    public CompletionStage<MyCard> create(MyCardRequest myCardRequest)
    {
        MyCard myCard = new MyCard();
        myCard.setCardId(myCardRequest.getCardId());
        myCard.setCardGlossType(myCardRequest.getGlossType());
        myCard.setObtainedDate(Utils.getCurrentDate());

        return this.myCardsDao.save(myCard);
    }

    @Override
    public CompletionStage<MyCardSnippet> index(Long cardId, HttpExecutionContext httpExecutionContext)
    {
        MyCardSnippet myCardSnippet = new MyCardSnippet();
        myCardSnippet.setId(cardId);

        CardSnippet cardSnippet = this.cardsService.get(cardId);

        myCardSnippet.setCard(cardSnippet);

        CompletionStage<List<MyCard>> myCardListResponse = this.myCardsDao.getByCardId(cardId);
        return myCardListResponse.thenApplyAsync(myCardList -> {

            if(!myCardList.isEmpty())
            {
                Map<String, Integer> glossTypeStats = new HashMap<>();

                Integer normalCount = 0;
                Integer glossyCount = 0;
                Integer prismaticCount = 0;

                for(MyCard myCard: myCardList)
                {
                    switch(myCard.getCardGlossType())
                    {
                        case NORMAL:
                            normalCount++;
                            break;
                        case GLOSSY:
                            glossyCount++;
                            break;
                        case PRISMATIC:
                            prismaticCount++;
                            break;
                    }
                }

                glossTypeStats.put(CardGlossType.NORMAL.name(), normalCount);
                glossTypeStats.put(CardGlossType.GLOSSY.name(), glossyCount);
                glossTypeStats.put(CardGlossType.PRISMATIC.name(), prismaticCount);

                myCardSnippet.setGlossTypeStats(glossTypeStats);

                myCardSnippet.setLastObtainedDate(myCardList.get(0).getObtainedDate());
                myCardSnippet.setFirstObtainedDate(myCardList.get(myCardList.size() - 1).getObtainedDate());

                if(null != cardSnippet)
                {
                    this.elasticService.index(ElasticIndex.MY_CARDS, cardId.toString(), myCardSnippet);
                }
            }
            return myCardSnippet;
        }, httpExecutionContext.current());
    }
}
