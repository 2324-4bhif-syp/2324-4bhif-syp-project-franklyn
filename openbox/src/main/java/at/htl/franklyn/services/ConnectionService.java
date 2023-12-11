package at.htl.franklyn.services;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import jakarta.inject.Singleton;

@Singleton
public class ConnectionService implements Scheduled.SkipPredicate {
    private boolean isConnected = false;

    public boolean test(ScheduledExecution execution) {
        Log.infof("CHECKING CONNECTION %b", this.isConnected);
        return this.isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
