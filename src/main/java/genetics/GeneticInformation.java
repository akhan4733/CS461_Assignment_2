package genetics;

import schedule.Activity;
import schedule.Room;
import schedule.ScheduleConstants;
import schedule.Time;

import java.util.Random;

public class GeneticInformation {
    private final Activity activity;
    private Room room;
    private Time time;
    private String facilitator;

    public GeneticInformation(Activity activity, Room room, Time time, String facilitator) {
        this.activity = activity;
        this.room = room;
        this.time = time;
        this.facilitator = facilitator;
    }

    GeneticInformation copyGene() {
        return new GeneticInformation(activity.copy(), room.copy(), time.copy(), facilitator);
    }

    public void mutateFacilitator() {
        Random random = new Random();
        String newFacilitator;
        do {
            newFacilitator = ScheduleConstants.FACILITATORS.get(
                    random.nextInt(ScheduleConstants.NUMBER_OF_FACILITATORS)
            );
        } while (newFacilitator.equals(facilitator));
        facilitator = newFacilitator;
    }

    public void mutateTime() {
        Random random = new Random();
        Time newTime;
        do {
            newTime = ScheduleConstants.TIMES.get(
                    random.nextInt(ScheduleConstants.NUMBER_OF_TIMES)
            );
        } while(newTime.equals(time));
        time = newTime;
    }

    public void mutateRoom() {
        Random random = new Random();
        Room newRoom;
        do {
            newRoom = ScheduleConstants.ROOMS.get(
                    random.nextInt( ScheduleConstants.NUMBER_OF_ROOMS)
            );
        } while (newRoom.equals(room));
        room = newRoom;
    }

    public Activity getActivity() {
        return activity;
    }

    public Room getRoom() {
        return room;
    }

    public Time getTime() {
        return time;
    }

    public String getFacilitator() {
        return facilitator;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "activity=" + activity +
                ", room=" + room +
                ", time=" + time +
                ", facilitator='" + facilitator + '\'' +
                '}';
    }
    public String toPrettyString() {
        return activity.name() + " = {" + "\n" +
                "\t\t" + "Room = " + room.name() + "\n" +
                "\t\t" + "Time = " + time.toPrettyString() + "\n" +
                "\t\t" + "Facilitator = " + facilitator + "\n" +
                "\t}";
    }
}
