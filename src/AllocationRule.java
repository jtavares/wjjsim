import java.util.List;

public abstract class AllocationRule {
	// Apply an allocation rule to an ordered list of bidders, and return the number of bidders who won.
	// Note that PaymentRule is run AFTER AllocationRule.
	public abstract int apply(List<Bid> bids);
}
