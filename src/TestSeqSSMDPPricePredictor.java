import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestSeqSSMDPPricePredictor {
	int no_agents = 8;
	int no_auctions = 5;
	int nth_price = 2;
	double reserve_price = 0;
	int no_per_iteration = 250;
	int avg_iterations = 1;
	double ks_threshold = 0.01;
	double precision = 1.0;

	int no_pp_per_agent = 1;
	int no_simulations = 750;
	double avg_surplus[] = new double[no_agents];
	
	public static void main (String args[]) {
		TestSeqSSMDPPricePredictor t = new TestSeqSSMDPPricePredictor();
		t.run();
	}
	
	public void run() {
		ArrayList<ArrayList<ArrayList<DiscreteDistribution>>> pp = new ArrayList<ArrayList<ArrayList<DiscreteDistribution>>>(no_agents);

		int[] max_iterations = {50, 500, 500, 1000, 1, 1, 1, 1};

		for (int i = 0; i < no_agents; i++) {
			System.out.println("GENERATING PRICE PREDICTION FOR AGENT " + i + ", max_iterations=" + max_iterations[i]);

			ArrayList<ArrayList<DiscreteDistribution>> tmp = new ArrayList<ArrayList<DiscreteDistribution>>(no_pp_per_agent); 
			
			for (int j = 0; j<no_pp_per_agent; j++) {
				ArrayList<Double> weight = new ArrayList<Double>();	// dummy weights
				weight.add(0.5);
				weight.add(0.5);
				String filename = "/Users/jl52/Desktop/fall_2011/Greenwald/project/data/convergence_diag/updating2_" + i + ".csv";
				tmp.add(genPrediction_andPrint(max_iterations[i], true, weight, filename));
//				tmp.add(genPrediction(max_iterations[i], false, weight));
				pp.add(tmp);
			}
			System.out.println("");
		}
		
		System.out.println("");
		System.out.println("------PLAYING SEQ SIMULATION-----");

		for (int i = 0; i<no_simulations; i++) {
//			if (i % 100 == 0)
//				System.out.print(".");
			
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
			
			// print out final prices to screen			
			ArrayList<Double> final_prices = new ArrayList<Double>();
			for (int j = 0; j<no_auctions; j++) {
				 final_prices.add(auctions.get(j).getWinnerPayment());
			}
			System.out.println(final_prices.get(0)+","+final_prices.get(1)+","+final_prices.get(2)+","+final_prices.get(3)+","+final_prices.get(4));
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
	
	// generate self-confirming price prediction
	private ArrayList<DiscreteDistribution> genPrediction(int max_iterations, boolean smoothed, ArrayList<Double> weight) {
		// Setup price predictor options
		PricePredictorSeqSSMDP pp = new PricePredictorSeqSSMDP(no_agents, no_auctions, nth_price, reserve_price,				
				no_per_iteration, max_iterations, avg_iterations, ks_threshold, precision);
		if (smoothed){
			return pp.predict_smoothed(weight);
		}
		else {
			return pp.predict();
		}		
	}

	// Generate self-confirming price prediction, and also print out the price update process under name "filename"
	private ArrayList<DiscreteDistribution> genPrediction_andPrint(int max_iterations, boolean smoothed, ArrayList<Double> weight, String filename) {
		// Setup price predictor options
		PricePredictorSeqSSMDP pp = new PricePredictorSeqSSMDP(no_agents, no_auctions, nth_price, reserve_price,				
				no_per_iteration, max_iterations, avg_iterations, ks_threshold, precision);
		if (smoothed){
			ArrayList<DiscreteDistribution> end_dist =  pp.predict_smoothed(weight);
			pp.printFile2(filename);
			return end_dist;
		}
		else {
			ArrayList<DiscreteDistribution>  end_dist = pp.predict();
			pp.printFile2(filename);
			return end_dist;
		}		
	}
	
}
