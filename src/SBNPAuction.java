import java.util.List;


public class SBNPAuction extends Auction {
	// Implements a single sealed-bid Nth price auction with supplied reserve price.
	
	double reserve_price;
	
	public SBNPAuction(int auction_idx, double reserve_price, List<Agent> agents, int nth_price) {
		super(auction_idx, agents);

		this.reserve_price = reserve_price;
		
		ar = new ARHighestBidder(reserve_price);
		pr = new PRNthPrice(reserve_price, nth_price);
	}
	
	public double getReservePrice() {
		return reserve_price;
	}
}
