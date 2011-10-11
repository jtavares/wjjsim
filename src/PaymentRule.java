import java.util.List;

public abstract class PaymentRule {
	// Calculate and apply payments for a list of bids, and return total payments for the auction.
	// Note PaymentRule is run AFTER AllocationRule.
	public abstract double apply(List<Bid> bids);
}
