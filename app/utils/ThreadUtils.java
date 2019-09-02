package utils;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class ThreadUtils
{
    private final ActorSystem actorSystem;
    private final ExecutionContext playActorContext;

    @Inject
    public ThreadUtils
    (
        ActorSystem actorSystem
    )
    {
        this.actorSystem = actorSystem;
        this.playActorContext = actorSystem.dispatcher();
    }

    public void schedule(Runnable runnable)
    {
        actorSystem.scheduler().scheduleOnce(Duration.create(5, TimeUnit.MILLISECONDS), runnable, this.playActorContext);
    }
}
