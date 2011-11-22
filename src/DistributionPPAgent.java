import java.util.ArrayList;


// implements an SAA bidder who uses a distribution to produce /incremental/
// prices ala Wellman, section 4.2, page 11.

public class DistributionPPAgent extends PerceivedPriceAgent {
	ArrayList<DiscreteDistribution> pd;
	
	public DistributionPPAgent(int agent_idx, Valuation valuation, ArrayList<DiscreteDistribution> pd) {
		super(agent_idx, valuation);
		this.pd = pd;
	}

	@Override
	protected double[] rho() {
		double[] prices = new double[valuation.getNoValuations()];
		
		// set prices[i] = current price, if winning good, or ask price, otherwise
		
		for (int i = 0; i<valuation.getNoValuations(); i++) {
			Result r = results.get(i);
			double cur = r.getCurPrice();
			DiscreteDistribution p = pd.get(i);
			
			if (r.getIsWinner()) {
				// if winning: (prob. final price is higher) * (expected final price knowing we have to out-bid the new winner).
				prices[i] = (1 - p.getProb(cur, cur)) * p.getExpectedFinalPrice(cur + 2*r.getAskEpsilon());
			} else {
				// if losing: expected final price, conditional on price being at least equal to ask price (the lowest it could be
				// if were to take the item)
				prices[i] = p.getExpectedFinalPrice(r.getAskPrice());
			}
		}
			
		return prices;
	}

}
