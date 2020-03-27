package dao;

import com.google.inject.Inject;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.ExpressionList;
import models.CardSubTypeMap;
import requests.CardSubTypeMapFilterRequest;
import utils.Logger;

import java.util.ArrayList;
import java.util.List;

public class CardSubTypeMapDao
{
    private final EbeanServer db = Ebean.getServer("default");

    private final Logger logger;

    @Inject
    public CardSubTypeMapDao
    (
        Logger logger
    )
    {
        this.logger = logger;
    }

    private ExpressionList<CardSubTypeMap> request(CardSubTypeMapFilterRequest request)
    {
        ExpressionList<CardSubTypeMap> expressionList = db.find(CardSubTypeMap.class).where();

        if(!request.getCardIds().isEmpty())
        {
            expressionList.in("cardId", request.getCardIds());
        }

        if(!request.getCardSubTypes().isEmpty())
        {
            expressionList.in("cardSubType", request.getCardSubTypes());
        }

        return expressionList;
    }

    public List<CardSubTypeMap> list(CardSubTypeMapFilterRequest request)
    {
        List<CardSubTypeMap> cardSubTypeMaps = new ArrayList<>();

        ExpressionList<CardSubTypeMap> expressionList = this.request(request);
        try
        {
            cardSubTypeMaps = expressionList.findList();
        }
        catch(Exception ex)
        {
            this.logger.error("Exception while filtering. Message: " + ex.getMessage());
        }

        return cardSubTypeMaps;
    }

    public List<CardSubTypeMap> create(List<CardSubTypeMap> cardSubTypeMaps)
    {
        try
        {
            this.db.saveAll(cardSubTypeMaps);
        }
        catch (Exception ex)
        {
            this.logger.error("Exception while saving cardSubTypeMap. Message: " + ex.getMessage());
        }

        return cardSubTypeMaps;
    }

    public boolean delete(List<CardSubTypeMap> cardSubTypeMaps)
    {
        boolean isSuccess = false;
        try
        {
            this.db.deleteAll(cardSubTypeMaps);
            isSuccess = true;
        }
        catch(Exception ex)
        {
            this.logger.error("Exception while deleting cardSubTypeMap. Message: " + ex.getMessage());
        }
        return isSuccess;
    }
}
