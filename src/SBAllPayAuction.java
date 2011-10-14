import java.util.List;

public class SBAllPayAuction extends Auction {
	// Implements a single sealed-bid all-pay price auction with supplied reserve price,
	// where the reserve price applies to the total auction revenue
	public SBAllPayAuction(int auction_idx, double reserve_price, List<Agent> agents) {
		super(auction_idx, reserve_price, agents);
		
		ar = new ARHighestBidderAllPay(reserve_price);
		pr = new PRAllPay();
	}
}
