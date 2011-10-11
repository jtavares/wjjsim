import java.util.List;


public class ARHighestBidder extends AllocationRule {
	double reserve_price;
	
	ARHighestBidder(double reserve_price) {
		this.reserve_price = reserve_price;
	}
	
	// Award the auction to the highest bidder, but only if that bid is greater than the reserve price.
	public int apply(List<Bid> bids) {
		// Error check
		if (bids.size() == 0)
			return 0;
		
		// Find maximum bid
		Bid winning = bids.get(0);
		
		for (Bid b : bids) {
			winning.setIsWinner(false);

			if (b.getBid() > winning.getBid())
				winning = b;
		}
		
		// Mark the highest bid as winner, so long as it is at or above reserve price
		if (winning.getBid() >= reserve_price) {
			winning.setIsWinner(true);
			return 1;
		} else {
			return 0;
		}
	}
}
