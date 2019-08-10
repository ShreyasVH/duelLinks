package customContexts;

import akka.actor.ActorSystem;
import com.google.inject.Inject;
import play.api.libs.concurrent.CustomExecutionContext;

public class DatabaseExecutionContext extends CustomExecutionContext
{
    @Inject
    public DatabaseExecutionContext(ActorSystem actorSystem)
    {
        super(actorSystem, "database.dispatcher");
    }
}
