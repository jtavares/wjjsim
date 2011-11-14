import java.util.HashMap;

// NOTE: THIS IS A RATHER "DUMB" AGENT -- GENERALLY, YOU DO NOT WANT TO USE THIS AGENT!!
//       THIS AGENT WAS CREATED AS A TEST FOR STRICTLY ADDITIVE ASCENDING AUCTIONS.
//       FOR REAL SIMUL. ASC. AUCTIONS, USE A SUB-CLASS OF PerceivedPriceAgent.java INSTEAD!!
//
// an agent that performs straightforward bidding in ascending auctions on the 
// naive premise that his values are strictly additive.


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
