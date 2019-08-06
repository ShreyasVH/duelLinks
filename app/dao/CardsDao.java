package dao;

import com.google.inject.Inject;
import io.ebean.EbeanServer;
import io.ebean.Ebean;
import io.ebean.Query;
import java.util.List;
import java.util.ArrayList;

import models.Card;
import models.CardSubTypeMap;

public class CardsDao
{
    private final EbeanServer db = Ebean.getServer("default");

    @Inject
    public CardsDao()
    {

    }

    public Card get(Long id)
    {
        Card card = null;

        Query<Card> query = db.find(Card.class);
        try
        {
            card = query.where().eq("id", id).findOne();
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return card;
    }
}