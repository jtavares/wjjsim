// Represents a (possibly interim) auction result for one agent in a given auction.
// The agent can query his/her current payment, whether or not they are the current winner,
// and the ask price for the following round.

public class Result {	
	private SBAuction auction;
	private boolean is_winner;
	private double payment;
	private double ask_price;
	private double cur_price;
	private double ask_epsilon;
	
	public Result(SBAuction auction, boolean is_winner, double payment, double ask_price, double cur_price, double ask_epsilon) {
		this.auction = auction;
		this.is_winner = is_winner;
		this.payment = payment;
		this.ask_price = ask_price;
		this.cur_price = cur_price;
		this.ask_epsilon = ask_epsilon;
	}
	
	// Returns a link to the original auction
	public SBAuction getAuction() {
		return auction;
	}
	
	// Returns the amount the you have to pay
	public double getPayment() {
		return payment;
	}
	
	// Returns true if you are the winner, false otherwise.
	public boolean getIsWinner() {
		return is_winner;
	}
	
	// Returns the ask price (should be: getCurPrice() + getAskEpsilon())
	public double getAskPrice() {
		return ask_price;
	}
	
	// Returns the current accept bid (price) for the good.
	public double getCurPrice() {
		return cur_price;
	}

	// Returns the epsilon required above current price
	public double getAskEpsilon() {
		return ask_epsilon;
	}
	
	// Get diagnostic information
	public String information() {
		return "is_winner=" + is_winner + ", payment=" + payment + ", ask_price=" + ask_price + ", cur_price=" + cur_price + ", ask_epsilon=" + ask_epsilon;
	}
}
