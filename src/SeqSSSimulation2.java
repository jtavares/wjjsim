import java.util.HashMap;
import java.util.List;

public class SeqSSSimulation2 {
	// A copy of SeqSSSimulation. Simply changed some private parameters to public so other classes can extend it. 
	public List<Agent> agents;
	public List<SBAuction> auctions;
	
	public SeqSSSimulation2(List<Agent> agents, List<SBAuction> auctions) {
		this.agents = agents;
		this.auctions = auctions;
	}
	
	public void play() {
		// For each auction, one at a time
		for (int j = 0; j<auctions.size(); j++) {
			//open the next auction
			for (int i = 0; i<agents.size(); i++) {
				agents.get(i).openAuction(j);
			}
	
			// Ask each agent for their bid
			for (int i = 0; i<agents.size(); i++) {
				HashMap<Integer, Double> i_bids = agents.get(i).getBids();	
				
				if (i_bids.containsKey(j))
					auctions.get(j).submitBid(i, (i_bids.get(j)));
			}
						
			// Apply allocation & payment rules
			auctions.get(j).solveAuction();
						
			// Tell agents this auction is closed. 
			for (int i = 0; i<agents.size(); i++) {
				agents.get(i).closeAuction(j);
			}
			
			// Report results for the current auction
			report(j);
		}
	}
			
	public void report(int auction_idx) {
		// Report results per auction
		auctions.get(auction_idx).report();
		
		System.out.println("");
		
		// Report results per-agent
		for (Agent a : agents) {
			System.out.println(a.information());
		}
	}	

}
