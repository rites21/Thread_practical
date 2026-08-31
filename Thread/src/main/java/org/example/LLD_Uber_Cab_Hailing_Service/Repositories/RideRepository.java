package example.LLD_Uber_Cab_Hailing_Service.Repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import example.LLD_Uber_Cab_Hailing_Service.Ride;

public class RideRepository {

    private final ConcurrentMap<Integer, Ride> rides = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Integer> activeRideByCustomer = new ConcurrentHashMap<>();
    // Second index: O(1) "does this customer already have a trip?" without
    // scanning every ride. Updated only from RideService while holding the
    // customer lock, so the index and the ride row stay aligned.

    public void save(Ride ride) {
        rides.put(ride.getRideId(), ride);
        if (ride.isActive()) {
            activeRideByCustomer.put(ride.getCustomerId(), ride.getRideId());
        } else {
            activeRideByCustomer.remove(ride.getCustomerId(), ride.getRideId());
            // remove(key, value) is conditional: a newer ride for the same
            // customer is not wiped if this terminal write is late.
        }
    }

    public Ride getRide(int rideId) {
        return rides.get(rideId);
    }

    public boolean hasActiveRide(int customerId) {
        return activeRideByCustomer.containsKey(customerId);
    }

    public List<Ride> getAllRides() {
        return new ArrayList<>(rides.values());
    }
}
