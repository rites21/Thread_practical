package example.LLD_Uber_Cab_Hailing_Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import example.LLD_Uber_Cab_Hailing_Service.Repositories.DriverRepository;
import example.LLD_Uber_Cab_Hailing_Service.Repositories.RideRepository;
import example.LLD_Uber_Cab_Hailing_Service.Strategies.NearestDriverStrategy;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        DriverRepository driverRepository = new DriverRepository();
        RideRepository rideRepository = new RideRepository();
        // 15 units: far PREMIUM at (40,40) is out of range so we do not burn
        // a long-haul driver on a local pickup. Expand radius as a follow-up.
        RideService rideService = new RideService(
                driverRepository,
                rideRepository,
                new NearestDriverStrategy(15));

        Driver nearestPremium = new Driver(1, new Cab(101, CabType.PREMIUM), new Location(10, 10.4), 4.5);
        Driver secondPremium = new Driver(2, new Cab(102, CabType.PREMIUM), new Location(11, 11), 4.9);
        Driver farPremium = new Driver(3, new Cab(103, CabType.PREMIUM), new Location(40, 40), 5.0);
        Driver closerMini = new Driver(4, new Cab(104, CabType.MINI), new Location(10, 10.85), 5.0);
        Driver black = new Driver(5, new Cab(105, CabType.BLACK), new Location(12, 12), 4.7);
        // MINI sits closer to pickup (10,11) than any PREMIUM. Matching must still
        // skip it — product promise beats raw distance.

        rideService.registerDriver(nearestPremium);
        rideService.registerDriver(secondPremium);
        rideService.registerDriver(farPremium);
        rideService.registerDriver(closerMini);
        rideService.registerDriver(black);

        Location pickup = new Location(10, 11);
        Location destination = new Location(50, 50);

        demoNearestPremiumIgnoresMini(rideService, pickup, destination);
        demoConcurrentRidersGetDifferentDrivers(rideService, pickup, destination);
        demoCompleteFreesDriver(rideService, pickup, destination);
        demoSameCustomerDoubleTapRejected(rideService, pickup, destination);
        demoCancelReturnsDriverToPool(rideService, pickup, destination);
        demoBlackCanFulfillPremium(rideService);

        System.out.println("\nAll demos passed.");
    }

    private static void demoNearestPremiumIgnoresMini(
            RideService rideService,
            Location pickup,
            Location destination) {

        Customer customer = new Customer(1, "Ritesh", pickup);
        Ride ride = rideService.requestRide(customer, pickup, destination, CabType.PREMIUM);
        expect("nearest premium, not the closer MINI", ride.getDriverId() == 1);
        expect("never assigned the MINI cab", ride.getDriverId() != 4);
        expect("cab id persisted", ride.getCabId() == 101);
        expect("assigned PREMIUM", ride.getAssignedCabType() == CabType.PREMIUM);
        rideService.cancelRide(ride.getRideId());
        System.out.println("PASS nearest-premium ignores closer MINI, got driver " + ride.getDriverId());
        // MINI at (10.1,10.1) is closer than driver 2 but must not take a
        // PREMIUM request. That is the product constraint the prompt named.
    }

    private static void demoConcurrentRidersGetDifferentDrivers(
            RideService rideService,
            Location pickup,
            Location destination) throws InterruptedException {

        Customer a = new Customer(10, "Asha", pickup);
        Customer b = new Customer(11, "Bala", pickup);

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);
        AtomicReference<Ride> rideA = new AtomicReference<>();
        AtomicReference<Ride> rideB = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();

        pool.submit(() -> runAtStart(start, done, error, () ->
                rideA.set(rideService.requestRide(a, pickup, destination, CabType.PREMIUM))));
        pool.submit(() -> runAtStart(start, done, error, () ->
                rideB.set(rideService.requestRide(b, pickup, destination, CabType.PREMIUM))));

        start.countDown();
        expect("both concurrent requests finished", done.await(5, TimeUnit.SECONDS));
        pool.shutdownNow();
        if (error.get() != null) {
            throw new AssertionError("concurrent request failed", error.get());
        }

        expect("both rides assigned", rideA.get() != null && rideB.get() != null);
        expect("no double-book of one driver",
                rideA.get().getDriverId() != rideB.get().getDriverId());
        expect("unique ride ids", rideA.get().getRideId() != rideB.get().getRideId());
        expect("both drivers BUSY",
                rideService.getDriver(rideA.get().getDriverId()).getDriverStatus() == DriverStatus.BUSY
                        && rideService.getDriver(rideB.get().getDriverId()).getDriverStatus() == DriverStatus.BUSY);

        rideService.cancelRide(rideA.get().getRideId());
        rideService.cancelRide(rideB.get().getRideId());
        System.out.println("PASS concurrent riders got drivers "
                + rideA.get().getDriverId() + " and " + rideB.get().getDriverId());
        // Two threads, same pickup, latch so they overlap. If check-then-act
        // were split, both would take driver 1.
    }

    private static void demoCompleteFreesDriver(
            RideService rideService,
            Location pickup,
            Location destination) {

        Customer customer = new Customer(20, "Chetna", pickup);
        Ride ride = rideService.requestRide(customer, pickup, destination, CabType.PREMIUM);
        int driverId = ride.getDriverId();
        rideService.startRide(ride.getRideId());
        rideService.completeRide(ride.getRideId());
        rideService.completeRide(ride.getRideId());
        // Second complete is a no-op. Idempotency is required for retries.

        expect("driver available after complete",
                rideService.getDriver(driverId).getDriverStatus() == DriverStatus.AVAILABLE);
        expect("driver moved to drop-off",
                rideService.getDriver(driverId).getLocation().equals(destination));
        expect("fare charged on assigned type", rideService.getRide(ride.getRideId()).getFare() > 0);

        Ride next = rideService.requestRide(new Customer(21, "Dev", pickup), pickup, destination, CabType.PREMIUM);
        rideService.cancelRide(next.getRideId());
        System.out.println("PASS complete frees driver " + driverId + " and computes fare");
    }

    private static void demoSameCustomerDoubleTapRejected(
            RideService rideService,
            Location pickup,
            Location destination) throws InterruptedException {

        Customer customer = new Customer(30, "Esha", pickup);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);
        Set<Integer> assigned = ConcurrentHashMap.newKeySet();
        AtomicInteger failures = new AtomicInteger();
        AtomicReference<Throwable> unexpected = new AtomicReference<>();

        Runnable click = () -> runAtStart(start, done, unexpected, () -> {
            try {
                Ride ride = rideService.requestRide(customer, pickup, destination, CabType.PREMIUM);
                assigned.add(ride.getRideId());
            } catch (CabHailingException e) {
                failures.incrementAndGet();
            }
        });

        pool.submit(click);
        pool.submit(click);
        start.countDown();
        expect("double-tap finished", done.await(5, TimeUnit.SECONDS));
        pool.shutdownNow();
        if (unexpected.get() != null) {
            throw new AssertionError("double-tap failed unexpectedly", unexpected.get());
        }

        expect("exactly one ride for one customer", assigned.size() == 1);
        expect("the other click was rejected", failures.get() == 1);

        int rideId = assigned.iterator().next();
        rideService.cancelRide(rideId);
        System.out.println("PASS same-customer double-tap: one ride, one rejection");
    }

    private static void demoCancelReturnsDriverToPool(
            RideService rideService,
            Location pickup,
            Location destination) {

        Ride ride = rideService.requestRide(
                new Customer(40, "Farhan", pickup), pickup, destination, CabType.PREMIUM);
        int driverId = ride.getDriverId();
        rideService.cancelRide(ride.getRideId());
        expect("driver available after cancel",
                rideService.getDriver(driverId).getDriverStatus() == DriverStatus.AVAILABLE);

        Ride again = rideService.requestRide(
                new Customer(41, "Geeta", pickup), pickup, destination, CabType.PREMIUM);
        expect("cancelled driver can be matched again", again.getDriverId() == driverId);
        rideService.cancelRide(again.getRideId());
        System.out.println("PASS cancel returns driver " + driverId + " to the pool");
    }

    private static void demoBlackCanFulfillPremium(RideService rideService) {
        Location farPickup = new Location(12, 12);
        Location dest = new Location(13, 13);
        Ride ride = rideService.requestRide(
                new Customer(50, "Hari", farPickup), farPickup, dest, CabType.PREMIUM);
        expect("BLACK may upgrade a PREMIUM request", ride.getDriverId() == 5);
        expect("assigned type is BLACK", ride.getAssignedCabType() == CabType.BLACK);
        rideService.cancelRide(ride.getRideId());
        System.out.println("PASS BLACK fulfills PREMIUM as an upgrade");
        // Pickup sits on the BLACK cab. Nearest PREMIUM cars are at (10,10)/(11,11)
        // which are still in radius, but (12,12) is closer — BLACK wins on distance
        // and is allowed to fulfill PREMIUM.
    }

    private static void runAtStart(
            CountDownLatch start,
            CountDownLatch done,
            AtomicReference<Throwable> error,
            Runnable action) {
        try {
            start.await();
            action.run();
        } catch (Throwable t) {
            error.compareAndSet(null, t);
        } finally {
            done.countDown();
        }
    }

    private static void expect(String label, boolean condition) {
        if (!condition) {
            throw new AssertionError("FAILED: " + label);
        }
    }
}
