package example.LLD_uber_meeting_room.Strategies;

import java.util.List;

import example.LLD_uber_meeting_room.MeetingRoom;

public interface RoomSelectionStrategy {

    MeetingRoom selectRoom(List<MeetingRoom> availableRooms);
}