package controllers;

import com.google.inject.Inject;
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


    @Inject
    public IndexController
    (
        IndexService indexService,
        HttpExecutionContext httpExecutionContext
    )
    {
        this.indexService = indexService;

        this.httpExecutionContext = httpExecutionContext;
    }

    public CompletionStage<Result> index()
    {
        return CompletableFuture.supplyAsync(() -> {
            return indexService.index();
        }, httpExecutionContext.current()).thenApplyAsync(response -> {
            return ok(Json.toJson(response));
        }, httpExecutionContext.current());
    }
}