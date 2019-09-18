package controllers;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import play.libs.concurrent.HttpExecutionContext;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;
import play.mvc.Result;
import play.libs.Json;

import services.IndexService;


public class IndexController extends BaseController
{
    private final IndexService indexService;
    private final HttpExecutionContext httpExecutionContext;
    private final Config config;

    @Inject
    public IndexController
    (
        IndexService indexService,
        HttpExecutionContext httpExecutionContext,
        Config config
    )
    {
        this.indexService = indexService;

        this.httpExecutionContext = httpExecutionContext;

        this.config = config;
    }

    public CompletionStage<Result> index()
    {
        return CompletableFuture.supplyAsync(() -> {
            return Json.toJson(this.config.getObject("play.modules.enabled")).toString();
//            return indexService.index();
        }, httpExecutionContext.current()).thenApplyAsync(response -> {
            return ok(Json.toJson(response));
        }, httpExecutionContext.current());
    }
}