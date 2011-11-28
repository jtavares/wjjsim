import java.util.HashMap;
import java.util.Set;

// Implemented a "Perceived price" bidder, ala Simultaneous Ascending Auctions.
// This bidder requires a function, rho, which generates perceived prices. From these prices,
// the perceived price bidder bids on the items which bring it the most utility.

//
// PerceivedPriceStar is an attempt to reduce exposure by only bidding on one item at a time from the basket
// of optimal goods.
//

// NOTE: we take a shortcut from our framework and assume all here that auctions are open. I.e., we assume this a
// simultaneous ascending auction (where all auctions close at the the same time, at the end). Don't use this agent
// with other auction styles.

public abstract class PerceivedPriceStarAgent extends PerceivedPriceAgent {

	public PerceivedPriceStarAgent(int agent_idx, Valuation valuation) {
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
		
		// ---PerceivedPrice* Agent logic---
		// Now that we have an optimal basket (which may be the empty set), place ask-price
		// bids on the item with the lowest ask price. Note that for problems that are
		// not based on SchedulingValuation, we may want to use surplus. We can cheat and 
		// use ask price because we know in the Scheduling problem that anything less
		// then meeting your objective is valued at 0.
		int lowest_ask = -1;
		for (Integer i : max_basket) {
			if (!results.get(i).getIsWinner()) {
				if (lowest_ask == -1)
					lowest_ask = i;
				else if (results.get(i).getAskPrice() < results.get(lowest_ask).getAskPrice())
					lowest_ask = i;
			}
		}	
		
		if (lowest_ask != -1)
			bids.put(lowest_ask, results.get(lowest_ask).getAskPrice());
		
		return bids;
	}

	// A subclass must implement rho, which returns perceived final prices for
	// each item. The information state, bold-B, is available via private
	// member variables: results, openAuctions, and closedAuctions (
	// where the last two variables are irrelevant in SAAs).
	protected abstract double[] rho();
	
	// Helper to get the cost of items in a basket, based on our perceived prices.
	private double cost(Set<Integer> basket, double[] prices) {
		double total_price = 0.0;
		
		for (Integer i : basket)
			total_price += prices[i];
		
		return total_price;
	}

}
