package dao;

import com.google.inject.Inject;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.RawSql;
import io.ebean.RawSqlBuilder;
import models.Source;

import play.db.ebean.EbeanConfig;
import play.db.ebean.EbeanDynamicEvolutions;
import requests.SourceFilterRequest;
import utils.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SourceDao
{
    private final EbeanServer db;
    private final EbeanDynamicEvolutions ebeanDynamicEvolutions;

    private final Logger logger;

    @Inject
    public SourceDao
    (
            EbeanConfig ebeanConfig,
            EbeanDynamicEvolutions ebeanDynamicEvolutions,

            Logger logger
    )
    {
        this.ebeanDynamicEvolutions = ebeanDynamicEvolutions;
        this.db = Ebean.getServer(ebeanConfig.defaultServer());

        this.logger = logger;
    }

    public Source save(Source source)
    {
        try
        {
            this.db.save(source);
        }
        catch(Exception ex)
        {
            this.logger.error("Exception while saving source. Message: " + ex.getMessage());
        }
        return source;
    }

    public RawSql filter(SourceFilterRequest request)
    {
        String query = "SELECT id, name, type, quantity, expiry, created_at FROM sources WHERE 1";

        if(null != request.getId())
        {
            query += " AND id = " + request.getId();
        }

        if(request.getIncludeQuantityCheck())
        {
            query += " AND quantity > 0";
        }

        if(request.getIncludeExpiryCheck())
        {
            query += " AND (expiry > '" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) +"' OR expiry IS NULL)";
        }
        else
        {
            query += " AND (1 OR expiry is NULL)";
        }

        query += " ORDER BY expiry ASC, id DESC";

        return RawSqlBuilder.parse(query).create();
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
            RawSql sql = this.filter(request);
            sources = this.db.find(Source.class).setRawSql(sql).findList();
        }
        catch(Exception ex)
        {
            String sh = "sh";
        }

        return sources;
    }
}
