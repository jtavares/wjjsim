import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestSeqSSMDPPricePredictor {
	int no_agents = 8;
	int no_of_new_agents = 1;		// number of SeqSScorrMDPAgents to compete against SeqSSMDPAgents

	int no_auctions = 5;
	int nth_price = 2;
	double reserve_price = 0;
	int no_per_iteration = 500;
	int avg_iterations = 40;		// number of PPs to average over
	double ks_threshold = 0.01;
	double precision = 1.0;

	int no_pp_per_agent = 1;
	int no_simulations = 40000;
	double avg_surplus[] = new double[no_agents];
	ArrayList<Double> surplus; 
	
	String filename = "/Users/jl52/Desktop/fall_2011/Greenwald/project/data/surplus_data.csv";	// save name for surplus output file
	
	
	BufferedWriter bw;
	StringBuilder contents;

	
	public static void main (String args[]) {
		TestSeqSSMDPPricePredictor t = new TestSeqSSMDPPricePredictor();
		t.run();
	}
	
	// A user can call this function to print all contents in "contents" 
	public void printFile(String filename)
	{
		try {	bw=new BufferedWriter(new FileWriter(new File (filename)));
				bw.write(contents.toString());
				bw.close();
		} catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}

	// Get a line of surplus outcome into contents
	public String getPrintTable(ArrayList<Double> surplus)
	{
		StringBuilder table=new StringBuilder("");
		
		for (int i = 0; i<surplus.size()-1; i++)
			table.append(surplus.get(i)+",");
		
		table.append(surplus.get(surplus.size()-1));
		
		return table.toString();
	}
	
	public void run() {
		ArrayList<ArrayList<ArrayList<DiscreteDistribution>>> pp = new ArrayList<ArrayList<ArrayList<DiscreteDistribution>>>(no_agents);

//		int[] max_iterations = {30, 30, 30, 30, 30, 30, 30, 30};
		int[] max_iterations = {50, 1, 1, 1, 1, 1, 1, 1};
		
		// Let's feed everyone the same input price prediction		
		ArrayList<Double> weight = new ArrayList<Double>();	// dummy weights
		ArrayList<ArrayList<DiscreteDistribution>> tmp = new ArrayList<ArrayList<DiscreteDistribution>>(no_pp_per_agent);
		tmp.add(genPrediction(max_iterations[0], false, weight));	

		for (int i = 0; i < no_agents; i++) {
			System.out.println("GENERATING PRICE PREDICTION FOR AGENT " + i + ", max_iterations=" + max_iterations[i]);
//			ArrayList<ArrayList<DiscreteDistribution>> tmp = new ArrayList<ArrayList<DiscreteDistribution>>(no_pp_per_agent); 			
			for (int j = 0; j<no_pp_per_agent; j++) {
//				ArrayList<Double> weight = new ArrayList<Double>();	// a necessary input to "genPrediction"; keep empty unless doing smoothed price updates

//				weight.add(0.5);
//				weight.add(0.5);
//				String filename = "/Users/jl52/Desktop/fall_2011/Greenwald/project/data/convergence_diag/updating2_" + i + ".csv";

//				tmp.add(genPrediction_andPrint(max_iterations[i], false, weight, filename));
//				tmp.add(genPrediction(max_iterations[i], false, weight));
				pp.add(tmp);
			}
			System.out.println("");
		}
		
		System.out.println("");
		System.out.println("------PLAYING SEQ SIMULATION-----");

		for (int i = 0; i<no_simulations; i++) {
			
			// Create distribution agents
			List<Agent> agents = new ArrayList<Agent>(no_agents);
			
			// new MDP Agents
			for (int j = 0; j < no_of_new_agents; j++){
				agents.add(new SeqSScorrMDPAgent(j, new SchedulingValuation(no_auctions), pp.get(j).get((int)(Math.random() * no_pp_per_agent))));
			}
			
			// old MDP Agents
			for (int j = no_of_new_agents; j<no_agents; j++)
				agents.add(new SeqSSMDPAgent(j, new SchedulingValuation(no_auctions), pp.get(j).get((int)(Math.random() * no_pp_per_agent))));
		
			// Create one auction per good
			List<SBAuction> auctions = new ArrayList<SBAuction>(no_auctions);
			for (int j = 0; j<no_auctions; j++)
				auctions.add(new SBNPAuction(j, reserve_price, 0, 0, agents, nth_price));
			
			// Play the auction
			SeqSSSimulation s = new SeqSSSimulation(agents, auctions);
			s.play(true);
			
			/*
			// print final prices of each game to screen			
			ArrayList<Double> final_prices = new ArrayList<Double>();
			for (int j = 0; j<no_auctions; j++) {
				 final_prices.add(auctions.get(j).getWinnerPayment());
			}
			System.out.println(final_prices.get(0)+","+final_prices.get(1)+","+final_prices.get(2)+","+final_prices.get(3)+","+final_prices.get(4));
			*/
			
			// Save each agents' surplus
			surplus = new ArrayList<Double>();
			for (int j = 0; j<no_agents; j++) {
				surplus.add(agents.get(j).getTotalValuation() - agents.get(j).getTotalPayment());
			}
			// System.out.println(surplus[0]+","+surplus[1]+","+surplus[2]+","+surplus[3]+","+surplus[4]+","+surplus[5]+","+surplus[6]+","+surplus[7]);
			contents.append(getPrintTable(surplus));
			
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
		
		// Write surpluses to file
		printFile(filename);

	}
	
	
	// generate self-confirming price prediction
	private ArrayList<DiscreteDistribution> genPrediction(int max_iterations, boolean smoothed, ArrayList<Double> weight) {
		
		// Setup price predictor options
		PricePredictorSeqSSMDP pp = new PricePredictorSeqSSMDP(no_agents, no_auctions, nth_price, reserve_price,				
				no_per_iteration, max_iterations, avg_iterations, ks_threshold, precision);
		
		// Whether to employ smoothed price updating
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
		
		// Whether to employ smoothed price updating
		if (smoothed){
			ArrayList<DiscreteDistribution> end_dist =  pp.predict_smoothed(weight);
			pp.printFile2(filename);	// write to file
			return end_dist;
		}
		else {
			ArrayList<DiscreteDistribution>  end_dist = pp.predict();
			pp.printFile2(filename);	// write to file
			return end_dist;
		}		
	}
	
}
