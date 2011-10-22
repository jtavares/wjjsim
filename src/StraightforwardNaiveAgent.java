import java.util.HashMap;

// an agent that performs straightforward bidding in ascending auctions on the 
// naive premise that his values are strictly additive

public class StraightforwardNaiveAgent extends Agent {
	public StraightforwardNaiveAgent(int agent_idx, Valuation valuation) {
		super(agent_idx, valuation);
	}

	@Override
	public HashMap<Integer, Double> getBids() {
		HashMap<Integer, Double> bids = new HashMap<Integer, Double>(openAuctions.size());

		for (Integer a : openAuctions) {
			if (valuation.getValue(a) >= results.get(a).getAskPrice() && !results.get(a).getIsWinner())
				bids.put(a, results.get(a).getAskPrice());
		}

		return bids;
	}

}
