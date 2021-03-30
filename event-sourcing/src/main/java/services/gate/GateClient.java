package services.gate;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import com.eventstore.dbclient.EventStoreDBConnectionString;

import static database.Config.CONNECTION_STRING;

public class GateService implements AutoCloseable {
    private EventStoreDBClient client;

    public void start() {
        EventStoreDBClientSettings setts = EventStoreDBConnectionString.parseOrThrow(CONNECTION_STRING);
        client = EventStoreDBClient.create(setts);
    }

    @Override
    public void close() throws Exception {
        client = null;
    }
}
