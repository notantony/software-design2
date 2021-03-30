package database.events;

import java.util.Date;


public class CheckpointVisited {
    public enum VisitType {ENTER, EXIT}

    private Integer userId;
    private Date when;
    private VisitType type;

    public Integer getUserId() {
        return userId;
    }

    public Date getWhen() {
        return when;
    }

    public VisitType getType() {
        return type;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public void setType(VisitType type) {
        this.type = type;
    }
}
