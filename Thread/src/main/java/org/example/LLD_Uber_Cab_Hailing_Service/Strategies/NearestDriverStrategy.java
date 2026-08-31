package example.LLD_Uber_Cab_Hailing_Service.Strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import example.LLD_Uber_Cab_Hailing_Service.Driver;
import example.LLD_Uber_Cab_Hailing_Service.Location;

public class NearestDriverStrategy implements DriverSelectionStrategy {

    private final double maxPickupDistance;

    public NearestDriverStrategy(double maxPickupDistance) {
        this.maxPickupDistance = maxPickupDistance;
        // Radius filter is a product rule for premium: do not assign a driver
        // 50km away just because they are the least-far. Dispatch can expand
        // the radius on a retry; that is a new select() call, not a silent
        // global sort of the whole city.
    }

    @Override
    public List<Driver> selectDriver(List<Driver> drivers, Location pickup) {
        List<Driver> inRange = new ArrayList<>();
        for (Driver driver : drivers) {
            if (driver.getLocation().euclideanDistanceTo(pickup) <= maxPickupDistance) {
                inRange.add(driver);
            }
        }

        inRange.sort(Comparator
                .comparingDouble((Driver d) -> d.getLocation().euclideanDistanceTo(pickup))
                .thenComparing(Comparator.comparingDouble(Driver::getRating).reversed())
                .thenComparingInt(Driver::getId));
        // 1) nearest first — core cab-allocation invariant
        // 2) higher rating if distance ties — premium product, deterministic
        // 3) driverId last — HashMap iteration must not decide who gets the trip
        //
        // Return the FULL ranked list, not top-3. A limit of 3 can fail while
        // driver 4 is free. The service walks this list and stops at first
        // successful lock; extra names cost nothing in-memory.

        return inRange;
    }
}
