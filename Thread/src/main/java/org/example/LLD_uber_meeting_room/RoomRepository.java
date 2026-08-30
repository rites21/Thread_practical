package example.LLD_uber_meeting_room;

import java.util.List;

public interface RoomRepository {

    void addRoom(MeetingRoom room);

    MeetingRoom getRoom(int roomId);

    List<MeetingRoom> getAllRooms();
}