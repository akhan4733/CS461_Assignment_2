package schedule;

public class ScheduleUtils {

    public static Room getRoomByName(String name) {
        if(!ScheduleConstants.ROOM_NAMES.contains(name)) {
            throw new IllegalArgumentException("The name '" + name + "' is not a valid room name");
        }
        return ScheduleConstants.ROOMS
                .stream()
                .filter(room -> room.name().equals(name))
                .toList()
                .get(0);
    }
}
