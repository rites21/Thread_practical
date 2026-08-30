package example.LLD_uber_meeting_room;

import java.util.List;

public interface BookingRepository {

    void save(Booking booking);

    List<Booking> getBookingsByRoom(int roomId);

    Booking getBooking(int bookingId);

    List<MeetingRoom> getMeetingRoomsByRoom();
}