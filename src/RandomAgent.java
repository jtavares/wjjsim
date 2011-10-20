import java.util.HashMap;

public class RandomAgent extends Agent {
	// An agent that bids randomly in auctions, regardless of his true value.
	
	public RandomAgent(int agent_idx,  Valuation valuation) {
		super(agent_idx, valuation);
	}

	@Override
	public HashMap<Integer, Double> getBids() {
		// random agent always just bids a random number, irrespective of valuations.
		HashMap<Integer, Double> bids = new HashMap<Integer, Double>(openAuctions.size());

		for (Integer a : openAuctions)
			bids.put(a, Math.random());

		return bids;
	}
}
