package dao;

import com.google.inject.Inject;
import customContexts.DatabaseExecutionContext;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.MyCard;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class MyCardsDao
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public MyCardsDao
    (
        EbeanConfig ebeanConfig,
        EbeanDynamicEvolutions ebeanDynamicEvolutions,
        DatabaseExecutionContext databaseExecutionContext
    )
    {
        this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
        this.db = Ebean.getServer(ebeanConfig.defaultServer());
        this.databaseExecutionContext = databaseExecutionContext;
    }

    public CompletionStage<MyCard> save(MyCard myCard)
    {
        return CompletableFuture.supplyAsync(() -> {
            try
            {
                this.db.save(myCard);
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }
            return myCard;
        }, databaseExecutionContext);
    }
}
