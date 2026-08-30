package example.LLD_uber_meeting_room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBookingRepository implements BookingRepository {

    private final Map<Integer, List<Booking>> bookingMap = new HashMap<>();

    private final Map<Integer, Booking> bookingIdMap = new HashMap<>();

    @Override
    public void save(Booking booking) {

        bookingMap.computeIfAbsent(booking.getRoomId(), k -> new ArrayList<>()).add(booking);

        bookingIdMap.put(booking.getBookingId(), booking);
    }

    @Override
    public List<Booking> getBookingsByRoom(int roomId) {
        return bookingMap.getOrDefault(roomId, new ArrayList<>());
    }

    @Override
    public Booking getBooking(int bookingId) {
        return bookingIdMap.get(bookingId);
    }

    @Override
    public List<MeetingRoom> getMeetingRoomsByRoom() {
        return List.of();
    }
}