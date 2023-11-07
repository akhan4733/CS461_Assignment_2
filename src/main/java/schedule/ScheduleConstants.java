package schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleConstants {

    public static final List<String> FACILITATORS = List.of(
            "Lock",
            "Glen",
            "Banks",
            "Richards",
            "Shaw",
            "Singer",
            "Uther",
            "Tyler",
            "Numen",
            "Zeldin"
    );

    public static final int NUMBER_OF_FACILITATORS = 10;

    public final static List<String> ACTIVITY_NAMES = List.of(
            "SLA101A",
            "SLA101B",
            "SLA191A",
            "SLA191B",
            "SLA201",
            "SLA291",
            "SLA303",
            "SLA304",
            "SLA394",
            "SLA449",
            "SLA451"
    );

    public final static List<List<String>> PREFERRED_FACILITATORS = List.of(
            List.of("Glen", "Lock", "Banks", "Zeldin"),
            List.of("Glen", "Lock", "Banks", "Zeldin"),
            List.of("Glen", "Lock", "Banks", "Zeldin"),
            List.of("Glen", "Lock", "Banks", "Zeldin"),
            List.of("Glen", "Banks", "Zeldin", "Shaw"),
            List.of("Lock", "Banks", "Zeldin", "Singer"),
            List.of("Glen", "Zeldin", "Banks"),
            List.of("Glen", "Banks", "Tyler"),
            List.of("Tyler", "Singer"),
            List.of("Tyler", "Singer", "Shaw"),
            List.of("Tyler", "Singer", "Shaw")
    );

    public final static List<List<String>> OTHER_FACILITATORS = List.of(
            List.of("Numen", "Richards"),
            List.of("Numen", "Richards"),
            List.of("Numen", "Richards"),
            List.of("Numen", "Richards"),
            List.of("Numen", "Richards", "Singer"),
            List.of("Numen", "Richards", "Shaw", "Tyler"),
            List.of("Numen", "Singer", "Shaw"),
            List.of("Numen", "Singer", "Shaw", "Richards", "Uther", "Zeldin"),
            List.of("Richards", "Zeldin"),
            List.of("Zeldin", "Uther"),
            List.of("Zeldin", "Uther", "Richards", "Banks")
    );

    public final static List<Integer> ENROLLMENTS = List.of(
            50, 50, 50, 50, 50, 50, 60, 25, 20, 60, 100
    );

    public final static List<String> ROOM_NAMES = List.of(
            "Slater 003",
            "Roman 216",
            "Loft 206",
            "Roman 201",
            "Loft 310",
            "Beach 201",
            "Beach 301",
            "Logos 325",
            "Frank 119"
    );

    public final static List<Integer> ROOM_CAPACITIES = List.of(
            45, 30, 75, 50, 108, 60, 75, 450, 60
    );

    public final static List<Room> ROOMS = initRooms();

    public final static List<Time> TIMES = List.of(
            new Time(10, TImeType.AM),
            new Time(11, TImeType.AM),
            new Time(12, TImeType.PM),
            new Time(1, TImeType.PM),
            new Time(2, TImeType.PM),
            new Time(3, TImeType.PM)
    );

    public final static int NUMBER_OF_ROOMS = 9;

    public final static int NUMBER_OF_ACTIVITIES = 11;

    public final static int NUMBER_OF_TIMES = 6;

    public static final List<Activity> ACTIVITIES = initActivities();

    private static List<Room> initRooms() {
        List<Room> roomList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_ROOMS; i++) {
            roomList.add(new Room(ROOM_NAMES.get(i), ROOM_CAPACITIES.get(i)));
        }
        return roomList;
    }

    private static List<Activity> initActivities() {
        List<Activity> activityList = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_ACTIVITIES; i++) {
            String name = ACTIVITY_NAMES.get(i);
            int expectedEnrollment = ENROLLMENTS.get(i);
            List<String> preferredFacilitators = PREFERRED_FACILITATORS.get(i);
            List<String> otherFacilitators = OTHER_FACILITATORS.get(i);

            activityList.add(
                    new Activity(name, expectedEnrollment, preferredFacilitators, otherFacilitators)
            );
        }
        return activityList;
    }

}
