package example.LLD_Uber_Cab_Hailing_Service;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class Ride {

    private final int rideId;
    private final int customerId;
    private final int driverId;
    private final int cabId;
    private final CabType requestedCabType;
    private final CabType assignedCabType;
    private final Location pickupLocation;
    private final Location destination;
    private final LocalDateTime bookingTime;

    private volatile RideStatus rideStatus;
    private volatile double fare;

    public Ride(int rideId, int customerId, int driverId, int cabId, CabType requestedCabType, CabType assignedCabType, Location pickupLocation, Location destination) {

        this.rideId = rideId;
        this.customerId = customerId;
        this.driverId = driverId;
        this.cabId = cabId;
        this.requestedCabType = requestedCabType;
        this.assignedCabType = assignedCabType;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.bookingTime = LocalDateTime.now();
        this.rideStatus = RideStatus.ASSIGNED;
        // cabId comes from the driver at assign time. The old constructor did
        // `this.cabId = cabId` on an uninitialized field and always stored 0.
    }

    public boolean isActive() {
        return rideStatus.isActive();
    }

    void markInProgress() {
        this.rideStatus = RideStatus.IN_PROGRESS;
    }

    void markCompleted(double fare) {
        this.rideStatus = RideStatus.COMPLETED;
        this.fare = fare;
    }

    void markCancelled() {
        this.rideStatus = RideStatus.CANCELLED;
    }
}
