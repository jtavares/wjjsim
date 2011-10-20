import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Simulator {
	public static void main(String args[]) {	
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Welcome to WJJSIM.");
		System.out.println("------------------");
		System.out.println("");	

		System.out.print("Enter 0 for single-shot, 1 for ascending> ");
		int style = sc.nextInt();
		
		System.out.print("Enter 0 for all-pay, or 1 thru n for n-th price> ");	
		int pay = sc.nextInt();
		
		System.out.print("Agent bid logic [0=random, 1=straight value]> ");
		int bid_logic = sc.nextInt();
		
		System.out.print("Agent valuation model [0=uniform additive, 1=substitutes, 2=complements]> ");
		int valuation = sc.nextInt();

		System.out.print("Please enter no. of agents> ");
		int no_agents = sc.nextInt();
				
		System.out.print("Please enter no. of goods> ");
		int no_auctions = sc.nextInt();

		System.out.println("");
		System.out.println("Running Simulation:");
		System.out.println("-------------------");
				
		// Create agents
		List<Agent> agents = new ArrayList<Agent>(no_agents);
		
		for (int i = 0; i<no_agents; i++) {
			Valuation v = null;
			
			if (valuation == 0)
				v = new UniformAdditiveValuation(no_auctions);
			else if (valuation == 1)
				v = new PerfectSubstitutesValuation(no_auctions);
			else if (valuation == 2)
				v = new PerfectComplementsValuation(no_auctions);
			else
				System.out.println("Invalid Valuation Model");
			
			Agent a = null;
			
			if (bid_logic == 0)
				a = new RandomAgent(i, v);
			else
				a = new NaiveValueAgent(i, v);
		
			agents.add(a);
		}

		// Create individual SB auctions
		List<SBAuction> auctions = new ArrayList<SBAuction>(no_auctions);
		for (int i = 0; i<no_auctions; i++) {
			// Choose starting ask price & epsilon. For single-shot auctions, ask_price should be 0,
			// or otherwise it will function as a known reserve price (in addition to the hidden
			// reserve_price), and ask_epsilon is essentially ignored.
			
			double ask_price = 0; // starting ask price.
			double ask_epsilon = 0.1; // for ascending/descending auctions, the epsilon amount per round
			
			if (pay == 0)
				auctions.add(new SBAllPayAuction(i, Math.random(), ask_price, ask_epsilon, agents));
			else
				auctions.add(new SBNPAuction(i, Math.random(), ask_price, ask_epsilon, agents, pay));
		}
	
		if (style == 0) {
			SimSSSimulation sim = new SimSSSimulation(agents, auctions);
			sim.play();
			sim.report();
		} else if (style == 1) {
			SimAscSimulation sim = new SimAscSimulation(agents, auctions);
			sim.play();
		}
	}
}