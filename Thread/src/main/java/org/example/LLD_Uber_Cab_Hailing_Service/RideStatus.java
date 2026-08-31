package example.LLD_Uber_Cab_Hailing_Service;

public enum RideStatus {
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public boolean isActive() {
        return this == ASSIGNED || this == IN_PROGRESS;
        // Active means the customer still occupies a cab. COMPLETED/CANCELLED
        // must free both the driver and the customer's "one trip at a time" slot.
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
