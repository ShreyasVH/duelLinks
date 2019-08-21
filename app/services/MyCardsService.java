package services;

import models.MyCard;
import requests.MyCardRequest;


public interface MyCardsService
{
    MyCard create(MyCardRequest myCardRequest);
}
