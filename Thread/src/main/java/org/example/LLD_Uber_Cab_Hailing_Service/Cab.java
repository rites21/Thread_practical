package example.LLD_Uber_Cab_Hailing_Service;

import lombok.Getter;

@Getter
public class Cab {

    private final int cabId;
    private final CabType cabType;

    public Cab(int cabId, CabType cabType) {
        if (cabType == null) {
            throw new IllegalArgumentException("Cab type is required");
        }
        this.cabId = cabId;
        this.cabType = cabType;
        // Immutable after construct: matching must not see a cab flip MINI → BLACK
        // mid-request. Change vehicle by registering a new Cab on the driver
        // under the driver lock, not by mutating this object.
    }
}
