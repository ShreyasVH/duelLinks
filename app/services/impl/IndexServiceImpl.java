package services.impl;

import com.google.inject.Inject;

import dao.IndexDao;
import services.IndexService;

public class IndexServiceImpl implements IndexService
{
    private final IndexDao indexDao;

    @Inject
    public IndexServiceImpl
    (
        IndexDao indexDao
    )
    {
        this.indexDao = indexDao;
    }

    @Override
    public String index()
    {
//        return indexDao.index();
        return System.getenv("play.db.prototype.hikaricp.maximumPoolSize");
    }
}