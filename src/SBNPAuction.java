import java.util.List;


public class SBNPAuction extends SBAuction {
	// Implements a single sealed-bid Nth price auction with supplied reserve price.	
	public SBNPAuction(int auction_idx, double reserve_price, double ask_price, double ask_epsilon, List<Agent> agents, int nth_price) {
		super(	auction_idx,
				reserve_price,
				agents, 
				new ARHighestBidder(reserve_price, ask_price, ask_epsilon),
				new PRNthPrice(reserve_price, nth_price)
		);
	}
}
