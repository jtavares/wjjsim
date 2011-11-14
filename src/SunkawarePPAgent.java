// Implements a Sunk Aware percevied price bidder

public class SunkawarePPAgent extends PerceivedPriceAgent {
	double k;
	
	// where k determines the amount of sunk-awareness. k=0 is fully sunk-aware, k=1 is
	// equiv. to straightforward bidding
	public SunkawarePPAgent(int agent_idx, Valuation valuation, double k) {
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
