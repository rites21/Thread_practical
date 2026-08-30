package example.LLD_uber_meeting_room;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingRoom {

    private int roomId;
    private String roomName;
    private long capacity;

    public MeetingRoom(int roomId, String roomName, long capacity) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
    }
}