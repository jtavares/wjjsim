import java.util.List;

public class SBAllPayAuction extends SBAuction {
	// Implements a single sealed-bid all-pay price auction with supplied reserve price,
	// where the reserve price applies to the total auction revenue.
	public SBAllPayAuction(int auction_idx, double reserve_price, double ask_price, double ask_epsilon, List<Agent> agents) {
		super(	auction_idx,
				reserve_price,
				agents,
				new ARHighestBidderAllPay(reserve_price, ask_price, ask_epsilon),
				new PRAllPay()	
		);
	}
}
