package example.LLD_Uber_Cab_Hailing_Service;

public enum CabType {
    MINI,
    PREMIUM,
    BLACK;

    public boolean canFulfill(CabType requested) {
        if (this == requested) {
            return true;
        }
        // BLACK may fulfill a PREMIUM request (upgrade). Never the reverse —
        // a MINI must not be assigned to a premium product promise.
        return requested == PREMIUM && this == BLACK;
    }

    public double farePerDistanceUnit() {
        return switch (this) {
            case MINI -> 1.0;
            case PREMIUM -> 2.0;
            case BLACK -> 3.0;
        };
    }
}
