import java.util.HashMap;
import java.util.List;

public class SeqSSSimulation {
	// Implements a sequential, single-shot, auction for n agents and m goods.
	private List<Agent> agents;
	private List<SBAuction> auctions;
	
	public SeqSSSimulation(List<Agent> agents, List<SBAuction> auctions) {
		this.agents = agents;
		this.auctions = auctions;
	}
	
	public void play() {
		for (int j = 0; j<auctions.size(); j++) {
			//open the next auction
			for (int i = 0; i<agents.size(); i++) {
				agents.get(i).openAuction(j);
			}
	
			// Ask each agent for their bids
			for (int i = 0; i<agents.size(); i++) {
				HashMap<Integer, Double> i_bids = agents.get(i).getBids();	
				auctions.get(j).submitBid(i, (i_bids.get(j)));
			}
						
			// Apply allocation & payment rules
			auctions.get(j).solveAuction();
			
			report(j);
			
			// Tell agents this auction is closed. 
			for (int i = 0; i<agents.size(); i++) {
				agents.get(i).closeAuction(j);
			}
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
