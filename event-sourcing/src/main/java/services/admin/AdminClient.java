package services.admin;

import com.eventstore.dbclient.EventData;
import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.EventStoreDBClientSettings;
import com.eventstore.dbclient.EventStoreDBConnectionString;
import database.events.AccountCreated;
import database.events.TicketAdded;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static database.Config.CONNECTION_STRING;

public class AdminService implements AutoCloseable {
    private EventStoreDBClient client;

    public void start() {
        EventStoreDBClientSettings setts = EventStoreDBConnectionString.parseOrThrow(CONNECTION_STRING);
        client = EventStoreDBClient.create(setts);
    }

    @Override
    public void close() throws Exception {
        client = null;
    }

    public String addTicket(Integer userId, Date from, Date to) {
        TicketAdded createdEvent = new TicketAdded();
        createdEvent.setFrom(from);
        createdEvent.setTo(to);

        if (!from.before(to)) {
            return "Invalid dates";
        }

        EventData event = EventData
                .builderAsJson("ticket-added", createdEvent)
                .build();

        try {
            return client.appendToStream("tickets", event)
                    .get().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public String addUser(Integer id, String name) {
        AccountCreated createdEvent = new AccountCreated();
        createdEvent.setId(id);
        createdEvent.setName(name);

        EventData event = EventData
                .builderAsJson("account-created", createdEvent)
                .build();

        try {
            return client.appendToStream("accounts", event)
                    .get().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
