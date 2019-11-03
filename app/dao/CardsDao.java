package dao;

import com.google.inject.Inject;
import customContexts.DatabaseExecutionContext;
import io.ebean.EbeanServer;
import io.ebean.Ebean;
import io.ebean.Query;

import java.util.List;

import models.Card;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;
import utils.Logger;

public class CardsDao
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    private final Logger logger;

    @Inject
    public CardsDao
    (
        EbeanConfig ebeanConfig,
        EbeanDynamicEvolutions ebeanDynamicEvolutions,
        DatabaseExecutionContext databaseExecutionContext,

        Logger logger
    )
    {
        this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
        this.db = Ebean.getServer(ebeanConfig.defaultServer());
        this.databaseExecutionContext = databaseExecutionContext;

        this.logger = logger;
    }

    public Card get(Long id)
    {
        Card card = null;

        Query<Card> query = this.db.find(Card.class);
        try
        {
            card = query.where().eq("id", id).findOne();
        }
        catch(Exception ex)
        {
            this.logger.error("Exception while getting card. Id: " + id + ". Message: " + ex.getMessage());
        }

        return card;
    }

    public Card save(Card card)
    {
        try
        {
            this.db.save(card);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
        return card;
    }

    public Card getLatest(String name)
    {
        Card card = null;

        try
        {
            List<Card> cards = this.db.find(Card.class).where().eq("name", name).setMaxRows(1).orderBy("version DESC").findList();
            if(!cards.isEmpty())
            {
                card = cards.get(0);
            }
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return card;
    }
}