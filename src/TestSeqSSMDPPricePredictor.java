import java.util.ArrayList;
import java.util.List;

public class TestSeqSSMDPPricePredictor {
	
	public static void main (String args[]) {
		// Setup price predictor options
		int no_agents = 8;
		int no_auctions = 5;
		int nth_price = 1;
		double reserve_price = 0;
		int no_per_iteration = 500;
		int max_iterations = 2;
		int avg_iterations = 10;
		double ks_threshold = 0.01;
		double precision = 1.0;
	
		PricePredictorSeqSSMDP pp = new PricePredictorSeqSSMDP(no_agents, no_auctions, nth_price, reserve_price,				
				no_per_iteration, max_iterations, avg_iterations, ks_threshold, precision);
		
		ArrayList<DiscreteDistribution> pp_data = pp.predict();
		
		System.out.println("");
		
		for (int i = 0; i<pp_data.size(); i++) {
			System.out.println("ITEM " + i + ", EFP: " + pp_data.get(i).getExpectedFinalPrice(0));
			pp_data.get(i).print(0);
			System.out.println("");
		}
		
		// Now that we have price predictions, play a game
		// NOTE: I use a shortcut here and give all agents get the exact same prediction.
		
		System.out.println("");
		System.out.println("------PLAYING SEQ SIMULATION-----");
		
		// Create distribution agents
		List<Agent> agents = new ArrayList<Agent>(no_agents);
		for (int i = 0; i<no_agents; i++)
			agents.add(new SeqSSMDPAgent(i, new SchedulingValuation(no_auctions), pp_data));
		
		// Create one auction per good
		List<SBAuction> auctions = new ArrayList<SBAuction>(no_auctions);
		for (int i = 0; i<no_auctions; i++)
			auctions.add(new SBNPAuction(i, reserve_price, 0, 0, agents, nth_price));
		
		// Play the auction
		SeqSSSimulation s = new SeqSSSimulation(agents, auctions);
		s.play();
	}
}
