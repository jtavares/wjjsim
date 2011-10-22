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
	
	/* For each auction j {
		  For each agent k
		    k.openAuction(j)

		  For each agent k {
		    k.getBids()
		    j.submitBid()
		  }

		  j.solveAuction()

		 For each agent k
		    k.closeAuction(j)
		} */
	
	public void play() {
		for (int j = 0; j<auctions.size(); j++) {
		// close the previous auction and open the next one (unless there is nothing to close)
			for (int i = 0; i<agents.size(); i++) {
				agents.get(i).openAuction(j);
			}
	
			// Ask each agent for their bids
			for (int i = 0; i<agents.size(); i++) {
				HashMap<Integer, Double> i_bids = agents.get(i).getBids();	
				System.out.println("i_bids==" + i_bids);
				auctions.get(j).submitBid(i, (i_bids.get(j)));
			}
						
			// Apply allocation & payment rules
			auctions.get(j).solveAuction();
			
			// Tell agents this auction is closed. 
			for (int i = 0; i<agents.size(); i++) {
				agents.get(i).closeAuction(j);
			}
		}
	}
			
	public void report() {
		// Report results per auction
		for (SBAuction a : auctions) {
			a.report();
			System.out.println("");
		}
		
		System.out.println("");
		
		// Report results per-agent
		for (Agent a : agents) {
			System.out.println(a.information());
		}
	}	

}
