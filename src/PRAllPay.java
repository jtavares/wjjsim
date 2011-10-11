import java.util.List;

public class PRAllPay extends PaymentRule {	
	// All Pay auction: all pay their bid, unless there are no winners, in which case
	// all bidders pay 0. (e.g., the reserve price not met during allocation).
	
	@Override
	public double apply(List<Bid> bids) {
		boolean is_winner = false;
		for (Bid b : bids) {
			b.setPayment(0.0);
			
			if (b.getIsWinner())
				is_winner = true;
		}
		
		if (is_winner) {
			double total_payments = 0.0;
			for (Bid b : bids) {
				total_payments += b.getBid();
				b.setPayment(b.getBid());
			}
			
			return total_payments;
		} else {
			return 0.0;
		}
	}
}
