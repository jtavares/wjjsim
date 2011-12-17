import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

// Tests PP Star methdology on distribution price

public class TestPPStarDist {
	// *** CONFIGURATION ***
		
	//double sunk_awareness = 0.0; // k value (0=fully sunk, 1=straightforward bidding)
	
	int no_goods = 5;
	
	double ask_price = 1;		// initial ask price
	double ask_epsilon = 1; 	// initial ask epsilon
	int nth_pay = 1;			// first price auction
	
	int no_iterations = 10000;
	
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
	
	ArrayList<ArrayList<DiscreteDistribution>> welldist_pp;
	ArrayList<ArrayList<DiscreteDistribution>> ppstardist_pp;
	
	public TestPPStarDist(int no_pp_agents, int no_ppstar_agents, ArrayList<ArrayList<DiscreteDistribution>> welldist_pp, ArrayList<ArrayList<DiscreteDistribution>> ppstardist_pp) {
		this.no_pp_agents = no_pp_agents;
		this.no_ppstar_agents = no_ppstar_agents;
		this.no_agents = no_pp_agents + no_ppstar_agents;
		this.welldist_pp = welldist_pp;
		this.ppstardist_pp = ppstardist_pp;
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
		//System.out.println("sunk_awareness: " + sunk_awareness);
		
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
			agents.add(new DistributionPPAgent(i, new SchedulingValuation(no_goods), welldist_pp.get((int)(Math.random() * welldist_pp.size()))));
		
		// Create PPStar agents next.
		for (int i = 0; i<no_ppstar_agents; i++)
			agents.add(new DistributionPPStarAgent(no_pp_agents + i, new SchedulingValuation(no_goods), ppstardist_pp.get((int)(Math.random() * ppstardist_pp.size()))));
		
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
	
	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
		int no_agents = 8;
		
		String welldist_folder = "../saa_welldist_pp/";
		String ppstardist_folder = "../saa_ppstardist_pp/";
		
		// Load predicted price data
		ArrayList<ArrayList<DiscreteDistribution>> welldist_pp = new ArrayList<ArrayList<DiscreteDistribution>>();
			
		for(String fname : new File(welldist_folder).list()) {
			try {
				FileInputStream fis = new FileInputStream(welldist_folder + fname);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				welldist_pp.add((ArrayList<DiscreteDistribution>)ois.readObject());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Loaded " + welldist_pp.size() + " Wellman Dist price distributions.");
		
		ArrayList<ArrayList<DiscreteDistribution>> ppstardist_pp = new ArrayList<ArrayList<DiscreteDistribution>>();

		for(String fname : new File(ppstardist_folder).list()) {
			try {
				FileInputStream fis = new FileInputStream(ppstardist_folder + fname);
				ObjectInputStream ois = new ObjectInputStream(fis);
				
				ppstardist_pp.add((ArrayList<DiscreteDistribution>)ois.readObject());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Loaded " + ppstardist_pp.size() + " PPStar Dist price distributions.");
		
		for (int i = 8; i >= 0; i--) {
			System.out.println("----PP_AGENTS = " + i + ", NO_PPSTAR_AGENTS = " + (no_agents-i) + "-------");
			TestPPStarDist test = new TestPPStarDist(i, no_agents-i, welldist_pp, ppstardist_pp);
			test.run();
		}
	}
}
 