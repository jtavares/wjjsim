import java.util.ArrayList;
import java.util.List;


public abstract class Auction {
	int auction_idx;
	double reserve_price;
	List<Agent> agents;
	
	// To be filled by sub-class:
	AllocationRule ar;
	PaymentRule pr;
	
	// Current bids, one per agent
	List<Bid> bids;
	
	// Filled in by solveAuction()
	int no_winners;
	double total_revenue;
	
	public Auction(int auction_idx, double reserve_price, List<Agent> agents) {
		this.auction_idx = auction_idx;
		this.agents = agents;
		this.reserve_price = reserve_price;
		
		bids = new ArrayList<Bid>(agents.size());
		for (Agent a : agents)
			this.bids.add(new Bid(this, a));
	}
	
	public void solveAuction() {
		no_winners = ar.apply(bids);
		total_revenue = pr.apply(bids);

		// Post agent-specific auction results to each agent.
		for (int i = 0; i<bids.size(); i++) {
			Result r = new Result(this, bids.get(i).getIsWinner(), bids.get(i).getPayment());
			agents.get(i).auctionResult(r);
		}
		
		// TODO: for some auction types, such as SAA, we may want to post-back results about "winning" agents
		// and payments for the current round.
		
		// The best place for these types of dispatches would be in a overridden solveAuction() of a sub-class
	}
	
	public int getAuctionIdx() {
		return auction_idx;
	}	
	
	public int getNoWinners() {
		return no_winners;
	}
	
	public double getTotalRevenue() {
		return total_revenue;
	}
	
	public double getReservePrice() {
		return reserve_price;
	}
}
