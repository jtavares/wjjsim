import java.util.ArrayList;
import java.util.List;

// implements a sealed-bid auction with specified payment and allocation rule 

public abstract class SBAuction {
	private int auction_idx;
	private double reserve_price;
	private List<Agent> agents;
	private AllocationRule ar;
	private PaymentRule pr;

	// Current admitted bids, one per agent
	private ArrayList<Bid> bids;
	private int no_admitted_bids = 0;
	
	// Filled in by solveAuction()
	private int no_winners;
	private double total_revenue;
	private double last_ask_price; // the ask price of the previous round
	
	public SBAuction(int auction_idx, double reserve_price, List<Agent> agents, AllocationRule ar, PaymentRule pr) {
		this.auction_idx = auction_idx;
		this.agents = agents;
		this.reserve_price = reserve_price;
		this.ar = ar;
		this.pr = pr;
		this.last_ask_price = ar.getAskPrice();
		
		bids = new ArrayList<Bid>(agents.size());
		for (Agent a : agents)
			this.bids.add(new Bid(this, a));
	}
	
	// submit a bid on behalf of an agent. returns true if bid was admitted, false otherwise.
	public boolean submitBid(int agent_idx, double bid) {
		if (bid >= ar.getAskPrice()) {
			bids.get(agent_idx).setBid(bid);
			no_admitted_bids++;
			return true;
		} else {
			return false;
		}
	}
	
	// call this at the end of a round to solve the auction.
	// returns true if auction was quiescent, false otherwise.
	public boolean solveAuction() {	
		last_ask_price = ar.getAskPrice();
		no_winners = ar.apply(bids);
		total_revenue = pr.apply(bids);

		// Post auction results to each agent.
		double max_bid = 0;
		for (int i = 0; i<bids.size(); i++) {
			Result r = new Result(this, bids.get(i).getIsWinner(), bids.get(i).getPayment(), ar.getAskPrice());
			agents.get(i).postResult(r);
			
			// find highest bid
			if (bids.get(i).getBid() > max_bid)
				max_bid = bids.get(i).getBid();
		}
		
		// determine if this auction was quiescent?
		boolean quiescent = (no_admitted_bids == 0);
		no_admitted_bids = 0;
		
		return quiescent;
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
	
	public List<Bid> getCurrentBids() {
		return bids;
	}
	
	public double getLastAskPrice() {
		return last_ask_price;
	}
	
	public double getCurrentAskPrice() {
		return ar.getAskPrice();
	}
	
	public void report() {
		System.out.println("AUCTION " + getAuctionIdx() +
				" [ask=" + getLastAskPrice() +
				", reserve=" + getReservePrice() + 
				", no_winners=" + getNoWinners() +
				", tot_revenue=" + getTotalRevenue() + "]");

		for (Bid b : bids)
			System.out.println("\t" + b.information());
	}
}
