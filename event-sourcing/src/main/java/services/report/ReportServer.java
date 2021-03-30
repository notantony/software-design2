package services.report;

import com.eventstore.dbclient.*;
import database.DBManager;
import database.RoutesConfig;
import database.events.AccountCreated;
import database.events.TicketAdded;
import database.events.TicketDiscarded;
import services.QueryProcessingError;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReportClient {
    private final RoutesConfig config;
    private final EventStoreDBClient client;
    Map<Integer, String> names;

    public ReportClient(RoutesConfig config) {
        names = new HashMap<>();
        client = config.buildClient();

        client.subscribeToStream(config.getVisitsStream(), new StatisticsListener(), SubscribeToStreamOptions.get().fromStart());
        this.config = config;
    }

    public void pullAll() throws QueryProcessingError {
        new DBManager(config).accountsCreated()
                .forEachOrdered(accountCreated -> names.put(accountCreated.getId(), accountCreated.getName()));

    }

    public AccountCreated getAccountCreated(int userId) throws QueryProcessingError {
        List<AccountCreated> accountCreatedSeq = new DBManager(config).accountsCreated()
                .filter(client -> client.getId().equals(userId))
                .collect(Collectors.toList());

        if (accountCreatedSeq.size() == 0) {
            throw new QueryProcessingError("User not found");
        } else if (accountCreatedSeq.size() > 1) {
            Logger.getGlobal().warning("User id is ambiguous, please contact the administrator");
        }

        return accountCreatedSeq.get(accountCreatedSeq.size() - 1);
    }

    public boolean checkEnter(Integer userId, Date now) throws QueryProcessingError {
        List<RecordedEvent> recordedEvents = new DBManager(config).ticketActions().collect(Collectors.toList());

        try {
            Map<String, TicketAdded> tickets = new HashMap<>();
            for (RecordedEvent recordedEvent : recordedEvents) {
                if (recordedEvent.getEventType().equals("ticket-added")) {
                    TicketAdded ticketAdded = recordedEvent.getEventDataAs(TicketAdded.class);
                    if (!ticketAdded.getUserId().equals(userId)) {
                        continue;
                    }
                    tickets.put(ticketAdded.getTicketId(), ticketAdded);
                } else if (recordedEvent.getEventType().equals("ticket-discarded")) {
                    TicketDiscarded ticketDiscarded = recordedEvent.getEventDataAs(TicketDiscarded.class);
                    tickets.remove(ticketDiscarded.getTicketId());
                    // TODO: Check if ticket exists?
                }
            }
            return tickets.values().stream()
                    .anyMatch(ticketAdded -> ticketAdded.getFrom().before(now) && ticketAdded.getTo().after(now));

        } catch (IOException e) {
            e.printStackTrace();
            throw new QueryProcessingError("Internal server error", e);
        }
    }
}
