package dao;

import com.google.inject.Inject;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.ExpressionList;
import models.SourceCardMap;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;
import requests.SourceCardMapFilterRequest;

import java.util.ArrayList;
import java.util.List;

public class CardSourceMapDao
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;

    @Inject
    public CardSourceMapDao
    (
        EbeanConfig ebeanConfig,
        EbeanDynamicEvolutions ebeanDynamicEvolutions
    )
    {
        this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
        this.db = Ebean.getServer(ebeanConfig.defaultServer());
    }

    private ExpressionList<SourceCardMap> filter(SourceCardMapFilterRequest request)
    {
        ExpressionList<SourceCardMap> expressionList = this.db.find(SourceCardMap.class).where();

        if(null != request.getId())
        {
            expressionList.eq("sourceId", request.getId());
        }

        if(null != request.getCardId())
        {
            expressionList.eq("cardId", request.getCardId());
        }

        return expressionList;
    }

    public List<SourceCardMap> get(SourceCardMapFilterRequest request)
    {
        List<SourceCardMap> sourceCardMaps = new ArrayList<>();
        try
        {

            ExpressionList<SourceCardMap> expressionList = this.filter(request);
            sourceCardMaps = expressionList.findList();
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
        return sourceCardMaps;
    }

    public List<SourceCardMap> save(List<SourceCardMap> sourceCardMaps)
    {
        try
        {
            this.db.saveAll(sourceCardMaps);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return sourceCardMaps;
    }

    public boolean delete(List<SourceCardMap> sourceCardMaps)
    {
        boolean isSuccess = false;

        try
        {
            this.db.deleteAll(sourceCardMaps);
            isSuccess = true;
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return isSuccess;
    }
}
