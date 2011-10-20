import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Agent {
	Valuation valuation;
	int agent_idx;
	String agent_id;

	ArrayList<Result> results;
	HashSet<Integer> openAuctions;
	
	public Agent(int agent_idx, Valuation valuation) {
		this.agent_idx = agent_idx;
		this.valuation = valuation;

		this.agent_id = UUID.randomUUID().toString();
		this.results = new ArrayList<Result>(valuation.getNoValuations());

		for (int i = 0; i<valuation.getNoValuations(); i++)
			this.results.add(null);
		
		this.openAuctions = new HashSet<Integer>(valuation.getNoValuations());
	}

	public String getAgentID() {
		return agent_id;
	}
	
	public int getAgentIdx() {
		return agent_idx;
	}
	
	public double getTotalPayment() {
		double total_payment = 0.0;
		
		for (Result r : results)
			total_payment += r.getPayment();
			
		return total_payment;
	}
	
	public double getTotalValuation() {
		// To compute total valuation, we create a basket of the winning auction numbers
		// and pass that to the underlying valuation engine.
		HashSet<Integer> won = new HashSet<Integer>();		
		for (Result r : results)		
			if (r.getIsWinner())
				won.add(r.getAuction().getAuctionIdx());			
		
		return valuation.getValue(won);
	}
	
	public String information() {
		double total_payment = 0.0;
		
		// To compute total valuation, we create a basket of the winning auction numbers
		// and pass that to the underlying valuation engine.
		String str_won = "";
		HashSet<Integer> won = new HashSet<Integer>();		
		for (Result r : results) {
			total_payment += r.getPayment();
			
			if (r.getIsWinner()) {
				won.add(r.getAuction().getAuctionIdx());
				
				if (!str_won.equals(""))
					str_won += ", ";
				
				str_won += r.getAuction().getAuctionIdx();
			}
		}
		
		double total_valuation = valuation.getValue(won);	
		
		return "AGENT " + agent_idx + "\n\tvaluation=" + total_valuation + ", payments=" +
			total_payment + ", profit=" + (total_valuation - total_payment) + "\n\tauctions won={" + str_won + "}\n";
	}
	
	// alert the agent that he will need to bid in an auction
	public void openAuction(int auction_idx) {
		// open an auction, if the auction idx is valid.
		if (auction_idx > 0 && auction_idx < valuation.getNoValuations())
			openAuctions.add(auction_idx);
	}
	
	// alert the agent that he will need to bid in all auctions (short-cut for simultaneous auctions)
	public void openAllAuctions() {
		openAuctions.clear(); // shouldn't need to clear, but, why not?
		
		for (int i = 0; i<valuation.getNoValuations(); i++)
			openAuctions.add(i);
	}
	
	// post a single auction result back to the agent
	public void postResult(Result prev_result) {
		results.set(prev_result.getAuction().getAuctionIdx(), prev_result);
	}

	// post a list of auction results back to the agent, one per currently open auction
	public void postResults(Set<Result> prev_results) {
		for (Result r : prev_results) {
			results.set(r.getAuction().getAuctionIdx(), r);
		}
	}
	
	// ask agent to bid for each currently open auction (agent return bids for any subset of the currently open auctions,
	// i.e., it may choose not to bid on any or all open auctions). The HashMap is from Auction# (integer) to Bid Price (double).
	public abstract HashMap<Integer, Double> getBids();
	
	// alert the agent that he can no longer bid in an auction. The last result received for said
	// auction is the auction result.
	public void closeAuction(int auction_idx) {
		openAuctions.remove(auction_idx);
	}
	
	// alert the agent that he can no longer bid in any auction (short-cut for simultaneous auctions)
	public void closeAllAuctions() {
		openAuctions.clear();
	}
}
