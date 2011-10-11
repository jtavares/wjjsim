public class Bid implements Comparable<Bid> {
	protected double bid;
	protected Agent agent;
	protected Auction auction;
	protected boolean is_winner = false;
	protected double payment;
	
	public Bid(Auction auction, Agent agent) {
		this.auction = auction;
		this.agent = agent;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setBid(double bid) {
		this.bid = bid;
	}
	
	public double getBid() {
		return bid;
	}
	
	// Is/is not a winner
	public boolean getIsWinner() {
		return is_winner;
	}
	
	public void setIsWinner(boolean is_winner) {
		this.is_winner = is_winner;
	}
	
	// Payment
	public double getPayment() {
		return payment;
	}
	
	public void setPayment(double payment) {
		this.payment = payment;
	}

	public String information() {
		return "Agent=" + agent.getAgentIdx() + ", bid=" + bid + ", is_winnner=" + is_winner + ", payment=" + payment;
	}

	// Compares by bid, and then by agent id.
	// This function inverts the normal compareTo() behavior so that sorts are descending. 
	@Override
	public int compareTo(Bid arg0) {	
		if (bid > arg0.bid)
			return -1;
		else if (bid < arg0.bid)
			return 1;
		else
			if (agent.getAgentIdx() > arg0.agent.getAgentIdx())
				return -1;
			else if (agent.getAgentIdx() < arg0.agent.getAgentIdx())
				return 1;
			else
				return 0; // this is bad. we need a toatl order. throw an exception?
	}
	
}
