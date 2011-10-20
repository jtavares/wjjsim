import java.util.List;
import java.util.Vector;


public class ARHighestBidderAllPay extends AllocationRule {
	double reserve_price;
	
	ARHighestBidderAllPay(double reserve_price, double ask_price, double ask_epsilon) {
		super(ask_price, ask_epsilon);
		this.reserve_price = reserve_price;
	}
	
	// Award the auction to the highest bidder, but only if the total bids from across all bidders
	// meets the reserve price. Ties are broken randomly.
	public int apply(List<Bid> bids) {
		// Error check
		if (bids.size() == 0)
			return 0;
		
		// Find set of maximal bids
		double total_revenue = 0.0;
		
		Vector<Bid> winners = new Vector<Bid>(bids.size());
		winners.add(bids.get(0));
		
		for (Bid b : bids) {
			b.setIsWinner(false);
	
			if (b.getBid() > winners.get(0).getBid()) {
				winners.clear();
				winners.add(b);
			} else if (b.getBid() == winners.get(0).getBid()) {
				winners.add(b);
			}

			total_revenue += b.getBid();
		}
		
		// Choose a random high bidder
		int w  = (int)(winners.size() * Math.random());
		
		// Mark the random high bid as winner, so long as total auction revenue is at or above reserve price
		if (total_revenue >= reserve_price) {
			winners.get(w).setIsWinner(true);
			ask_price = winners.get(w).getBid() + ask_epsilon;
			return 1;
		} else {
			return 0;
		}
	}
}
