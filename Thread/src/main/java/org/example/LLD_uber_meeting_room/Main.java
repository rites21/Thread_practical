package example.LLD_uber_meeting_room;

import example.LLD_uber_meeting_room.Strategies.RoomSelectionStrategy;
import example.LLD_uber_meeting_room.Strategies.SmallestRoomStrategy;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        RoomRepository roomRepository = new InMemoryRoomRepository();

        BookingRepository bookingRepository = new InMemoryBookingRepository();

        RoomSelectionStrategy strategy = new SmallestRoomStrategy();

        MeetingRoomScheduler scheduler = new MeetingRoomScheduler(strategy, roomRepository, bookingRepository);

        // Add rooms through scheduler
        scheduler.addRoom(new MeetingRoom(1, "Room-1", 4));

        scheduler.addRoom(new MeetingRoom(2, "Room-2", 8));

        scheduler.addRoom(new MeetingRoom(3, "Room-3", 12));

        LocalDateTime start = LocalDateTime.of(2026, 8, 30, 10, 0);

        LocalDateTime end = LocalDateTime.of(2026, 8, 30, 11, 0);

        System.out.println("\n--- BOOKING 1 ---");

        Booking booking1 = scheduler.bookRoom(101, start, end, 6);

        System.out.println(booking1);

        System.out.println("\n--- BOOKING 2 ---");

        Booking booking2 = scheduler.bookRoom(102, start, end, 6);

        System.out.println(booking2);

        System.out.println("\n--- CANCEL BOOKING 1 ---");

        scheduler.cancelBooking(booking1.getBookingId());

        System.out.println(booking1);

        System.out.println("\n--- BOOKING 3 ---");

        Booking booking3 = scheduler.bookRoom(103, start, end, 6);

        System.out.println(booking3);
    }
}