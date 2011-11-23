import java.util.List;

public abstract class AllocationRule {
	double ask_epsilon = 0;
	double ask_price = 0;
	double cur_price = 0;

	public AllocationRule(double ask_price, double ask_epsilon) {
		this.ask_price = ask_price;
		this.ask_epsilon = ask_epsilon;
	}
	
	// Apply an allocation rule to an ordered list of bidders, and return the number of bidders who won.
	// Note that PaymentRule is run AFTER AllocationRule.
	public abstract int apply(List<Bid> bids);
	
	// returns the ask price for the next round of the auction (updated after each call to apply()).
	public double getAskPrice() {
		return ask_price;
	}

	// returns the current winning price (bid) for the auction (updated after each call to apply())
	public double getCurPrice() {
		return cur_price;
	}
	
	// returns the ask epsilon for this auction
	public double getAskEpsilon() {
		return ask_epsilon;
	}
}
