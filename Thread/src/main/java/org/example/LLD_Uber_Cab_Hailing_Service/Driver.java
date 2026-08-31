package example.LLD_Uber_Cab_Hailing_Service;

import lombok.Getter;

@Getter
public class Driver {

    private final int id;
    private final Cab cab;
    private final double rating;

    // volatile: unlocked matching scans read these without the driver lock.
    // Stale AVAILABLE is safe (we recheck under the lock). Stale location only
    // affects ranking, never double-assign. Writes still happen under synchronized(this).
    private volatile DriverStatus driverStatus;
    private volatile Location location;

    public Driver(int id, Cab cab, Location location, double rating) {
        if (cab == null || location == null) {
            throw new IllegalArgumentException("Cab and location are required");
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.id = id;
        this.cab = cab;
        this.location = location;
        this.rating = rating;
        this.driverStatus = DriverStatus.AVAILABLE;
        // Rating is a matching tie-break for a premium product: same ETA, higher
        // rated driver wins. It is immutable here so two threads cannot disagree
        // on sort order while assigning.
    }

    void markBusy() {
        this.driverStatus = DriverStatus.BUSY;
    }

    void markAvailable() {
        this.driverStatus = DriverStatus.AVAILABLE;
    }

    void markOffline() {
        this.driverStatus = DriverStatus.OFFLINE;
    }

    void moveTo(Location newLocation) {
        this.location = newLocation;
    }
}
