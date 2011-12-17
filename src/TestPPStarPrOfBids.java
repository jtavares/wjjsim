import java.util.ArrayList;
import java.util.List;

// Tests PP Star methdology on distribution price

public class TestPPStarPrOfBids {
	// *** CONFIGURATION ***
		
	double sunk_awareness = 0; // k value (0=fully sunk, 1=straightforward bidding)
	
	int no_goods = 5;
	
	double ask_price = 1;		// initial ask price
	double ask_epsilon = 1; 	// initial ask epsilon
	int nth_pay = 1;			// first price auction
	
	int no_samples = 10000;		// no of samples (auctions) to run to generate PrOfBids.
	int no_steps = 25;			// no of bid probability generation steps to perform
	
	int no_iterations = 100000;	// no of iterations to generate average payoff results.
	
	// *** VARIABLES ***
	int no_pp_agents;
	int no_ppstar_agents;
	int no_agents;

	double avg_pp_profit = 0;
	int no_pp_losses = 0;
	int no_pp_gains = 0;
	double avg_pp_loss = 0;
	double avg_pp_gain = 0;
	
	double avg_ppstar_profit = 0;
	int no_ppstar_losses = 0;
	int no_ppstar_gains = 0;
	double avg_ppstar_loss = 0;
	double avg_ppstar_gain = 0;

	ArrayList<ArrayList<Double>> PrOfBids_Pred;	// PrOfBids predicted for this round.
	ArrayList<Double> PrOfBids_Act;		// PrOfBids that actually occurred this round.

	public TestPPStarPrOfBids(int no_pp_agents, int no_ppstar_agents, ArrayList<Double> PrOfBids_LR) {
		this.no_pp_agents = no_pp_agents;
		this.no_ppstar_agents = no_ppstar_agents;
		this.no_agents = no_pp_agents + no_ppstar_agents;
		this.PrOfBids_Pred = new ArrayList<ArrayList<Double>>(no_steps);
		this.PrOfBids_Act = new ArrayList<Double>(256);
		
		// Make a deep copy of PrOfBids_LR.
		ArrayList<Double> last = new ArrayList<Double>(256);
		
		for (double d : PrOfBids_LR)
			last.add(d);
		
		this.PrOfBids_Pred.add(last);
	}
	
	public ArrayList<Double> run() {		
		// Generate bid probabilities for this mix.
		System.out.print("Generating PrOfBids...");

		for (int i = 1; i<no_steps; i++) {
			ArrayList<Double> prev = PrOfBids_Pred.get(i-1);
			ArrayList<Double> next = new ArrayList<Double>(256);
		
			for (int j = 0; j<no_samples; j++)
				generate_pr_iteration(prev, next);
						
			for (int j = 0; j<next.size(); j++) {
				double a = next.get(j) / (no_samples*no_agents); 	// probability this step
				double b = j < prev.size() ? prev.get(j) : 0;		// probability last step
				
				// make probability this round the average of this step and last step
				next.set(j, (a+b)/2);				
			}
			
			PrOfBids_Pred.add(next);
			
			System.out.print(".");
		}	

		System.out.println("Done.");
		
		// Play game
		System.out.println("Playing game with bid predictions....");
		
		ArrayList<Double> last = PrOfBids_Pred.get(no_steps-1);
		for (int i = 0; i<no_iterations; i++)
			iteration(last, PrOfBids_Act);

		for (int i = 0; i<PrOfBids_Act.size(); i++)
			PrOfBids_Act.set(i, PrOfBids_Act.get(i) / (no_iterations*no_agents));	

		System.out.println("Done.");

		avg_pp_profit /= no_iterations*no_pp_agents;
		avg_pp_loss /= no_pp_losses;
		avg_pp_gain /= no_pp_gains;
		
		avg_ppstar_profit /= no_iterations*no_ppstar_agents;
		avg_ppstar_loss /= no_ppstar_losses;
		avg_ppstar_gain /= no_ppstar_gains;
		
		// Print Results
		System.out.println("no_iterations: " + no_iterations);
		System.out.println("sunk_awareness: " + sunk_awareness);
		
		System.out.println("");
		
		System.out.println("no_pp_agents: " + no_pp_agents);
		System.out.println("no_pp_losses: " + no_pp_losses);
		System.out.println("no_pp_gains: " + no_pp_gains);
		System.out.println("avg_pp_profit: " + avg_pp_profit);
		System.out.println("avg_pp_loss: " + avg_pp_loss);
		System.out.println("avg_pp_gain: " + avg_pp_gain);

		System.out.println("");
		
		System.out.println("no_ppstar_agents: " + no_ppstar_agents);
		System.out.println("no_ppstar_losses: " + no_ppstar_losses);
		System.out.println("no_ppstar_gains: " + no_ppstar_gains);
		System.out.println("avg_ppstar_profit: " + avg_ppstar_profit);
		System.out.println("avg_ppstar_loss: " + avg_ppstar_loss);
		System.out.println("avg_ppstar_gain: " + avg_ppstar_gain);

		System.out.println("");
		
		System.out.print("Round,");
		
		for (int j = 0; j<no_steps; j++)
			System.out.print("Pred" + j + ",");
		
		System.out.println("Actual");
				
		int max_rounds = PrOfBids_Pred.get(0).size();
		for (int i = 1; i<no_steps; i++)
			if (PrOfBids_Pred.get(i).size() > max_rounds)
				max_rounds = PrOfBids_Pred.get(i).size();
		
		for (int i = 0; i<max_rounds; i++) {
			System.out.print(i + ",");

			for (int j = 0; j<no_steps; j++) {
				double pred = i < PrOfBids_Pred.get(j).size() ? PrOfBids_Pred.get(j).get(i) : 0;
			
				System.out.print(pred + ",");
			}
			
			double act = i < PrOfBids_Act.size() ? PrOfBids_Act.get(i) : 0;

			System.out.println(act + "");
		}

		System.out.println("");
		
		return PrOfBids_Act; // this will be fed into the next round
	}
	
	// Call this to add to the PrOfBids histogram. We use Sunkaware agents w/o supplying PrOfBids.
	private void generate_pr_iteration(ArrayList<Double> PrOfBids_input, ArrayList<Double> PrOfBids_output) {
		List<Agent> agents = new ArrayList<Agent>(no_agents);
		// Create PP agents first
		for (int i = 0; i<no_pp_agents; i++)
			agents.add(new SunkawarePPStarAgent(i, new SchedulingValuation(no_goods), sunk_awareness));
		
		// Create PPStar agents next.
		for (int i = 0; i<no_ppstar_agents; i++)
			agents.add(new SunkawarePPStarAgent(no_pp_agents + i, new SchedulingValuation(no_goods), PrOfBids_input, sunk_awareness));
		
		// Create one auction per good
		List<SBAuction> auctions = new ArrayList<SBAuction>(no_goods);
		for (int i = 0; i<no_goods; i++)
			auctions.add(new SBNPAuction(i, 0, ask_price, ask_epsilon, agents, nth_pay));
		
		// Play the auction. We provide PrOfBids_Pred to the simulator to cause it to populate PrOfBids_Pred.
		SimAscSimulation s = new SimAscSimulation(agents, auctions, PrOfBids_output);
		s.play(true);
	}

	// Call this to play one iteration and add to the statistics.
	private void iteration(ArrayList<Double> PrOfBids_input, ArrayList<Double> PrOfBids_output) {
		List<Agent> agents = new ArrayList<Agent>(no_agents);
		// Create PP agents first
		for (int i = 0; i<no_pp_agents; i++)
			agents.add(new SunkawarePPStarAgent(i, new SchedulingValuation(no_goods), sunk_awareness));
		
		// Create PPStar agents next.
		for (int i = 0; i<no_ppstar_agents; i++)
			agents.add(new SunkawarePPStarAgent(no_pp_agents + i, new SchedulingValuation(no_goods), PrOfBids_input, sunk_awareness));
		
		// Create one auction per good
		List<SBAuction> auctions = new ArrayList<SBAuction>(no_goods);
		for (int i = 0; i<no_goods; i++)
			auctions.add(new SBNPAuction(i, 0, ask_price, ask_epsilon, agents, nth_pay));
		
		// Play the auction. We provide PrOfBids_Act to record actual bids given our prediction.
		SimAscSimulation s = new SimAscSimulation(agents, auctions, PrOfBids_output);
		s.play(true);
		
		// Generate statistics
		for (int i = 0; i<no_pp_agents; i++) {
			double profit = agents.get(i).getTotalValuation() - agents.get(i).getTotalPayment();
			
			avg_pp_profit += profit;
			
			if (profit < 0) {
				no_pp_losses++;
				avg_pp_loss += profit;
			} else if (profit > 0) {
				no_pp_gains++;
				avg_pp_gain += profit;
			}
		}
		
		for (int i = no_pp_agents; i<no_pp_agents + no_ppstar_agents; i++) {
			double profit = agents.get(i).getTotalValuation() - agents.get(i).getTotalPayment();
			
			avg_ppstar_profit += profit;
			
			if (profit < 0) {
				no_ppstar_losses++;
				avg_ppstar_loss += profit;
			} else if (profit > 0) {
				no_ppstar_gains++;
				avg_ppstar_gain += profit;
			}
		}
	}
	
	public static void main(String args[]) {
		int no_agents = 8;
		
		ArrayList<Double> PrOfBids_LR = new ArrayList<Double>();
		for (int i = 8; i>= 0; i--) {
			System.out.println("**--- -PP_AGENTS = " + i + ", NO_PPSTAR_AGENTS = " + (no_agents-i) + "-----**");
			TestPPStarPrOfBids test = new TestPPStarPrOfBids(i, no_agents-i, PrOfBids_LR);
			PrOfBids_LR = test.run();
		}
	}
}
