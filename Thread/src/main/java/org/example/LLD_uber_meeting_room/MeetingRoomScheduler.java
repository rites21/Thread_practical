package example.LLD_uber_meeting_room;

import example.LLD_uber_meeting_room.Strategies.RoomSelectionStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class MeetingRoomScheduler {

    private final RoomSelectionStrategy roomSelectionStrategy;
    private final RoomRepository roomRepo;
    private final BookingRepository bookingRepository;

    // One lock for each room
    private final Map<Integer, ReentrantLock> roomLocks = new HashMap<>();

    private final AtomicInteger bookingIdGenerator = new AtomicInteger(1);

    public MeetingRoomScheduler(RoomSelectionStrategy roomSelectionStrategy, RoomRepository roomRepo, BookingRepository bookingRepository) {

        this.roomSelectionStrategy = roomSelectionStrategy;
        this.roomRepo = roomRepo;
        this.bookingRepository = bookingRepository;
    }

    public void addRoom(MeetingRoom room) {

        roomRepo.addRoom(room);

        roomLocks.put(room.getRoomId(), new ReentrantLock());
    }

    // ---------------------------------------------------
    // BOOK ROOM
    // ---------------------------------------------------

    public Booking bookRoom(int userId, LocalDateTime startTime, LocalDateTime endTime, long requiredCapacity) {

        validateInput(startTime, endTime, requiredCapacity);

        /*
         * First find rooms that satisfy capacity
         * and appear to be available.
         */
        List<MeetingRoom> availableRooms = findAvailableRooms(startTime, endTime, requiredCapacity);

        /*
         * We may have multiple rooms.
         *
         * Strategy decides which one we prefer.
         */
        while (!availableRooms.isEmpty()) {

            MeetingRoom selectedRoom = roomSelectionStrategy.selectRoom(availableRooms);

            if (selectedRoom == null) {
                return null;
            }

            ReentrantLock lock = roomLocks.get(selectedRoom.getRoomId());

            lock.lock();

            try {

                /*
                 * IMPORTANT:
                 *
                 * This is the second availability check.
                 *
                 * The first check happened before we acquired
                 * the lock. Another thread could have booked
                 * the room in between.
                 */
                if (!isRoomAvailable(selectedRoom.getRoomId(), startTime, endTime)) {

                    /*
                     * Someone booked this room.
                     *
                     * Remove it and try another room.
                     */
                    availableRooms.remove(selectedRoom);

                    continue;
                }

                /*
                 * Room is still available.
                 *
                 * Now check + create + save happen while
                 * holding the same room lock.
                 */
                Booking booking = new Booking(bookingIdGenerator.getAndIncrement(), selectedRoom.getRoomId(), userId, requiredCapacity, startTime, endTime);

                bookingRepository.save(booking);

                System.out.println("Booked room: " + selectedRoom.getRoomName());

                return booking;

            } finally {

                lock.unlock();
            }
        }

        return null;
    }

    // ---------------------------------------------------
    // FIND AVAILABLE ROOMS
    // ---------------------------------------------------

    private List<MeetingRoom> findAvailableRooms(LocalDateTime startTime, LocalDateTime endTime, long requiredCapacity) {

        List<MeetingRoom> availableRooms = new ArrayList<>();

        for (MeetingRoom room : roomRepo.getAllRooms()) {

            if (room.getCapacity() < requiredCapacity) {
                continue;
            }

            if (isRoomAvailable(room.getRoomId(), startTime, endTime)) {

                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    // ---------------------------------------------------
    // CAPACITY FILTER
    // ---------------------------------------------------

    public List<MeetingRoom> filterByCapacity(long requiredCapacity) {

        List<MeetingRoom> result = new ArrayList<>();

        for (MeetingRoom room : roomRepo.getAllRooms()) {

            if (room.getCapacity() >= requiredCapacity) {
                result.add(room);
            }
        }

        return result;
    }

    // ---------------------------------------------------
    // AVAILABILITY
    // ---------------------------------------------------

    private boolean isRoomAvailable(int roomId, LocalDateTime startTime, LocalDateTime endTime) {

        List<Booking> bookings = bookingRepository.getBookingsByRoom(roomId);

        for (Booking booking : bookings) {

            if (booking.getStatus() == BookingStatus.CANCELLED) {

                continue;
            }

            boolean overlap = booking.getStartTime().isBefore(endTime) && booking.getEndTime().isAfter(startTime);

            if (overlap) {
                return false;
            }
        }

        return true;
    }

    // ---------------------------------------------------
    // CANCEL
    // ---------------------------------------------------

    public boolean cancelBooking(int bookingId) {

        Booking booking = bookingRepository.getBooking(bookingId);

        if (booking == null) {
            return false;
        }

        ReentrantLock lock = roomLocks.get(booking.getRoomId());

        lock.lock();

        try {

            /*
             * Idempotent cancellation.
             *
             * Calling cancel twice doesn't create a
             * second state change.
             */
            if (booking.getStatus() == BookingStatus.CANCELLED) {

                return true;
            }

            booking.setStatus(BookingStatus.CANCELLED);

            return true;

        } finally {

            lock.unlock();
        }
    }

    // ---------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------

    private void validateInput(LocalDateTime startTime, LocalDateTime endTime, long requiredCapacity) {

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end time are required");
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (requiredCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
    }

    public Booking bookRoomSimpleOne(int userId, LocalDateTime startTime, LocalDateTime endTime, long requiredCapacity) {

        List<MeetingRoom> availableRooms = findAvailableRooms(startTime, endTime, requiredCapacity);

        while (!availableRooms.isEmpty()) {

            MeetingRoom selectedRoom = roomSelectionStrategy.selectRoom(availableRooms);

            synchronized (selectedRoom) {

                // SECOND CHECK
                if (!isRoomAvailable(selectedRoom.getRoomId(), startTime, endTime)) {

                    availableRooms.remove(selectedRoom);
                    continue;
                }

                Booking booking = new Booking(bookingIdGenerator.getAndIncrement(), selectedRoom.getRoomId(), userId, requiredCapacity, startTime, endTime);

                bookingRepository.save(booking);

                return booking;
            }
        }

        return null;
    }

}