package example.LLD_Uber_Cab_Hailing_Service.Repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import example.LLD_Uber_Cab_Hailing_Service.CabHailingException;
import example.LLD_Uber_Cab_Hailing_Service.Driver;
import example.LLD_Uber_Cab_Hailing_Service.DriverStatus;

public class DriverRepository {

    private final ConcurrentMap<Integer, Driver> drivers = new ConcurrentHashMap<>();
    // ConcurrentHashMap so two threads registering or matching different
    // drivers do not corrupt the map. Per-driver synchronized does NOT
    // protect this structure (lesson from the meeting-room HashMap hole).

    public void registerDriver(Driver driver) {
        Driver existing = drivers.putIfAbsent(driver.getId(), driver);
        if (existing != null) {
            throw new CabHailingException("Driver already registered: " + driver.getId());
        }
        // putIfAbsent keeps the canonical Driver instance stable. We lock on
        // that instance in RideService; replacing it would make synchronized
        // (oldDriver) and synchronized(newDriver) miss each other.
    }

    public Driver getDriver(int driverId) {
        return drivers.get(driverId);
    }

    public List<Driver> getAllDrivers() {
        return new ArrayList<>(drivers.values());
        // Copy so callers cannot mutate the live map values collection.
    }

    public List<Driver> getAvailableDrivers() {
        List<Driver> result = new ArrayList<>();
        for (Driver driver : drivers.values()) {
            if (driver.getDriverStatus() == DriverStatus.AVAILABLE) {
                result.add(driver);
            }
        }
        return result;
        // Unlocked snapshot. A driver may go BUSY before we lock them — that
        // is expected. Recheck under synchronized(driver) is the source of truth.
    }
}
