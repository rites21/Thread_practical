package example.LLD_uber_meeting_room;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Booking {

    private int bookingId;
    private int roomId;
    private int userId;

    private long requiredCapacity;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private BookingStatus status;

    public Booking(int bookingId, int roomId, int userId, long requiredCapacity, LocalDateTime startTime, LocalDateTime endTime) {

        this.bookingId = bookingId;
        this.roomId = roomId;
        this.userId = userId;
        this.requiredCapacity = requiredCapacity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = BookingStatus.ACTIVE;
    }

    public Booking() {

    }
}