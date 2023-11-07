package genetics;

import schedule.Activity;
import schedule.Room;
import schedule.ScheduleConstants;
import schedule.Time;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GeneticUtils {

    private static final Random random = new Random();

    public static double calculateFitness(GeneticInformation targetGeneticInformation, Individual individual) {
        double fitnessScore = 0;
        List<GeneticInformation> remainingGeneticInformation = remainderOfGenes(targetGeneticInformation, individual);

        //Activity is scheduled at the same time in the same room as another of the activities: -0.5
        if(remainingGeneticInformation.stream().anyMatch(geneticInformation -> activitySame(geneticInformation, targetGeneticInformation))) {
            fitnessScore -= 0.5;
        }

        /*
        Room size:
	    Activities is in a room too small for its expected enrollment: -0.5
	    Activities is in a room with capacity > 3 times expected enrollment: -0.2
	    Activities is in a room with capacity > 6 times expected enrollment: -0.4
	    Otherwise + 0.3
         */
        if(targetGeneticInformation.getActivity().expectedEnrollment() > targetGeneticInformation.getRoom().capacity()) {
            fitnessScore -= 0.5;
        }
        else if(targetGeneticInformation.getRoom().capacity() > 6 * targetGeneticInformation.getActivity().expectedEnrollment()) {
            fitnessScore -= 0.4;
        }
        else if(targetGeneticInformation.getRoom().capacity() > 3 * targetGeneticInformation.getActivity().expectedEnrollment()) {
            fitnessScore -= 0.2;
        }
        else {
            fitnessScore += 0.3;
        }
        if(targetGeneticInformation.getActivity().preferredFacilitators().contains(targetGeneticInformation.getFacilitator())) {
            fitnessScore += 0.5;
        }
        else if(targetGeneticInformation.getActivity().otherFacilitators().contains(targetGeneticInformation.getFacilitator())) {
            fitnessScore += 0.2;
        }
        else {
            fitnessScore -= 0.1;
        }

        // Activity facilitator is scheduled for only 1 activity in this time slot: + 0.2
        if(remainingGeneticInformation.stream().noneMatch(geneticInformation -> activitiesSameTimePlusFac(geneticInformation, targetGeneticInformation))) {
            fitnessScore += 0.2;
        }
        // Activity facilitator is scheduled for more than one activity at the same time: - 0.2
        else  {
            fitnessScore -= 0.2;
        }

        long scheduledActivityCountForFacilitator = remainingGeneticInformation.stream()
                .filter(geneticInformation -> geneticInformation.getFacilitator().equals(targetGeneticInformation.getFacilitator()))
                .count() + 1; //The plus one is because this is just remaining genes. Target gene also has one.

        // Facilitator is scheduled to oversee more than 4 activities total: -0.5
        if(scheduledActivityCountForFacilitator > 4) {
            fitnessScore -= 0.5;
        }
        // Facilitator is scheduled to oversee 1 or 2 activities*: -0.4
        // Exception: Dr. Tyler is committee chair and has other demands on his time.
        // No penalty if he’s only required to oversee < 2 activities.
        else if(scheduledActivityCountForFacilitator < 3 && !targetGeneticInformation.getFacilitator().equals("Tyler")) {
            fitnessScore -= 0.4;
        }

        return fitnessScore;
    }

    public static List<GeneticInformation> remainderOfGenes(GeneticInformation targetGeneticInformation, Individual individual) {
        return individual.geneList()
                .stream()
                .filter(geneticInformation -> targetGeneticInformation != geneticInformation)
                .collect(Collectors.toList());
    }

    public static boolean areAnyTimesConsecutive(List<GeneticInformation> geneticInformationList) {
        int bound = geneticInformationList.size();
        for (int idx = 0; idx < bound; idx++) {
            int i = idx;
            int bound1 = geneticInformationList.size() - 1;
            for (int j = i + 1; j < bound1; j++) {
                if (geneticInformationList.get(i).getTime().differenceBetween(geneticInformationList.get(j).getTime()) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<GeneticInformation> getConsistantTimes(List<GeneticInformation> geneticInformationList) {
        for(int i = 0; i < geneticInformationList.size(); i++) {
            for(int j = i + 1; j < geneticInformationList.size() - 1; j++) {
                if(geneticInformationList.get(i).getTime().differenceBetween(geneticInformationList.get(j).getTime()) == 1) {
                    return List.of(geneticInformationList.get(i), geneticInformationList.get(j));
                }
            }
        }
        return Collections.emptyList();
    }

    public static boolean changesOccurred(double mutationRate) {
        return (random.nextInt(1, (int)(1.0 / mutationRate))) == 1;
    }

    public static GeneticInformation createRandomGene(Activity activity) {
        return new GeneticInformation(activity, getRandomRoom(), getRandomTime(), getRandomFacilitator());
    }

    public static GeneticInformation getGeneByName(String name, Individual individual) {
        if(!ScheduleConstants.ACTIVITY_NAMES.contains(name)) {
            throw new IllegalArgumentException("The name '" + name + "' is not a valid activity name");
        }
        return individual.geneList()
                .stream()
                .filter(geneticInformation -> geneticInformation.getActivity().name().equals(name))
                .toList()
                .get(0);
    }

    /*
        A section of SLA 191 and a section of SLA 101 are overseen in consecutive time slots (e.g., 10 AM & 11 AM): +0.5
        In this case only (consecutive time slots), one of the activities is in Roman or Beach, and the other isn’t: -0.4
        It’s fine if neither is in one of those buildings, of activity; we just want to avoid having consecutive activities being widely separated.
         */
    public static boolean roomRequirementsMet(GeneticInformation geneticInformation1, GeneticInformation geneticInformation2, Room room1, Room room2) {
        boolean gene1HasOneOfTargetRooms = geneticInformation1.getRoom().equals(room1) || geneticInformation1.getRoom().equals(room2);
        boolean gene2HasOneOfTargetRooms = geneticInformation2.getRoom().equals(room1) || geneticInformation2.getRoom().equals(room2);

        return gene1HasOneOfTargetRooms && !gene2HasOneOfTargetRooms
                || gene2HasOneOfTargetRooms && !gene1HasOneOfTargetRooms;

    }




    private static Room getRandomRoom() {
        return ScheduleConstants.ROOMS.get(random.nextInt(ScheduleConstants.NUMBER_OF_ROOMS));
    }

    private static Time getRandomTime() {
        return ScheduleConstants.TIMES.get(random.nextInt(ScheduleConstants.NUMBER_OF_TIMES));
    }

    private static String getRandomFacilitator() {
        return ScheduleConstants.FACILITATORS.get(random.nextInt(ScheduleConstants.NUMBER_OF_FACILITATORS));
    }

    public static boolean activitySame(GeneticInformation geneticInformation, GeneticInformation targetGeneticInformation) {
        return geneticInformation.getTime().equals(targetGeneticInformation.getTime()) && geneticInformation.getRoom().equals(targetGeneticInformation.getRoom());
    }

    public static boolean activitiesSameTimePlusFac(GeneticInformation geneticInformation, GeneticInformation targetGeneticInformation) {
        return geneticInformation.getTime().equals(targetGeneticInformation.getTime())
                && geneticInformation.getFacilitator().equals(targetGeneticInformation.getFacilitator());
    }


}
