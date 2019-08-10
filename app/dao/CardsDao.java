package dao;

import com.google.inject.Inject;
import customContexts.DatabaseExecutionContext;
import io.ebean.EbeanServer;
import io.ebean.Ebean;
import io.ebean.Query;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import models.Card;
import play.db.ebean.EbeanConfig;

public class CardsDao
{
    private final EbeanServer db;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public CardsDao(EbeanConfig ebeanConfig, DatabaseExecutionContext databaseExecutionContext)
    {
        this.db = Ebean.getServer(ebeanConfig.defaultServer());
        this.databaseExecutionContext = databaseExecutionContext;
    }

    public CompletionStage<Card> get(Long id)
    {
        return CompletableFuture.supplyAsync(() -> {
            Card card = null;

            Query<Card> query = this.db.find(Card.class);
            try
            {
                card = query.where().eq("id", id).findOne();
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }

            return card;
        }, databaseExecutionContext);
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
}