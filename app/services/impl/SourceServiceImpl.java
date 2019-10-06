package services.impl;

import com.google.inject.Inject;
import dao.CardSourceMapDao;
import dao.SourceDao;
import models.SourceCardMap;
import models.Source;
import play.libs.Json;
import requests.CardsFilterRequest;
import requests.SourceCardMapFilterRequest;
import requests.SourceFilterRequest;
import requests.SourceRequest;
import responses.CardFilterResponse;
import responses.SourceResponse;
import services.CardsService;
import services.SourceService;
import utils.Logger;
import utils.ThreadUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceServiceImpl implements SourceService
{
    private final CardSourceMapDao cardSourceMapDao;
    private final SourceDao sourceDao;

    private final CardsService cardsService;

    private final ThreadUtils threadUtils;
    private final Logger logger;

    @Inject
    public SourceServiceImpl
    (
        CardSourceMapDao cardSourceMapDao,
        SourceDao sourceDao,

        CardsService cardsService,

        ThreadUtils threadUtils,
        Logger logger
    )
    {
        this.cardSourceMapDao = cardSourceMapDao;
        this.sourceDao = sourceDao;

        this.cardsService = cardsService;

        this.threadUtils = threadUtils;
        this.logger = logger;
    }

    @Override
    public SourceResponse create(SourceRequest request)
    {
        SourceResponse sourceResponse = null;
        Source source = new Source();
        source.setName(request.getName());
        source.setQuantity(request.getQuantity());
        source.setType(request.getType());
        source.setCreatedAt(new Date());

        Date expiry = null;
        if(null != request.getExpiry())
        {
            try
            {
                expiry = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(request.getExpiry());
            }
            catch(Exception ex)
            {
                String sh = "sh";
            }
        }

        source.setExpiry(expiry);

        Source createdSource = this.sourceDao.save(source);

        if(null != createdSource.getId())
        {
            List<SourceCardMap> sourceCardMaps = new ArrayList<>();

            for(Long cardId: request.getCards())
            {
                SourceCardMap sourceCardMap = new SourceCardMap();
                sourceCardMap.setCardId(cardId);
                sourceCardMap.setSourceId(createdSource.getId());

                sourceCardMaps.add(sourceCardMap);
            }

            List<SourceCardMap> sourceCardMapList = this.cardSourceMapDao.save(sourceCardMaps);

            sourceResponse = new SourceResponse(createdSource, sourceCardMapList);
        }

        return sourceResponse;
    }

    @Override
    public SourceResponse update(SourceRequest request)
    {
        SourceResponse sourceResponse = null;
        Source existingSource = this.sourceDao.getById(request.getId());
        if(null != existingSource)
        {
            boolean isSourceSaveRequired = false;

            if(null != request.getName())
            {
                isSourceSaveRequired = true;
                existingSource.setName(request.getName());
            }

            if(null != request.getQuantity())
            {
                existingSource.setQuantity(request.getQuantity());
                isSourceSaveRequired = true;
            }

            if(null != request.getExpiry())
            {
                try
                {
                    Date expiry = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(request.getExpiry());
                    existingSource.setExpiry(expiry);
                    isSourceSaveRequired = true;
                }
                catch(Exception ex)
                {
                    String sh = "sh";
                }
            }

            if(isSourceSaveRequired)
            {
                existingSource = this.sourceDao.save(existingSource);
            }

            SourceCardMapFilterRequest cardMapFilterRequest = new SourceCardMapFilterRequest();
            cardMapFilterRequest.setId(existingSource.getId());

            List<SourceCardMap> existingCards = this.cardSourceMapDao.get(cardMapFilterRequest);
            sourceResponse = new SourceResponse(existingSource, existingCards);

            if(null != request.getCards())
            {
                List<Long> existingCardIds = new ArrayList<>();
                List<SourceCardMap> updatedCards = new ArrayList<>();
                List<SourceCardMap> cardsToRemove = new ArrayList<>();
                List<SourceCardMap> cardsToAdd = new ArrayList<>();
                for(SourceCardMap existingCard: existingCards)
                {
                    Long cardId = existingCard.getCardId();
                    existingCardIds.add(cardId);

                    if(request.getCards().contains(cardId))
                    {
                        updatedCards.add(existingCard);
                    }
                    else
                    {
                        cardsToRemove.add(existingCard);
                    }
                }

                for(Long cardId: request.getCards())
                {
                    if(!existingCardIds.contains(cardId))
                    {
                        SourceCardMap cardMap = new SourceCardMap();
                        cardMap.setCardId(cardId);
                        cardMap.setSourceId(existingSource.getId());

                        cardsToAdd.add(cardMap);
                        updatedCards.add(cardMap);
                    }
                }

                if(!cardsToAdd.isEmpty())
                {
                    this.cardSourceMapDao.save(cardsToAdd);
                }

                if(!cardsToRemove.isEmpty())
                {
                    this.logger.debug(Json.toJson(request).toString());
                    this.logger.debug(Json.toJson(cardsToRemove).toString());
                    this.cardSourceMapDao.delete(cardsToRemove);

                    this.threadUtils.schedule(() -> cardsService.indexCards(cardsToRemove));
                }

                sourceResponse.setCards(updatedCards);
            }
        }

        return sourceResponse;
    }

    @Override
    public boolean obtain(Long sourceId)
    {
        boolean isSuccess = false;

        Source existingSource = this.sourceDao.getById(sourceId, false);
        if(null != existingSource)
        {
            Integer newQuantity = existingSource.getQuantity() + 1;
            existingSource.setQuantity(newQuantity);

            existingSource = this.sourceDao.save(existingSource);
            isSuccess = (newQuantity.equals(existingSource.getQuantity()));
        }

        return isSuccess;
    }

    @Override
    public boolean redeem(Long sourceId)
    {
        boolean isSuccess = false;

        Source existingSource = this.sourceDao.getById(sourceId);
        if(null != existingSource)
        {
            if(existingSource.getQuantity() > 0)
            {
                Integer newQuantity = existingSource.getQuantity() - 1;
                existingSource.setQuantity(newQuantity);

                existingSource = this.sourceDao.save(existingSource);
                isSuccess = (newQuantity.equals(existingSource.getQuantity()));

                if(isSuccess)
                {

                }
            }
            else
            {
                isSuccess = true;
            }
        }

        return isSuccess;
    }

    @Override
    public SourceResponse get(Long sourceId)
    {
        SourceResponse sourceResponse = null;

        Source existingSource = this.sourceDao.getById(sourceId);
        if(null != existingSource)
        {
            SourceCardMapFilterRequest request = new SourceCardMapFilterRequest();
            request.setId(sourceId);

            CardsFilterRequest filterRequest = new CardsFilterRequest();
            Map<String, List<String>> filters = new HashMap<>();
            filters.put("sources", Collections.singletonList(sourceId.toString()));
            filterRequest.setFilters(filters);

            CardFilterResponse cardFilterResponse = this.cardsService.getWithFilters(filterRequest);

            sourceResponse = new SourceResponse(existingSource, cardFilterResponse.getCards());
        }

        return sourceResponse;
    }

    @Override
    public List<Source> getAll()
    {
        return this.sourceDao.get(new SourceFilterRequest());
    }
}
