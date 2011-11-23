import java.util.ArrayList;


// A PriceDistrbution that can be updated using Wellman-style re-normalization

public class DiscreteDistributionWellman extends DiscreteDistribution {
	public DiscreteDistributionWellman(ArrayList<Double> F, double precision) {
		super(F, precision);
	}

	@Override
	public double getProb(int idx, double b) {
		// Wellman defines Pr(p|B) as a normalization given current bid b		
		if (idx >= f.size())
			return 0.0;
		else if (idx < bin(b)) // prices less than b can not possibly happen.
			return 0.0;
		else {
			// Compute sum of remaining possible bids
			double denom = 0;
			
			for (int i = bin(b); i<f.size(); i++)
				denom += f.get(i);
			
			// Return renormalized probability as a function of the remaining possible bids.
			// DEBUG: System.out.println("idx: " + idx + ", b=" + b + ", bin(b)=" + bin(b) + ", F.get(idx)=" + F.get(idx) + ", denom=" + denom);
			return f.get(idx) / denom;
		}
	}
}
