import java.util.List;
import java.util.Vector;


public class ARHighestBidder extends AllocationRule {
	double reserve_price;
	
	ARHighestBidder(double reserve_price, double ask_price, double ask_epsilon) {
		super(ask_price, ask_epsilon);
		this.reserve_price = reserve_price;
	}
	
	// Award the auction to the highest bidder, but only if that bid is greater than the reserve price.
	// Ties are broken randomly.
	public int apply(List<Bid> bids) {
		// Error check
		if (bids.size() == 0)
			return 0;
		
		// Find set of maximal bids
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
		}
	
		// Choose a random high bidder
		int w  = (int)(winners.size() * Math.random());
		
		// Set ask price
		cur_price = winners.get(w).getBid();
		ask_price = winners.get(w).getBid() + ask_epsilon;
		
		// Mark the highest random bid as winner, so long as it is at or above reserve price
		if (winners.get(w).getBid() >= reserve_price) {
			winners.get(w).setIsWinner(true);
			return 1;
		} else {
			return 0;
		}
	}
}
