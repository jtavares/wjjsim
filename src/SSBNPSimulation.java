import java.util.List;


public class SSBNPSimulation {
	// Implements a simultaneous, sealed-bid, auction for n agents and m goods.
	
	List<Agent> agents;
	List<Auction> auctions;
	
	public SSBNPSimulation(List<Agent> agents, List<Auction> auctions) {
		this.agents = agents;
		this.auctions = auctions;
	}
	
	public void play() {
		// Ask agents for their bids
		for (int i = 0; i<agents.size(); i++) {
			double[] i_bids = agents.get(i).getBids();
		
			// i: agent idx, j: auction idx.
			
			for (int j = 0; j<i_bids.length; j++)
				auctions.get(j).bids.get(i).setBid(i_bids[j]);
		}
		
		// Apply allocation & payment rules to each separate auction
		for (Auction a : auctions)
			a.solveAuction();
	}
	
	public void report() {
		// Report results per auction
		for (Auction a : auctions) {
			System.out.println("AUCTION " + a.getAuctionIdx() +
					" [reserve=" + a.getReservePrice() + 
					", no_winners=" + a.getNoWinners() +
					", tot_revenue=" + a.getTotalRevenue() + "]");

			for (Bid b : a.bids) {
				System.out.println("\t" + b.information());
			}

			System.out.println("");
		}
		
		System.out.println("");
		
		// Report results per-agent
		for (Agent a : agents) {
			System.out.println(a.information());
		}
	}	
}
