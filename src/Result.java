// Represents a (possibly interim) auction result for one agent in a given auction.
// The agent can query his/her current payment, whether or not they are the current winner,
// and the ask price for the following round.

public class Result {	
	private SBAuction auction;
	private boolean is_winner;
	private double payment;
	private double ask_price;
	
	public Result(SBAuction auction, boolean is_winner, double payment, double ask_price) {
		this.auction = auction;
		this.is_winner = is_winner;
		this.payment = payment;
		this.ask_price = ask_price;
	}
	
	public SBAuction getAuction() {
		return auction;
	}
	
	public double getPayment() {
		return payment;
	}
	
	public boolean getIsWinner() {
		return is_winner;
	}
	
	public double getAskPrice() {
		return ask_price;
	}
}
