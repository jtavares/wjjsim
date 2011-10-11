import java.util.Set;


public abstract class AdditiveValuation extends Valuation {
	// Represents a valuation function where values of baskets are simply summations of the values of their items.

	double[] values;
	
	public AdditiveValuation(int n) {
		super(n);
		
		values = new double[no_valuations];
	}
		
	@Override
	public double getValue(Set<Integer> basket) {
		double total_value = 0.0;

		for (Integer i : basket)
			total_value += getValue(i);
		
		return total_value;
	}

	@Override
	public double getValue(int n) {
		return values[n];
	}
}
