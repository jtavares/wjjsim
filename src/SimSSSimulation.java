import java.util.HashMap;
import java.util.List;


public class SimSSSimulation {
	// Implements a simultaneous, single-shot, auction for n agents and m goods.
	
	private List<Agent> agents;
	private List<SBAuction> auctions;
	
	public SimSSSimulation(List<Agent> agents, List<SBAuction> auctions) {
		this.agents = agents;
		this.auctions = auctions;
	}
	
	public void play() {
		// Tell agents all auctions are now open.
		for (int i = 0; i<agents.size(); i++) {
			agents.get(i).openAllAuctions();
		}
		
		// Ask agents for their bids
		for (int i = 0; i<agents.size(); i++) {
			HashMap<Integer, Double> i_bids = agents.get(i).getBids();
		
			// i: agent idx, j: auction idx.
			
			for (Integer j : i_bids.keySet())
				auctions.get(j).submitBid(i, (i_bids.get(j)));
		}
		
		// Apply allocation & payment rules to each underlying auction
		for (SBAuction a : auctions)
			a.solveAuction();
		
		// Tell agents all auctions are closed.
		for (int i = 0; i<agents.size(); i++) {
			agents.get(i).closeAllAuctions();
		}
		
		
		// Report results
		report();
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
