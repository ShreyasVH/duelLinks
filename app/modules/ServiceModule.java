package modules;

import com.google.inject.AbstractModule;

import services.*;

import services.impl.*;


public class ServiceModule extends AbstractModule
{
    @Override
    public void configure()
    {
        bind(IndexService.class).to(IndexServiceImpl.class).asEagerSingleton();
        bind(CardsService.class).to(CardsServiceImpl.class).asEagerSingleton();
        bind(ElasticService.class).to(ElasticServiceImpl.class).asEagerSingleton();
        bind(MyCardsService.class).to(MyCardsServiceImpl.class).asEagerSingleton();
        bind(SourceService.class).to(SourceServiceImpl.class).asEagerSingleton();
    }
}