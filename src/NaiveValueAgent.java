import java.util.HashMap;

public class NaiveValueAgent extends Agent {
	// An agent that bids an item's unit value; it does not consider effects of compliments or substitutes.
	// In particular, for perfect compliments, this agent will bid 0 on every item, and end up with nothing.
	
	public NaiveValueAgent(int agent_idx, Valuation valuation) {
		super(agent_idx, valuation);
	}

	@Override
	public HashMap<Integer, Double> getBids() {
		// Naive agent just always bids an item's independent value, as if that was the only
		// value it owned.
		HashMap<Integer, Double> bids = new HashMap<Integer, Double>(openAuctions.size());

		for (Integer a : openAuctions)
			bids.put(a, valuation.getValue(a));

		return bids;		
	}
}
