package example.LLD_Uber_Cab_Hailing_Service;

public record Location(double latitude, double longitude) {

    public double euclideanDistanceTo(Location other) {
        double dx = latitude - other.latitude;
        double dy = longitude - other.longitude;
        return Math.sqrt(dx * dx + dy * dy);
        // Euclidean is enough for an in-memory LLD. Production matching uses
        // road ETA (or at least haversine) so "nearest" means fastest, not
        // straight-line. Same DriverSelectionStrategy, different distance fn.
    }
}
