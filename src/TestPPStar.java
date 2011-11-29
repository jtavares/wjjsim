import java.util.ArrayList;
import java.util.List;

// Tests PP Star methdology

public class TestPPStar {
	// *** CONFIGURATION ***
	
	// total number of agents should equal 8.
	int no_pp_agents = 4;
	int no_ppstar_agents = 4;
	int no_agents = no_pp_agents + no_ppstar_agents;
	
	double sunk_awareness = 0.4; // k value (0=fully sunk, 1=straightforward bidding)
	
	int no_goods = 5;
	
	double ask_price = 0;		// initial ask price
	double ask_epsilon = 1; 	// initial ask epsilon
	int nth_pay = 1;			// first price auction
	
	int no_iterations = 10000;
	
	// *** VARIABLES ***
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
	
	public static void main(String args[]) {
		TestPPStar test = new TestPPStar();
		test.run();
	}
	
	public void run() {
		for (int i = 0; i<no_iterations; i++)
			iteration();
		
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
	}
	
	private void iteration() {
		List<Agent> agents = new ArrayList<Agent>(no_agents);
		// Create PP agents first
		for (int i = 0; i<no_pp_agents; i++)
			agents.add(new SunkawarePPAgent(i, new SchedulingValuation(no_goods), sunk_awareness));
		
		// Create PPStar agents next.
		for (int i = 0; i<no_ppstar_agents; i++)
			agents.add(new SunkawarePPStarAgent(no_pp_agents + i, new SchedulingValuation(no_goods), sunk_awareness));
		
		// Create one auction per good
		List<SBAuction> auctions = new ArrayList<SBAuction>(no_goods);
		for (int i = 0; i<no_goods; i++)
			auctions.add(new SBNPAuction(i, 0, ask_price, ask_epsilon, agents, nth_pay));
		
		// Play the auction
		SimAscSimulation s = new SimAscSimulation(agents, auctions);
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
}
