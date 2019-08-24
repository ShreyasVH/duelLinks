package dao;

import com.google.inject.Inject;
import customContexts.DatabaseExecutionContext;
import enums.Status;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import models.MyCard;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;

import java.util.ArrayList;
import java.util.List;


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

    public MyCard save(MyCard myCard)
    {
        try
        {
            this.db.save(myCard);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
        return myCard;
    }

    public List<MyCard> getByCardId(Long cardId)
    {
        List<MyCard> cards = new ArrayList<>();

        try
        {
            cards = this.db.find(MyCard.class).where().eq("cardId", cardId).eq("status", Status.ENABLED).orderBy("obtainedDate DESC").findList();
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return cards;
    }
}
