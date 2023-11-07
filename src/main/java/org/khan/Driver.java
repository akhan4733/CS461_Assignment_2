package org.khan;

import genetics.GeneticConstants;
import genetics.Population;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Driver {
    public static void main(String[] args) throws IOException {
        Population population = new Population();

        for(int i = 0; i < GeneticConstants.GENERATION_COUNT; i++) {
            population.runGeneration();
            population.printBestIndividualInformation();
        }

        double previousAverageFitnessScore;
        do {
            previousAverageFitnessScore = population.getAverageFitness();
            population.runGeneration();
            population.printBestIndividualInformation();
        } while (population.getAverageFitness() > 1.01 * previousAverageFitnessScore);


        Files.writeString(Paths.get("output.txt"), population.getBestIndividual().toPrettyString());
    }
}