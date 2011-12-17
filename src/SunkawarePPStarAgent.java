import java.util.ArrayList;

// Implements a Sunk Aware percevied price star bidder

// There is currently no difference between this agent and a SunkawarePPAgent,
// other than this agent is a sub-class of PerceivedPriceStarAgent

public class SunkawarePPStarAgent extends PerceivedPriceStarAgent {
	double k;
	
	// where k determines the amount of sunk-awareness. k=0 is fully sunk-aware, k=1 is
	// equiv. to straightforward bidding
	
	// first constructor: required PrOfBids
	public SunkawarePPStarAgent(int agent_idx, Valuation valuation, ArrayList<Double> PrOfBids, double k) {
		super(agent_idx, valuation, PrOfBids);
		this.k = k;
	}
	
	// second constructor: assumes PrOfBids == 0
	public SunkawarePPStarAgent(int agent_idx, Valuation valuation, double k) {
		super(agent_idx, valuation);
		this.k = k;
	}

	@Override
	protected double[] rho() {
		double[] prices = new double[valuation.getNoValuations()];
		
		// set prices[i] = k*current_price, if winning good, or ask price, otherwise
		
		for (int i = 0; i<valuation.getNoValuations(); i++)
			prices[i] = results.get(i).getIsWinner() ? k*results.get(i).getPayment() : results.get(i).getAskPrice(); 
			
		return prices;
	}
}
