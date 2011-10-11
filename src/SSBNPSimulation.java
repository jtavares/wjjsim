import java.util.ArrayList;
import java.util.List;


public class SSBNPSimulation {
	// Implements a simultaneous, sealed-bid, auction for K goods, with an allocation rule
	// of highest bidder wins, and a payment rule of nTh price. Valuations are drawn from U[0,1].
	// Agents are RandomBidders. A reserve price is assigned at random for eacha auction from U0,1].
	
	int nth_price;
	int no_auctions;
	int no_agents;

	List<Agent> agents;
	List<SBNPAuction> auctions;
	
	public SSBNPSimulation(int nth_price, int no_auctions, int no_agents) {
		this.nth_price = nth_price;
		this.no_auctions = no_auctions;
		this.no_agents = no_agents;

		// Create agents
		agents = new ArrayList<Agent>(no_agents);
		for (int i = 0; i<no_agents; i++) {
			// Set agent valuations to randomly generated uniform additive valuations.
			agents.add(new RandomAgent(i, new UniformAdditiveValuation(no_auctions)));
		}

		// Create individual SBNP auctions
		auctions = new ArrayList<SBNPAuction>(no_auctions);
		for (int i = 0; i<no_auctions; i++)
			auctions.add(new SBNPAuction(i, Math.random(), agents, nth_price));
	}
	
	public void play() {
		// Ask agents for their bids
		for (int i = 0; i<no_agents; i++) {
			double[] i_bids = agents.get(i).getBids();
		
			// i = agent idx, j == auction idx.
			
			for (int j = 0; j<i_bids.length; j++)
				auctions.get(j).bids.get(i).setBid(i_bids[j]);
		}
		
		// Apply allocation & payment rules to each separate auction
		for (Auction a : auctions)
			a.solveAuction();
	}
	
	public void report() {
		// Report results per auction
		for (SBNPAuction a : auctions) {
			System.out.println("AUCTION " + a.getAuctionIdx() + " [reserve=" + a.getReservePrice() + ", no_winners=" + a.getNoWinners() + ", tot_revenue=" + a.getTotalRevenue());

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
