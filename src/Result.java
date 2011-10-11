public class Result {	
	Auction auction;
	boolean is_winner;
	double payment;
	
	public Result(Auction auction, boolean is_winner, double payment) {
		this.auction = auction;
		this.is_winner = is_winner;
		this.payment = payment;
	}
	
	public Auction getAuction() {
		return auction;
	}
	
	public double getPayment() {
		return payment;
	}
	
	public boolean getIsWinner() {
		return is_winner;
	}
}
