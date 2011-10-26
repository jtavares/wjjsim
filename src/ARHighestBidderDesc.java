import java.util.List;
import java.util.Vector;


public class ARHighestBidderDesc extends AllocationRule {
	double reserve_price;
	
	ARHighestBidderDesc(double reserve_price, double ask_price, double ask_epsilon) {
		super(ask_price, ask_epsilon);
		this.reserve_price = reserve_price;
				
		if (this.ask_price < this.reserve_price)
			this.ask_price = this.reserve_price;
	}
	
	// Award the auction to the highest bidder, but only if that bid is greater than the reserve price.
	// Ties are broken randomly. Ask price decreases by ask_price each round, but never goes lower than the
	// reserve price.
	public int apply(List<Bid> bids) {
		// Error check
		if (bids.size() == 0) {
			// no bids, we need to lower price
			ask_price -= ask_epsilon;
			if (ask_price < reserve_price)
				ask_price = reserve_price;
			
			return 0;
		}
		
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
				
		// Mark the highest random bid as winner, so long as it is at or above reserve price
		if (winners.get(w).getBid() >= reserve_price) {
			winners.get(w).setIsWinner(true);
			return 1;
		} else {
			// Adjust ask price for next round
			ask_price -= ask_epsilon;
			if (ask_price < reserve_price)
				ask_price = reserve_price;

			return 0;
		}
	}

}
