import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public abstract class Agent {
	Valuation valuation;
	int agent_idx;
	String agent_id;

	ArrayList<Result> results;
	
	public Agent(int agent_idx, Valuation valuation) {
		this.agent_idx = agent_idx;
		this.valuation = valuation;

		this.agent_id = UUID.randomUUID().toString();
		this.results = new ArrayList<Result>();

		for (int i = 0; i<valuation.getNoValuations(); i++)
			this.results.add(null);
	}

	public String getAgentID() {
		return agent_id;
	}
	
	public int getAgentIdx() {
		return agent_idx;
	}
	
	public void auctionResult(Result result) {
		results.set(result.getAuction().getAuctionIdx(), result);
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
	
	// For multi-round auctions, post the results from the previous round. For each item, 
	// let the bidder know the current winner & winning bid. May be NULL for auction formats
	// that do not include results information.
	public abstract void roundResult(List<Result> results);
	
	// Retrieve bids for the current round.
	public abstract double[] getBids();
}
