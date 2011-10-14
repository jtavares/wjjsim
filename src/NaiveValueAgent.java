import java.util.List;


public class NaiveValueAgent extends Agent {
	// An agent that bids an item's unit value; it does not consider effects of compliments or substitutes.
	// In particular, for perfect compliments, this agent will bid 0 on every item, and end up with nothing.
	
	public NaiveValueAgent(int agent_idx, Valuation valuation) {
		super(agent_idx, valuation);
	}

	@Override
	public void roundResult(List<Result> results) {
		// TODO
	}

	@Override
	public double[] getBids() {
		// Naive agent just always bids an item's independent value.
		double[] bid_list = new double[valuation.getNoValuations()];

		for (int k = 0; k < valuation.getNoValuations(); k++) 
			bid_list[k] = valuation.getValue(k);

		return bid_list;
	}
}
