import java.util.List;


public class SBNPAuction extends Auction {
	// Implements a single sealed-bid Nth price auction with supplied reserve price.	
	public SBNPAuction(int auction_idx, double reserve_price, List<Agent> agents, int nth_price) {
		super(auction_idx, reserve_price, agents);
		
		ar = new ARHighestBidder(reserve_price);
		pr = new PRNthPrice(reserve_price, nth_price);
	}
}
