package database.events;

import java.util.Date;
import java.util.Random;

public class TicketAdded {
    private String ticketId;
    private Integer userId;
    private Date from, to;

    public TicketAdded() {
    }

    public Integer getUserId() {
        return userId;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
