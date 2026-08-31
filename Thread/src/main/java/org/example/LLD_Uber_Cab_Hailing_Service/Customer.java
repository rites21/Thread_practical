package example.LLD_Uber_Cab_Hailing_Service;

import lombok.Getter;

@Getter
public class Customer {

    private final int customerId;
    private final String customerName;
    private final Location location;

    public Customer(int customerId, String customerName, Location location) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.location = location;
    }
}
