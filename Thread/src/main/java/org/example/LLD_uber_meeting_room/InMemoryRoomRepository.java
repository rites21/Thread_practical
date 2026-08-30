package example.LLD_uber_meeting_room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryRoomRepository implements RoomRepository {

    private final Map<Integer, MeetingRoom> roomMap = new HashMap<>();

    @Override
    public void addRoom(MeetingRoom room) {
        roomMap.put(room.getRoomId(), room);
    }

    @Override
    public MeetingRoom getRoom(int roomId) {
        return roomMap.get(roomId);
    }

    @Override
    public List<MeetingRoom> getAllRooms() {
        return new ArrayList<>(roomMap.values());
    }
}