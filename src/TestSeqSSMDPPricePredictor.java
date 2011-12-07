import java.util.ArrayList;
import java.util.List;

public class TestSeqSSMDPPricePredictor {
	int no_agents = 8;
	int no_auctions = 5;
	int nth_price = 2;
	double reserve_price = 0;
	int no_per_iteration = 250;
	int avg_iterations = 2;
	double ks_threshold = 0.01;
	double precision = 1.0;

	int no_pp_per_agent = 2;
	int no_simulations = 100000;
	double avg_surplus[] = new double[no_agents];
	
	public static void main (String args[]) {
		TestSeqSSMDPPricePredictor t = new TestSeqSSMDPPricePredictor();
		t.run();
	}
	
	public void run() {
		ArrayList<ArrayList<ArrayList<DiscreteDistribution>>> pp = new ArrayList<ArrayList<ArrayList<DiscreteDistribution>>>(no_agents);

		int[] max_iterations = {0, 0, 0, 0, 0, 0, 1, 2};
//		int[] max_iterations = {1, 1, 5, 5, 10, 10, 20, 20};

		for (int i = 0; i < no_agents; i++) {
			System.out.println("GENERATING PRICE PREDICTION FOR AGENT " + i + ", max_iterations=" + max_iterations[i]);

			ArrayList<ArrayList<DiscreteDistribution>> tmp = new ArrayList<ArrayList<DiscreteDistribution>>(no_pp_per_agent); 
			
			for (int j = 0; j<no_pp_per_agent; j++)
				tmp.add(genPrediction(max_iterations[i]));
			
			pp.add(tmp);
			
			System.out.println("");
		}
		
		System.out.println("");
		System.out.println("------PLAYING SEQ SIMULATION-----");

		for (int i = 0; i<no_simulations; i++) {
			if (i % 100 == 0)
				System.out.print(".");
			
			// Create distribution agents
			List<Agent> agents = new ArrayList<Agent>(no_agents);
			for (int j = 0; j<no_agents; j++)
				agents.add(new SeqSSMDPAgent(j, new SchedulingValuation(no_auctions), pp.get(j).get((int)(Math.random() * no_pp_per_agent))));
			
			// Create one auction per good
			List<SBAuction> auctions = new ArrayList<SBAuction>(no_auctions);
			for (int j = 0; j<no_auctions; j++)
				auctions.add(new SBNPAuction(j, reserve_price, 0, 0, agents, nth_price));
			
			// Play the auction
			SeqSSSimulation s = new SeqSSSimulation(agents, auctions);
			s.play(true);
			
			// Generate statistics
			for (int j = 0; j<no_agents; j++)
				avg_surplus[j] += agents.get(j).getTotalValuation() - agents.get(j).getTotalPayment();
		}
		
		// Normalize the results
		for (int j = 0; j<no_agents; j++)
			avg_surplus[j] /= no_simulations;

		// Print the results
		for (int j = 0; j<no_agents; j++)
			System.out.println("Agent " + j + ": PP=" + max_iterations[j] + ", Avg_Surplus=" + avg_surplus[j]);
	}
	
	private ArrayList<DiscreteDistribution> genPrediction(int max_iterations) {
		// Setup price predictor options
		PricePredictorSeqSSMDP pp = new PricePredictorSeqSSMDP(no_agents, no_auctions, nth_price, reserve_price,				
				no_per_iteration, max_iterations, avg_iterations, ks_threshold, precision);
		
		return pp.predict();		
	}
}
