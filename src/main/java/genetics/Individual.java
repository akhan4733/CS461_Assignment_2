package genetics;

import genetics.math.ProbableSelection;
import schedule.Room;
import schedule.ScheduleUtils;

import java.util.*;
import java.util.stream.Stream;

//Genetic representation of a schedule
public final class Individual implements ProbableSelection {
    private final List<GeneticInformation> geneticInformationList;
    private double fitness = -1;
    private double matingProbability = -1; //start off with no possibility to mate
    private final SplittableRandom random;

    public Individual(List<GeneticInformation> geneticInformationList) {
        this.random = new SplittableRandom();
        this.geneticInformationList = geneticInformationList;
    }

    public void attemptMutation(double mutationRate) {
        for (GeneticInformation geneticInformation : geneticInformationList) {
            if (GeneticUtils.changesOccurred(mutationRate)) {
                geneticInformation.mutateFacilitator();
            }
            if (GeneticUtils.changesOccurred(mutationRate)) {
                geneticInformation.mutateRoom();
            }
            if (GeneticUtils.changesOccurred(mutationRate)) {
                geneticInformation.mutateTime();
            }
        }
    }

    public List<Individual> crossoverWith(Individual other) {
        int indexOfDividingLine = random.nextInt(1, GeneticConstants.GENE_COUNT - 1);

        List<GeneticInformation> firstHalf1 = this.splitGeneticInformation(0, indexOfDividingLine);
        List<GeneticInformation> secondHalf1 = other.splitGeneticInformation(indexOfDividingLine, GeneticConstants.GENE_COUNT);
        List<GeneticInformation> firstHalf2 = other.splitGeneticInformation(0, indexOfDividingLine);
        List<GeneticInformation> secondHalf2 = this.splitGeneticInformation(indexOfDividingLine, GeneticConstants.GENE_COUNT);

        Individual individual1 =
                new Individual(Stream.concat(firstHalf1.stream(), secondHalf1.stream()).toList());
        Individual individual2 =
                new Individual(Stream.concat(firstHalf2.stream(), secondHalf2.stream()).toList());

        return List.of(individual1, individual2);
    }
    public String niceFormat() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Schedule = {")
                .append("\n");
        for (GeneticInformation geneticInformation : geneticInformationList) {
            stringBuilder.append("\t").append(geneticInformation.getFormatString()).append("\n");
        }

        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private List<GeneticInformation> splitGeneticInformation(int start, int end) {
        return geneticInformationList.subList(start, end)
                .stream()
                .map(GeneticInformation::copyGene)
                .toList();
    }

    public void attemptMutation() {
        attemptMutation(GeneticConstants.M_RATE);
    }

    public void calculateFitness() {
        double fitnessScore = geneticInformationList
                .stream()
                .mapToDouble(geneticInformation -> GeneticUtils.calculateFitness(geneticInformation, this))
                .sum();



        //The 2 sections of SLA 101 are more than 4 hours apart: + 0.5
        final GeneticInformation SLA101A = GeneticUtils.getGeneByName("SLA101A", this);
        final GeneticInformation SLA101B = GeneticUtils.getGeneByName("SLA101B", this);
        if (SLA101A.getTime().differenceBetween(SLA101B.getTime()) > 4) {
            fitnessScore += 0.5;
        }

        //Both sections of SLA 101 are in the same time slot: -0.5
        else if (SLA101A.getTime().equals(SLA101B.getTime())) {
            fitnessScore -= 0.5;
        }

        //The 2 sections of SLA 191 are more than 4 hours apart: + 0.5
        GeneticInformation SLA191A = GeneticUtils.getGeneByName("SLA191A", this);
        GeneticInformation SLA191B = GeneticUtils.getGeneByName("SLA191B", this);
        if (SLA191A.getTime().differenceBetween(SLA191B.getTime()) > 4) {
            fitnessScore += 0.5;
        }

        //Both sections of SLA 191 are in the same time slot: -0.5
        else if (SLA191A.getTime().equals(SLA191B.getTime())) {
            fitnessScore -= 0.5;
        }

        //4 possible combos: SLA101A with SLA191A, SLA101B with SLA191A, SLA101A with SLA191B, SLA101B with SLA191B
        final Room roman201 = ScheduleUtils.getRoomByName("Roman 201");
        final Room beach201 = ScheduleUtils.getRoomByName("Beach 201");

        if (SLA101A.getTime().differenceBetween(SLA191A.getTime()) == 1) {
            fitnessScore += 0.5;
            if (GeneticUtils.roomRequirementsMet(SLA101A, SLA191A, roman201, beach201)) {
                fitnessScore -= 0.4;
            }
        } else if (SLA101B.getTime().differenceBetween(SLA191A.getTime()) == 1) {
            fitnessScore += 0.5;
            if (GeneticUtils.roomRequirementsMet(SLA101B, SLA191A, roman201, beach201)) {
                fitnessScore -= 0.4;
            }
        } else if (SLA101A.getTime().differenceBetween(SLA191B.getTime()) == 1) {
            fitnessScore += 0.5;
            if (GeneticUtils.roomRequirementsMet(SLA101A, SLA191B, roman201, beach201)) {
                fitnessScore -= 0.4;
            }
        } else if (SLA101B.getTime().differenceBetween(SLA191B.getTime()) == 1) {
            fitnessScore += 0.5;
            if (GeneticUtils.roomRequirementsMet(SLA101B, SLA191B, roman201, beach201)) {
                fitnessScore -= 0.4;
            }
        }


        //A section of SLA 191 and a section of SLA 101 are taught separated by 1 hour (e.g., 10 AM & 12:00 Noon): + 0.25
        else if (SLA101A.getTime().differenceBetween(SLA191A.getTime()) > 1) {
            fitnessScore += 0.25;
        } else if (SLA101B.getTime().differenceBetween(SLA191A.getTime()) > 1) {
            fitnessScore += 0.25;
        } else if (SLA101A.getTime().differenceBetween(SLA191B.getTime()) > 1) {
            fitnessScore += 0.25;
        } else if (SLA101B.getTime().differenceBetween(SLA191B.getTime()) > 1) {
            fitnessScore += 0.25;
        }

        //A section of SLA 191 and a section of SLA 101 are taught in the same time slot: -0.25
        else if (SLA101A.getTime().equals(SLA191A.getTime())) {
            fitnessScore -= 0.25;
        } else if (SLA101B.getTime().equals(SLA191A.getTime())) {
            fitnessScore -= 0.25;
        } else if (SLA101A.getTime().equals(SLA191B.getTime())) {
            fitnessScore -= 0.25;
        } else if (SLA101B.getTime().equals(SLA191B.getTime())) {
            fitnessScore -= 0.25;
        }

        Map<String, List<GeneticInformation>> facilitatorToActivityMap = new HashMap<>();
        for (GeneticInformation geneticInformation : geneticInformationList) {
            if (!facilitatorToActivityMap.containsKey(geneticInformation.getFacilitator())) {
                facilitatorToActivityMap.put(geneticInformation.getFacilitator(), new ArrayList<>(List.of(geneticInformation)));
            } else {
                List<GeneticInformation> geneticInformations = facilitatorToActivityMap.get(geneticInformation.getFacilitator());
                geneticInformations.add(geneticInformation);
                facilitatorToActivityMap.put(geneticInformation.getFacilitator(), geneticInformations);
            }
        }
        List<GeneticInformation> consecutiveGeneticInformations = Collections.emptyList();

        for (var entry : facilitatorToActivityMap.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (GeneticUtils.areAnyTimesConsecutive(entry.getValue())) {
                    consecutiveGeneticInformations = GeneticUtils.getConsistantTimes(entry.getValue());
                }
            }
        }

        if (!consecutiveGeneticInformations.isEmpty()) {
            fitnessScore += 0.5;
            if (GeneticUtils.roomRequirementsMet(
                    consecutiveGeneticInformations.get(0),
                    consecutiveGeneticInformations.get(1),
                    roman201,
                    beach201)) {
                fitnessScore -= 0.4;
            }
        }
        fitness = fitnessScore;
    }

    @Override
    public String toString() {
        return "Chromosome{" +
                "geneList=" + geneticInformationList +
                '}';
    }

    public List<GeneticInformation> geneList() {
        return geneticInformationList;
    }

    public void setMatingProbability(double matingProbability) {
        this.matingProbability = matingProbability;
    }

    public double getFitness() {
        return fitness;
    }

    @Override
    public double calculateLikelyHood() {
        return matingProbability;
    }
}
