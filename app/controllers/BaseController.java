package controllers;

import play.mvc.Controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BaseController extends Controller
{
    public CompletionStage<Object> computeAsync(Object returnValue)
    {
        return CompletableFuture.supplyAsync(() -> ((null == returnValue) ? "" : returnValue));
    }
}