import java.util.HashSet;
import java.util.Set;


public abstract class Valuation {
	// A class to create and hold valuation functions with desirable properties.

	int no_valuations;
	
	public Valuation(int n) {
		no_valuations = n;
	}
	
	// Obtain our value if we were to win ALL of the items in the provided basket.
	public abstract double getValue(Set<Integer> basket);

	// Obtain our value if we were to obtain only item n.
	public abstract double getValue(int n);
	
	// Obtain the number of items up for grabs
	public int getNoValuations() {
		return no_valuations;
	}
	
	// Get the items as a set
	public Set<Integer> getItems() {
		Set<Integer> items = new HashSet<Integer>();
		
		for (int i = 0; i<no_valuations; i++)
			items.add(i);
		
		return items;
	}
	
	// Get the power set of all items, including the empty set.
	public Set<Set<Integer>> getPowerSetOfitems() {
		return PowerSet.generate(getItems());
	}
	
	// Get info about the valuation, if any
	public abstract String getInfo();
	
	// Print the valuation function
	public void print() {
		for (Set<Integer> basket : this.getPowerSetOfitems()) {
			System.out.print("{");
			
			for (Integer i : basket)
				System.out.print(" " + i);
			
			System.out.println("} ==> $" + this.getValue(basket));
		}
	}
}	
