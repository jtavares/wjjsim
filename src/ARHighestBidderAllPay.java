import java.util.List;


public class ARHighestBidderAllPay extends AllocationRule {
	double reserve_price;
	
	ARHighestBidderAllPay(double reserve_price) {
		this.reserve_price = reserve_price;
	}
	
	// Award the auction to the highest bidder, but only if the total bids from across all bidders
	// meets the reseve price.
	public int apply(List<Bid> bids) {
		// Error check
		if (bids.size() == 0)
			return 0;
		
		// Find maximum bid
		double total_revenue = 0.0;
		Bid winning = bids.get(0);
		
		for (Bid b : bids) {
			winning.setIsWinner(false);

			if (b.getBid() > winning.getBid())
				winning = b;
			
			total_revenue = b.getBid();
		}
		
		// Mark the highest bid as winner, so long as total auction revenue is at or above reserve price
		if (total_revenue >= reserve_price) {
			winning.setIsWinner(true);
			return 1;
		} else {
			return 0;
		}
	}
}
