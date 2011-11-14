// Implements a perceived price bidder who uses "Straightforward Biding" logic.

public class StraightforwardPPAgent extends PerceivedPriceAgent {

	public StraightforwardPPAgent(int agent_idx, Valuation valuation) {
		super(agent_idx, valuation);
	}

	@Override
	protected double[] rho() {
		double[] prices = new double[valuation.getNoValuations()];
		
		// set prices[i] = current price, if winning good, or ask price, otherwise
		
		for (int i = 0; i<valuation.getNoValuations(); i++)
			prices[i] = results.get(i).getIsWinner() ? results.get(i).getPayment() : results.get(i).getAskPrice(); 
			
		return prices;
	}

}
