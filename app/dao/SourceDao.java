package dao;

import com.google.inject.Inject;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.ExpressionList;
import models.Source;
import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;
import requests.SourceFilterRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SourceDao
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;

    @Inject
    public SourceDao
    (
            EbeanConfig ebeanConfig,
            EbeanDynamicEvolutions ebeanDynamicEvolutions
    )
    {
        this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
        this.db = Ebean.getServer(ebeanConfig.defaultServer());
    }

    public Source save(Source source)
    {
        try
        {
            this.db.save(source);
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }
        return source;
    }

    public ExpressionList<Source> filter(SourceFilterRequest request)
    {
        ExpressionList<Source> expressionList = this.db.find(Source.class).where();

        if(null != request.getId())
        {
            expressionList.eq("id", request.getId());
        }

        if(request.getIncludeQuantityCheck())
        {
            expressionList.ne("quantity", 0);
        }

        if(request.getIncludeExpiryCheck())
        {
            expressionList.ge("expiry", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
        }

        expressionList.order("expiry ASC, id ASC");

        return expressionList;
    }

    public Source getById(Long id)
    {
        return this.getById(id, true);
    }

    public Source getById(Long id, Boolean includeQuantityCheck)
    {
        Source source = null;
        SourceFilterRequest request = new SourceFilterRequest();
        request.setId(id);
        request.setIncludeQuantityCheck(includeQuantityCheck);
        List<Source> sources = this.get(request);
        if(!sources.isEmpty())
        {
            source = sources.get(0);
        }

        return source;
    }

    public List<Source> get(SourceFilterRequest request)
    {
        List<Source> sources = new ArrayList<>();

        try
        {
            ExpressionList<Source> expressionList = this.filter(request);
            sources = expressionList.findList();
            String sh = "sh";
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return sources;
    }
}
