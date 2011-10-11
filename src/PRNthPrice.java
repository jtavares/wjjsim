import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PRNthPrice extends PaymentRule {
	double reserve_price;
	int nth_price;
	
	PRNthPrice(double reserve_price, int nth_price) {
		this.reserve_price = reserve_price;
		this.nth_price = nth_price;
	}

	@Override
	public double apply(List<Bid> bids) {
		// Rank the bids to find the nTH highest.
		List<Bid> ranked_bids = new ArrayList<Bid>(bids);		
		Collections.sort(ranked_bids);
		
		// Populate bid list such that winners pay nth_price, and everyone else pays 0.
		double total_payments = 0.0;
		for (Bid b : bids) {
			if (b.getIsWinner()) {
				if (ranked_bids.size() < nth_price) {
					// The requested rank does not exist. Must pay reserve price.
					b.setPayment(reserve_price);
				} else {
					b.setPayment(ranked_bids.get(nth_price - 1).getBid());
					
					// Make sure the 2nd highest price meets reserve. If not, set payment to that of reserve.
					if (b.getPayment() < reserve_price)
						b.setPayment(reserve_price);
				}

				total_payments += b.getPayment();
			} else {
				b.setPayment(0.0);
			}
		}
		
		return total_payments;
	}
}
