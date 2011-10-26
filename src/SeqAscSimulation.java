import java.util.HashMap;
import java.util.List;


public class SeqAscSimulation {

	private List<Agent> agents;
	private List<SBAuction> auctions;
	
	public SeqAscSimulation(List<Agent> agents, List<SBAuction> auctions) {
		this.agents = agents;
		this.auctions = auctions;
	}
	
	public void play() {
		// For each auction
		for(int x=0; x<auctions.size(); x++) {
			int rounds = 0;
			
			// Open the auction
			for(int y=0;y<agents.size();y++)
				agents.get(y).openAuction(x);
			
			boolean activity;
			do {
				activity = false;
				
				// Ask agents for their for their bids for current round 
				for (int i = 0; i<agents.size(); i++) {
					// i_bids is agent i's bids, as a hash mapping of auction # to bid amount
					HashMap<Integer, Double> i_bids = agents.get(i).getBids();
					
					if (i_bids.containsKey(x))
						activity |= auctions.get(x).submitBid(i, i_bids.get(x)); // bids below ask will get rejected.
				}
				
				// Apply allocation & payment rules to each separate auction
				auctions.get(x).solveAuction();
				
				// Report round results.
				System.out.println("--- AUCTION " + x + " --- ROUND " + rounds + " ---");
				System.out.println("");
				report(x);
				
				rounds++;
			} while (activity);
			
			// Close the auction
			for(int y=0;y<agents.size();y++)
				agents.get(y).closeAuction(x);
		}
	}
	
	public void report(int auction_idx) {
		// Report current auction results
		auctions.get(auction_idx).report();
		
		System.out.println("");
		
		// Report results per-agent
		for (Agent a : agents)
			System.out.println(a.information());
			
	}
}
