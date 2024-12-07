package projet_metaheuristique_P1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class GeneticAlgo {

    public static List<BasePopulation.State> generatePopulation(List<Integer> capacities, List<BasePopulation.Item> items, int populationSize) {
        List<BasePopulation.State> population = new ArrayList<>();

        while (population.size() < populationSize) {
            // Generate a random state and add it to the population
            BasePopulation.State state = generateState(capacities, items);
            population.add(state);
        }
        
        
        List<BasePopulation.State> parents = rankBasedSelection(population, 25);

        // Print the resulting population
        
        printPopulation(population);
        System.out.println("******************parents***************\n");
        printPopulation(parents);

        return population;
    }

    private static void printPopulation(List<BasePopulation.State> population) {
        System.out.println("Generated Population:");
        for (BasePopulation.State state : population) {
            System.out.println("State:");
            for (int i = 0; i < state.sacks.size(); i++) {
                List<BasePopulation.Item> sack = state.sacks.get(i);
                System.out.print("Sack " + (i + 1) + ": ");
                for (BasePopulation.Item item : sack) {
                    System.out.print(item.toString() + " ");
                }
                System.out.println();
            }
            System.out.println("Total Weight: " + state.totalWeight);
            System.out.println();
        }
    }

    private static BasePopulation.State generateState(List<Integer> capacities, List<BasePopulation.Item> items) {
        List<List<BasePopulation.Item>> sacks = new ArrayList<>();
        for (int i = 0; i < capacities.size(); i++) {
            sacks.add(new ArrayList<>()); // Initialize each sack as empty
        }

        // Shuffle the list of items
        Collections.shuffle(items);

        // Fill the sacks with shuffled items
        for (BasePopulation.Item item : items) {
           
            for (int sackIndex = 0; sackIndex < capacities.size(); sackIndex++) {
                int capacity = capacities.get(sackIndex);
                List<BasePopulation.Item> sack = sacks.get(sackIndex);
                if (canFit(capacity, sack, item)) {
                    sack.add(item);
                    break;
                    
                }else {
                	continue;
                }
            }
            
        }
        

        // Calculate the total weight of items in each sack
        int totalWeight = 0;
        for (List<BasePopulation.Item> sack : sacks) {
            totalWeight += calculateSackWeight(sack);
        }

        // Create and return the state
        return new BasePopulation.State(sacks, totalWeight, items.size());
    }
    public static List<BasePopulation.State> rankBasedSelection(List<BasePopulation.State> population, int numParents) {
        List<BasePopulation.State> selectedParents = new ArrayList<>();

        // Sort the population based on fitness (ascending order)
        Collections.sort(population, (state1, state2) -> Double.compare(state1.getFitness(), state2.getFitness()));

        // Calculate cumulative probabilities
        int totalRank = population.size() * (population.size() + 1) / 2;
        double[] cumulativeProbabilities = new double[population.size()];
        double cumulativeProbability = 0.0;
        for (int i = 0; i < population.size(); i++) {
            cumulativeProbability += (double) (i + 1) / totalRank;
            cumulativeProbabilities[i] = cumulativeProbability;
        }

        // Perform selection
        Random random = new Random();
        for (int i = 0; i < numParents; i++) {
            double r = random.nextDouble();
            int selectedIdx = 0;
            while (selectedIdx < population.size() && r > cumulativeProbabilities[selectedIdx]) {
                selectedIdx++;
            }
            selectedParents.add(population.get(selectedIdx));
        }

        return selectedParents;
    }
    public static List<BasePopulation.State> performSinglePointCrossover(List<BasePopulation.State> population, double crossoverProbability) {
        List<BasePopulation.State> offspringPopulation = new ArrayList<>();

        for (int i = 0; i < population.size(); i += 2) {
            // Check if there are at least two parents left for crossover
            if (i + 1 < population.size()) {
                BasePopulation.State parent1 = population.get(i);
                BasePopulation.State parent2 = population.get(i + 1);

                // Perform crossover with probability crossoverProbability
                if (Math.random() < crossoverProbability) {
                    // Select a random crossover point (index) within the range of items
                    int crossoverPoint = (int) (Math.random() * parent1.sacks.size());

                    // Create offspring states by swapping the sacks at the crossover point
                    BasePopulation.State offspring1 = createOffspring(parent1, parent2, crossoverPoint);
                    BasePopulation.State offspring2 = createOffspring(parent2, parent1, crossoverPoint);

                    // Add the offspring to the new population
                    offspringPopulation.add(offspring1);
                    offspringPopulation.add(offspring2);
                } else {
                    // If crossover doesn't occur, simply add parents to the offspring population
                    offspringPopulation.add(parent1);
                    offspringPopulation.add(parent2);
                }
            } else {
                // If there's only one parent left, add it to the offspring population
                offspringPopulation.add(population.get(i));
            }
        }

        return offspringPopulation;
    }

    private static BasePopulation.State createOffspring(BasePopulation.State parent1, BasePopulation.State parent2, int crossoverPoint) {
        List<List<BasePopulation.Item>> offspringSacks = new ArrayList<>();

        // Add sacks from parent1 up to the crossover point
        for (int i = 0; i < crossoverPoint; i++) {
            offspringSacks.add(new ArrayList<>(parent1.sacks.get(i)));
        }

        // Add sacks from parent2 after the crossover point
        for (int i = crossoverPoint; i < parent2.sacks.size(); i++) {
            offspringSacks.add(new ArrayList<>(parent2.sacks.get(i)));
        }

        // Calculate the total weight of items in each sack of the offspring
        int totalWeight = 0;
        for (List<BasePopulation.Item> sack : offspringSacks) {
            totalWeight += calculateSackWeight(sack);
        }

        // Create and return the offspring state
        return new BasePopulation.State(offspringSacks, totalWeight, parent1.itemIndex);
    }

    private static boolean canFit(int capacity, List<BasePopulation.Item> sack, BasePopulation.Item item) {
        if (sack == null) return true;
        int sackWeight = calculateSackWeight(sack);
        return sackWeight + item.weight <= capacity;
    }

    
    public static void evaluatePopulation(List<BasePopulation.State> population) {
        for (BasePopulation.State state : population) {
            int totalWeight = calculateTotalWeight(state);
            int totalValue = calculateTotalValue(state);
            state.setTotalWeight(totalWeight);
            state.setTotalValue(totalValue);
        }
    }

    private static int calculateTotalWeight(BasePopulation.State state) {
        int totalWeight = 0;
        for (List<BasePopulation.Item> sack : state.sacks) {
            totalWeight += calculateSackWeight(sack);
        }
        return totalWeight;
    }

    private static int calculateTotalValue(BasePopulation.State state) {
        int totalValue = 0;
        for (List<BasePopulation.Item> sack : state.sacks) {
            totalValue += calculateSackValue(sack);
        }
        return totalValue;
    }

    private static int calculateSackWeight(List<BasePopulation.Item> sack) {
        int weight = 0;
        for (BasePopulation.Item item : sack) {
            weight += item.weight;
        }
        return weight;
    }

    private static int calculateSackValue(List<BasePopulation.Item> sack) {
        int value = 0;
        for (BasePopulation.Item item : sack) {
            value += item.value;
        }
        return value;
    }
}
