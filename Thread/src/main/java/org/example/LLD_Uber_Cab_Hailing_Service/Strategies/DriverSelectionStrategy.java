package example.LLD_Uber_Cab_Hailing_Service.Strategies;

import java.util.List;

import example.LLD_Uber_Cab_Hailing_Service.Driver;
import example.LLD_Uber_Cab_Hailing_Service.Location;

public interface DriverSelectionStrategy {

    /**
     * Returns eligible drivers nearest-first. Empty if nobody is in range.
     * Ranking only — the caller still rechecks availability under the driver lock.
     */
    List<Driver> selectDriver(List<Driver> drivers, Location pickupLocation);
}
