import java.util.HashMap;
import java.util.List;

public class SeqSSSimulation3 extends SeqSSSimulation2{
	// Implements a SeqSS
	
	public SeqSSSimulation3(List<Agent> agents, List<SBAuction> auctions) {
		super(agents,auctions);
	}

	@Override
	public void play() {
		// For each auction, one at a time
		for (int j = 0; j<auctions.size(); j++) {
			//open the next auction
			for (int i = 0; i<agents.size(); i++) {
				agents.get(i).openAuction(j);
				System.out.print("Agent " + i + "opens auction j. ");
			}
	
			// Ask each agent for their bid
			for (int i = 0; i<agents.size(); i++) {
				HashMap<Integer, Double> i_bids = agents.get(i).getBids();
				
				if (i_bids.containsKey(j))
					auctions.get(j).submitBid(i, (i_bids.get(j)));
					System.out.print("In auction " + j + ", Agent " + i + "submits bid " + i_bids.get(j));
			}
						
			// Apply allocation & payment rules
			auctions.get(j).solveAuction();
			System.out.print("Solving auction " + j + " ...");
						
			// Tell agents this auction is closed. 
			for (int i = 0; i<agents.size(); i++) {
				agents.get(i).closeAuction(j);
			}
			
			// Report results for the current auction
			report(j);
		}
	}

}

