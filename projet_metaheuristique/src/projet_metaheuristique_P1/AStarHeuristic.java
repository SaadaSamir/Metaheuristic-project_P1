package projet_metaheuristique_P1;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import projet_metaheuristique_P1.AStarAlgo.Item;
import projet_metaheuristique_P1.AStarAlgo.State;

public class AStarHeuristic {
	
	static class Sack {
	    private int capacity;
	    private List<Item> content;
	    private int totalValue;
	    private int totalWeight;

	    public int calculateTotalValue() {
	        totalValue = 0;
	        for (Item item : content) {
	            totalValue += item.getValue();
	        }
	        return totalValue;
	    }

	    public int calculateTotalWeight() {
	        totalWeight = 0;
	        for (Item item : content) {
	            totalWeight += item.getWeight();
	        }
	        return totalWeight;
	    }

	    public int calculateRemainingCapacity() {
	        return capacity - calculateTotalWeight();
	    }

	    public void setCapacity(int capa) {
	        capacity = capa;
	    }

	    public void setContent(List<Item> c) {
	        content = c;
	    }

	    // Add getters for totalValue, totalWeight, and capacity if needed
	}


    public static double g(State state) {
        return state.getTotalValue();
    }

    public static double h(State state, List<Item> items, List<Integer> capacities) {
        // Sort items in descending order of profit/weight ratio
        List<Item> sortedItems = sortItemsByProfitWeightRatio(items);
        
        List<Sack> sacks = new ArrayList<>();
        
        List<List<Item>> sackContents = state.getSacks();
        for (int i = 0; i < sackContents.size(); i++) {
            Sack sack = new Sack();
            sack.setContent(sackContents.get(i));
                  
            sack.setCapacity(capacities.get(i));
            sack.calculateTotalValue();
            sack.calculateTotalWeight();
            sack.calculateRemainingCapacity();
            System.out.println("sacks number: "+ sackContents.size()+" sack capacity: " + capacities.get(i) + "V: " +sack.calculateTotalValue()+" W: "+sack.calculateTotalWeight()+" CAPA: "+sack.calculateRemainingCapacity()); 
            sacks.add(sack);
        }
        

        // Sort sacks in ascending order of total weight
        
       
        Collections.sort(sacks, new Comparator<Sack>() {
            @Override
            public int compare(Sack s1, Sack s2) {
                return Integer.compare(s1.capacity, s2.capacity);
            }
        });

        int totalValue = 0;

        for (Sack sack : sacks) {
            int remainingCapacity = sack.calculateRemainingCapacity();

            for (Item item : sortedItems) {
                if (item.weight <= remainingCapacity) {
                    totalValue += item.value;
                    remainingCapacity -= item.weight;
                }
            }
        }
        System.out.println("total val : "+ totalValue);
        return totalValue;
    }

    private static List<Item> sortItemsByProfitWeightRatio(List<Item> items) {
        items.sort((item1, item2) -> {
            double ratio1 = (double) item1.getValue() / item1.getWeight();
            double ratio2 = (double) item2.getValue() / item2.getWeight();
            return Double.compare(ratio2, ratio1); // Reversed order
        });
        return items;
    }

    
}
