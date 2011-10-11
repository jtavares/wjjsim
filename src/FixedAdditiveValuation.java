
public class FixedAdditiveValuation extends AdditiveValuation {
	// Values are user-provided and fixed. Baskets are straight additive, per AdditiveValuation superclass.

	// Additive valuation function whereby individual item values
	// are all fixed to the same single value. Baskets are straight additive.
	public FixedAdditiveValuation(int n, double value) {
		super(n);
		
		for (int i = 0; i<no_valuations; i++)
			values[i] = value;
	}	
	
	// Additive valuation function whereby individual item values are provided
	// by the user in an array of doubles. Values are copied from the array;
	// an object reference to the user array is not kept. Baskets are
	// straight additive.
	public FixedAdditiveValuation(double[] values) {
		super(values.length);
		
		for (int i = 0; i<no_valuations; i++)
			this.values[i] = values[i];
	}
}
