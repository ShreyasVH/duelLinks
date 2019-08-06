package dao;

import com.google.inject.Inject;
import io.ebean.EbeanServer;
import io.ebean.Ebean;
import io.ebean.Query;
import java.util.List;
import java.util.ArrayList;

import models.Card;

public class CardsDao
{
    private static EbeanServer db = null;

    @Inject
    public CardsDao()
    {
        if(null == db)
        {
            db = Ebean.getServer("default");
        }
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
//
//    public List<Card> getWithFilters()
//    {
//        List<Card> cards = new ArrayList<>();
//
//        try
//        {
//            cards = db.find(Card.class).where().findList();
//        }
//        catch(Exception ex)
//        {
//
//        }
//
//        return cards;
//    }
}