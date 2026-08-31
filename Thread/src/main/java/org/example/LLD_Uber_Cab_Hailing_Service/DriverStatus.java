package example.LLD_Uber_Cab_Hailing_Service;

public enum DriverStatus {
    AVAILABLE,
    BUSY,
    OFFLINE
    // OFFLINE is distinct from BUSY so a driver who went off-shift is not
    // treated as "on a trip" and is never a matching candidate.
}
