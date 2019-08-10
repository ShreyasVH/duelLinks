package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.Controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BaseController extends Controller
{
    public CompletionStage<Object> computeAsync(Object returnValue)
    {
        return CompletableFuture.supplyAsync(() -> ((null == returnValue) ? "" : returnValue));
    }

    public JsonNode formatResponse(Object returnValue)
    {
        return ((null == returnValue) ? (Json.parse("")) : Json.toJson(returnValue));
    }
}