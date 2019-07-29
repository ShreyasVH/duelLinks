package services.impl;

import services.CardsService;
import com.google.inject.Inject;
import java.util.List;

import models.Card;

import dao.CardsDao;

public class CardsServiceImpl implements CardsService
{
    private final CardsDao cardsDao;

    @Inject
    public CardsServiceImpl
    (
        CardsDao cardsDao
    )
    {
        this.cardsDao = cardsDao;
    }

    @Override
    public Card get(Long id)
    {
        return cardsDao.get(id);
    }


//    @Override
//    public List<Card> getWithFilters()
//    {
//        return cardsDao.getWithFilters();
//    }
}