package example.LLD_Uber_Cab_Hailing_Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import example.LLD_Uber_Cab_Hailing_Service.Repositories.DriverRepository;
import example.LLD_Uber_Cab_Hailing_Service.Repositories.RideRepository;
import example.LLD_Uber_Cab_Hailing_Service.Strategies.DriverSelectionStrategy;

/**
 * Cab hailing service — lock policy (say this in the interview):
 * <p>
 * Level 0: synchronized on this whole class. Correct, every rider queues.
 * Level 1 (this code): lock customer first, then the chosen driver. Never reverse.
 * Level 2 (follow-up): in-process locks die across JVMs. Use
 * UPDATE drivers SET status=BUSY WHERE id=? AND status=AVAILABLE and rows==1.
 * <p>
 * Inventory unit is the Driver, not a time slot. Check AVAILABLE + mark BUSY
 * + save Ride are one critical section on that driver.
 */
public class RideService {

    private static final int MATCH_ATTEMPTS = 2;

    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;
    private final DriverSelectionStrategy driverSelectionStrategy;
    private final AtomicInteger rideIdGenerator = new AtomicInteger(1);
    private final ConcurrentHashMap<Integer, Object> customerLocks = new ConcurrentHashMap<>();
    // One monitor per customer, not synchronized(Integer). Integer intern/cache
    // can share boxes and deadlock unrelated customers.

    public RideService(DriverRepository driverRepository, RideRepository rideRepository, DriverSelectionStrategy driverSelectionStrategy) {
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
        this.driverSelectionStrategy = driverSelectionStrategy;
    }

    public void registerDriver(Driver driver) {
        driverRepository.registerDriver(driver);
    }

    public Ride requestRide(Customer customer, Location pickup, Location destination, CabType requestedType) {

        validateRequest(customer, pickup, destination, requestedType);

        Object customerLock = lockForCustomer(customer.getCustomerId());
        synchronized (customerLock) {
            if (rideRepository.hasActiveRide(customer.getCustomerId())) {
                throw new CabHailingException("Customer already has an active ride: " + customer.getCustomerId());
                // Double-tap / two tabs. Without this, one person occupies two cabs.
                // Held under the customer lock so two threads for the same rider
                // cannot both pass the check.
            }

            for (int attempt = 0; attempt < MATCH_ATTEMPTS; attempt++) {
                List<Driver> ranked = rankEligibleDrivers(pickup, requestedType);

                for (Driver driver : ranked) {
                    Ride ride = tryAssign(driver, customer, pickup, destination, requestedType);
                    if (ride != null) {
                        return ride;
                    }
                    // Driver lost the race (went BUSY/OFFLINE). Try the next nearest.
                    // Do not hold more than one driver lock — no deadlock.
                }
            }
            // Second attempt re-scans. A completeRide in between can free someone
            // who was missing from the first snapshot. After two misses we fail
            // rather than spin.
        }

        throw new CabHailingException("No " + requestedType + " driver available nearby");
    }

    public void startRide(int rideId) {
        Ride ride = requireRide(rideId);
        Driver driver = requireDriver(ride.getDriverId());

        Object customerLock = lockForCustomer(ride.getCustomerId());
        synchronized (customerLock) {
            synchronized (driver) {
                if (ride.getRideStatus() == RideStatus.IN_PROGRESS) {
                    return;
                }
                if (ride.getRideStatus() != RideStatus.ASSIGNED) {
                    throw new CabHailingException("Ride cannot be started from " + ride.getRideStatus());
                }
                ride.markInProgress();
                rideRepository.save(ride);
                // ASSIGNED → IN_PROGRESS so cancel vs complete have a real FSM.
                // Same lock order as request/complete: customer then driver.
            }
        }
    }

    public void completeRide(int rideId) {
        Ride ride = requireRide(rideId);
        Driver driver = requireDriver(ride.getDriverId());

        Object customerLock = lockForCustomer(ride.getCustomerId());
        synchronized (customerLock) {
            synchronized (driver) {
                if (ride.getRideStatus() == RideStatus.COMPLETED) {
                    return;
                    // Idempotent: retry / double-click must not throw or double-fare.
                }
                if (ride.getRideStatus() == RideStatus.CANCELLED) {
                    throw new CabHailingException("Cancelled ride cannot be completed");
                }
                if (ride.getRideStatus() != RideStatus.IN_PROGRESS && ride.getRideStatus() != RideStatus.ASSIGNED) {
                    throw new CabHailingException("Ride cannot be completed from " + ride.getRideStatus());
                }

                double distance = ride.getPickupLocation().euclideanDistanceTo(ride.getDestination());
                double fare = distance * ride.getAssignedCabType().farePerDistanceUnit();
                ride.markCompleted(fare);

                driver.markAvailable();
                driver.moveTo(ride.getDestination());
                // Driver re-enters the pool at drop-off. Next nearest match is
                // geographically honest instead of teleporting back to the old ping.

                rideRepository.save(ride);
            }
        }
        // Notifications / payments stay OUTSIDE the lock. Holding a driver lock
        // across SMTP or a payment API is the latency bug they look for.
    }

    public void cancelRide(int rideId) {
        Ride ride = requireRide(rideId);
        Driver driver = requireDriver(ride.getDriverId());

        Object customerLock = lockForCustomer(ride.getCustomerId());
        synchronized (customerLock) {
            synchronized (driver) {
                if (ride.getRideStatus() == RideStatus.CANCELLED) {
                    return;
                }
                if (ride.getRideStatus() == RideStatus.COMPLETED) {
                    throw new CabHailingException("Completed ride cannot be cancelled");
                }

                ride.markCancelled();
                driver.markAvailable();
                rideRepository.save(ride);
                // ASSIGNED or IN_PROGRESS both free the driver. Production may
                // still bill IN_PROGRESS; the concurrency rule is the same.
            }
        }
    }

    public void updateDriverLocation(int driverId, Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location is required");
        }
        Driver driver = requireDriver(driverId);
        synchronized (driver) {
            driver.moveTo(location);
        }
        // Location pings must not take the customer lock. Matching reads a
        // volatile snapshot; stale GPS is acceptable, double-assign is not.
    }

    public void setDriverOffline(int driverId) {
        Driver driver = requireDriver(driverId);
        synchronized (driver) {
            if (driver.getDriverStatus() == DriverStatus.BUSY) {
                throw new CabHailingException("Busy driver cannot go offline");
            }
            driver.markOffline();
        }
    }

    public Driver getDriver(int driverId) {
        return driverRepository.getDriver(driverId);
    }

    public Ride getRide(int rideId) {
        return rideRepository.getRide(rideId);
    }

    private Ride tryAssign(Driver driver, Customer customer, Location pickup, Location destination, CabType requestedType) {

        synchronized (driver) {
            if (driver.getDriverStatus() != DriverStatus.AVAILABLE) {
                return null;
            }
            if (!driver.getCab().getCabType().canFulfill(requestedType)) {
                return null;
                // Recheck type under the lock in case the vehicle was swapped.
            }

            driver.markBusy();

            Ride ride = new Ride(rideIdGenerator.getAndIncrement(), customer.getCustomerId(), driver.getId(), driver.getCab().getCabId(), requestedType, driver.getCab().getCabType(), pickup, destination);
            // AtomicInteger: two assigns on different drivers cannot both persist
            // as rideId=1 and clobber ConcurrentHashMap.put.

            rideRepository.save(ride);
            return ride;
            // Recheck + BUSY + save are the same critical section. If the check
            // is outside this lock, two riders both see AVAILABLE and both write.
        }
    }

    private List<Driver> rankEligibleDrivers(Location pickup, CabType requestedType) {
        List<Driver> eligible = new ArrayList<>();
        for (Driver driver : driverRepository.getAvailableDrivers()) {
            if (driver.getCab().getCabType().canFulfill(requestedType)) {
                eligible.add(driver);
            }
        }
        // Type filter lives in the service, not the strategy. Strategy stays a
        // pure ranker (distance / rating). Swapping in "highest rating first"
        // does not re-implement premium vs mini.
        return driverSelectionStrategy.selectDriver(eligible, pickup);
    }

    private Object lockForCustomer(int customerId) {
        return customerLocks.computeIfAbsent(customerId, id -> new Object());
    }

    private Ride requireRide(int rideId) {
        Ride ride = rideRepository.getRide(rideId);
        if (ride == null) {
            throw new CabHailingException("Ride not found: " + rideId);
        }
        return ride;
    }

    private Driver requireDriver(int driverId) {
        Driver driver = driverRepository.getDriver(driverId);
        if (driver == null) {
            throw new CabHailingException("Driver not found: " + driverId);
        }
        return driver;
    }

    private void validateRequest(Customer customer, Location pickup, Location destination, CabType requestedType) {
        if (customer == null || pickup == null || destination == null || requestedType == null) {
            throw new IllegalArgumentException("Customer, pickup, destination and cab type are required");
        }
        if (pickup.equals(destination)) {
            throw new IllegalArgumentException("Pickup and destination must differ");
        }
    }
}
