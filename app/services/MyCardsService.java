package services;

import models.MyCard;
import requests.MyCardRequest;

import java.util.List;


public interface MyCardsService
{
    MyCard create(MyCardRequest myCardRequest);

    List<MyCard> get(Long id);
}
