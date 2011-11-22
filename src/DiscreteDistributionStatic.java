import java.util.ArrayList;

// a class that represents the price distribution for a single item.

public class DiscreteDistributionStatic extends DiscreteDistribution {
	public DiscreteDistributionStatic(ArrayList<Double> F, double precision) {
		super(F, precision);
	}

	// In a static price distribution, we do not take in account the the current price, b.
	public double getProb(int idx, double b) {
		if (idx >= f.size())
			return 0.0;
		else
			return f.get(idx);
	}
}
