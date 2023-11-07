package genetics;

import genetics.math.Picker;
import genetics.math.SoftMax;
import schedule.Activity;
import schedule.ScheduleConstants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Population {

    private int generationCount;
    private final List<Individual> population;

    public Population() {
        population = new ArrayList<>(GeneticConstants.POP_SIZE + 10);
        generationCount = 0;
        generateInitialPopulation();
    }

    public void printBestIndividualInformation() {
        System.out.println("_______________________________________________________");
        System.out.println("Best Individual from generation: " + generationCount);
        System.out.println(population.get(0));
        System.out.println("Fitness Score: " + population.get(0).getFitness());
        System.out.println("_______________________________________________________");
    }

    public double getAverageFitness(){
        double total = 0;
        for(Individual individual : population){
            total += individual.getFitness();
        }
        return total / population.size();
    }
    public void runGeneration() {
        if(generationCount == 0) {
            rankInitialPopulation();
        }
        cullHalfPopulation();

        reproduceHalfPopulation();

        rankPopulation();

        generationCount++;
    }
    public Individual getBestIndividual() {
        return population.get(0);
    }

    private void reproduceHalfPopulation() {
        assignChromosomesMatingProbabilities();
        Picker<Individual> picker = new Picker<>(population);

        int targetPopulation = population.size() * 2;
        while(population.size() != targetPopulation) {
            picker.reset();
            Individual individual1 = picker.pick(0);
            Individual individual2 = picker.pick(1);

            List<Individual> offspring = individual1.crossoverWith(individual2);

            offspring.forEach(Individual::attemptMutation);
            population.addAll(offspring);

        }
    }

    private void rankInitialPopulation() {
        population.parallelStream().forEach(Individual::calculateFitness);
        population.sort(Comparator.comparingDouble(Individual::getFitness).reversed());
    }
    private void rankPopulation() {
        population.subList(population.size() / 2, population.size())
                .parallelStream()
                .forEach(Individual::calculateFitness);
        population.sort(Comparator.comparingDouble(Individual::getFitness).reversed());
    }

    private void cullHalfPopulation() {
        int cutoffIndex = population.size() / 2;
        population.subList(cutoffIndex, population.size()).clear();
    }

    private void assignChromosomesMatingProbabilities() {
        List<Double> normalizedFitnessScores = SoftMax.calculateSoftMax(
                population.stream().map(Individual::getFitness).toList()
        );

        for(int i = 0; i < normalizedFitnessScores.size(); i++) {
            population.get(i).setMatingProbability(normalizedFitnessScores.get(i));
        }
    }

    private void generateInitialPopulation() {
        for(int i = 0; i < GeneticConstants.POP_SIZE; i++) {
            List<GeneticInformation> geneticInformationList = new ArrayList<>();
            for(Activity activity : ScheduleConstants.ACTIVITIES) {
                geneticInformationList.add(GeneticUtils.createRandomGene(activity));
            }
            population.add(new Individual(geneticInformationList));
        }
    }

}
