import java.util.List;


public class RandomAgent extends Agent {
	// An agent that bids randomly in auctions, regardless of his true value.
	
	public RandomAgent(int agent_idx, Valuation valuation) {
		super(agent_idx, valuation);
	}

	@Override
	public void roundResult(List<Result> results) {
		// Random agent does not care about past results.
	}

	@Override
	public double[] getBids() {
		// random agent always just bids a random number, irrespective of valuations.
		double[] bid_list = new double[valuation.getNoValuations()];

		for (int k = 0; k < valuation.getNoValuations(); k++) 
			bid_list[k] = Math.random();

		return bid_list;
	}
}
