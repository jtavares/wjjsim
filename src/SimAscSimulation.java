import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SimAscSimulation {
	// Implements a simultaneous, ascending, auction for n agents and m goods.

	private List<Agent> agents;
	private List<SBAuction> auctions;
	private ArrayList<Double> PrOfBids;
	private int rounds = 0;
		
	public SimAscSimulation(List<Agent> agents, List<SBAuction> auctions, ArrayList<Double> PrOfBids) {
		this.agents = agents;
		this.auctions = auctions;
		this.PrOfBids = PrOfBids;
	}
	
	public SimAscSimulation(List<Agent> agents, List<SBAuction> auctions) {
		this.agents = agents;
		this.auctions = auctions;
		this.PrOfBids = null;
	}
	
	public void play() {
		play(false);
	}
	
	public void play(boolean quiet) {
		// Tell agents the auctions are open.
		for (int i = 0; i<agents.size(); i++) {
			agents.get(i).openAllAuctions();
		}
		
		int bids;
		do {
			bids = 0;
			
			if (PrOfBids != null) {
				if (rounds == PrOfBids.size())
					PrOfBids.add(0.0);
			}
			
			// Ask agents for their for their bids for current round 
			for (int i = 0; i<agents.size(); i++) {
				// i_bids is agent i's bids, as a hash mapping of auction # to bid amount
				HashMap<Integer, Double> i_bids = agents.get(i).getBids();
			
				// i: agent idx, j: auction idx.
				
				boolean agent_bid = false;
				for (Integer j : i_bids.keySet())
					if (auctions.get(j).submitBid(i, i_bids.get(j))) // bids below ask will get rejected
						agent_bid = true;
				
				if (agent_bid)
					bids++;
			}
			
			// Apply allocation & payment rules to each separate auction
			for (SBAuction a : auctions)
				a.solveAuction();
			
			// Report round results.
			if (!quiet) {
				System.out.println("---ROUND " + rounds + "---");
				System.out.println("");
				report();
			}
			
			if (PrOfBids != null) {
				PrOfBids.set(rounds, PrOfBids.get(rounds) + bids);
			}
			
			rounds++;
		} while (bids > 0);
		
		// Tell agents the auctions are closed.
		for (int i = 0; i<agents.size(); i++) {
			agents.get(i).closeAllAuctions();
		}
	}
		
	public void report() {
		// Report current auction results
		for (SBAuction a : auctions) {
			a.report();
			System.out.println("");
		}
		
		System.out.println("");
		
		// Report results per-agent
		for (Agent a : agents)
			System.out.println(a.information());
	}
}
