package database.processors;

import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.ReadStreamOptions;
import com.eventstore.dbclient.RecordedEvent;
import com.eventstore.dbclient.ResolvedEvent;
import database.events.AccountCreated;
import services.QueryProcessingError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountProcessor {
    private final EventStoreDBClient client;

    public AccountProcessor(EventStoreDBClient client) {
        this.client = client;
    }

    public Stream<AccountCreated> accountsCreated() throws QueryProcessingError {
        ReadStreamOptions options = ReadStreamOptions.get()
                .forwards()
                .fromStart();

        List<ResolvedEvent> accounts;
        try {
            accounts = client.readStream("accounts", options).get().getEvents();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new QueryProcessingError("Internal server error", e);
        }

        try {
            List<RecordedEvent> accountCreatedEvents = accounts.stream()
                    .map(ResolvedEvent::getOriginalEvent)
                    .filter(event -> event.getEventType().equals("account-created"))
                    .collect(Collectors.toList());

            List<AccountCreated> createdResults = new ArrayList<>();
            for (RecordedEvent event : accountCreatedEvents) {
                createdResults.add(event.getEventDataAs(AccountCreated.class));
            }
            return createdResults.stream();
        } catch (IOException e) {
            throw new QueryProcessingError("Internal server error", e);
        }
    }

    public Stream<RecordedEvent> ticketActions() throws QueryProcessingError {
        ReadStreamOptions options = ReadStreamOptions.get()
                .forwards()
                .fromStart();

        List<ResolvedEvent> ticketsEvents;
        try {
            ticketsEvents = client.readStream("tickets", options).get().getEvents();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new QueryProcessingError("Internal server error", e);
        }

        return ticketsEvents.stream()
                .map(ResolvedEvent::getOriginalEvent);
    }
}
