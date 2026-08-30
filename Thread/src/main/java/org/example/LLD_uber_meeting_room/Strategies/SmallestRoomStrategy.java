package example.LLD_uber_meeting_room.Strategies;

import java.util.List;

import example.LLD_uber_meeting_room.MeetingRoom;

public class SmallestRoomStrategy implements RoomSelectionStrategy {

    @Override
    public MeetingRoom selectRoom(List<MeetingRoom> availableRooms) {

        MeetingRoom selectedRoom = null;

        for (MeetingRoom room : availableRooms) {

            if (selectedRoom == null || room.getCapacity() < selectedRoom.getCapacity()) {

                selectedRoom = room;
            }
        }

        return selectedRoom;
    }
}