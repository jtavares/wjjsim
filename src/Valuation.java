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
	
	public int getNoValuations() {
		return no_valuations;
	}
}	
