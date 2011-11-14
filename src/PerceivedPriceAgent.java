import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// Implemented a "Perceived price" bidder, ala Simultaneous Ascending Auctions.
// This bidder requires a function, rho, which generates perceived prices. From these prices,
// the perceived price bidder bids on the items which bring it the most utility.

// NOTE: we take a shortcut from our framework and assume all here that auctions are open. I.e., we assume this a
// simultaneous ascending auction (where all auctions close at the the same time, at the end). Don't use this agent
// with other auction styles.

public abstract class PerceivedPriceAgent extends Agent {

	public PerceivedPriceAgent(int agent_idx, Valuation valuation) {
		super(agent_idx, valuation);
	}

	@Override
	public HashMap<Integer, Double> getBids() {
		// Solve the acquisition problem. We want to find the optimal basket X*, and bid the ask price on those items
		// we do not already possess.
				
		HashMap<Integer, Double> bids = new HashMap<Integer, Double>();

		double prices[] = rho();
		Set<Set<Integer>> ps = valuation.getPowerSetOfitems();

		Set<Integer> max_basket = null;
		double max_surplus = Double.NEGATIVE_INFINITY;
		for (Set<Integer> basket : ps) {
			double surplus = valuation.getValue(basket) - cost(basket, prices);
			
			if (surplus > max_surplus) {
				max_surplus = surplus;
				max_basket = basket;
			}
		}
		
		// Now that we have an optimal basket (which may be the empty set), place ask-price
		// bids on those items that we do not already own.
		for (Integer i : max_basket) {
			if (!results.get(i).getIsWinner())
				bids.put(i, results.get(i).getAskPrice());
		}
		
		return bids;
	}

	// A subclass must implement rho, which returns perceived final prices for
	// each item. The information state, bold-B, is available via private
	// member variables: results, openAuctions, and closedAuctions 
	protected abstract double[] rho();
	
	// Helper to get the cost of items in a basket, based on our perceived prices.
	private double cost(Set<Integer> basket, double[] prices) {
		double total_price = 0.0;
		
		for (Integer i : basket)
			total_price += prices[i];
		
		return total_price;
	}
}
