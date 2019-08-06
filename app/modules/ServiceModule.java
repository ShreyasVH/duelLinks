package modules;

import com.google.inject.AbstractModule;

import services.ElasticService;
import services.IndexService;
import services.CardsService;

import services.impl.ElasticServiceImpl;
import services.impl.IndexServiceImpl;
import services.impl.CardsServiceImpl;


public class ServiceModule extends AbstractModule
{
    @Override
    public void configure()
    {
        bind(IndexService.class).to(IndexServiceImpl.class).asEagerSingleton();
        bind(CardsService.class).to(CardsServiceImpl.class).asEagerSingleton();
        bind(ElasticService.class).to(ElasticServiceImpl.class).asEagerSingleton();
    }
}