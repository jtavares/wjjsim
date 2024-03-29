import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Simulator {
	public static void main(String args[]) {		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Welcome to WJJSIM!!!");
		System.out.println("------------------");
		System.out.println("");	

		System.out.print("Enter 0 for simultaneous, or 1 for sequential auction> ");
		int group = sc.nextInt();
		
		System.out.print("Enter 0 for single-shot, 1 for ascending, or 2 for descending> ");
		int style = sc.nextInt();
		
		if (style == 2)		
			System.out.print("Enter 1 thru n for n-th price> ");
		else
			System.out.print("Enter 0 for all-pay, or 1 thru n for n-th price> ");
				
		int pay = sc.nextInt();
		
		System.out.println("Agent bid logic Naive:       [0=random, 1=straight value, 2=straightforward-naive]");
		System.out.print  ("                Wellman SAA: [3=straightforward PP, 4=sunkaware PP, 5=distribution price]> ");
		int bid_logic = sc.nextInt();

		double k = 0.0;
		if (bid_logic == 4) {
			System.out.print("Enter [0, 1] k-value for sunk-aware bidder [0=fully sunk, 1=straight]> ");
			k = sc.nextDouble();
		}
		
		System.out.print("Agent valuation model [0=uniform additive, 1=substitutes, 2=complements, 3=scheduling]> ");
		int valuation = sc.nextInt();

		System.out.print("Please enter no. of agents> ");
		int no_agents = sc.nextInt();
				
		System.out.print("Please enter no. of goods> ");
		int no_auctions = sc.nextInt();

		// Create distributions
		ArrayList<DiscreteDistribution> pd = new ArrayList<DiscreteDistribution>();
		if (bid_logic == 5) {
			System.out.println("WARNING: The same uniform (1,50) distribution set will be used across all agents.");
			
			Histogram h = new Histogram(1);

			for (int j = 0; j<no_auctions; j++) {
				for (int i = 0; i<128; i++)
					h.add(Math.floor((Math.random() * 50)) + 1); // generates 1,50
			
				pd.add((DiscreteDistribution)new DiscreteDistributionWellman(h.getDiscreteDistribution(), 1));
			}
		}
		
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
			else if (valuation == 3)
				v = new SchedulingValuation(no_auctions);
			else
				System.out.println("Invalid Valuation Model");
			
			Agent a = null;
			
			if (bid_logic == 0)
				a = new RandomAgent(i, v);
			else if(bid_logic == 1)
				a = new NaiveValueAgent(i, v);
			else if (bid_logic == 2)
				a = new StraightforwardNaiveAgent(i, v); 
			else if (bid_logic == 3)
				a = new StraightforwardPPAgent(i, v); 
			else if (bid_logic == 4)				
				a = new SunkawarePPAgent(i, v, k);
			else if (bid_logic == 5)
				a = new DistributionPPAgent(i, v, pd);
			else
				System.out.println("Invalid Bidder Logic");
			
			agents.add(a);
		}

		// Create individual SB auctions
		List<SBAuction> auctions = new ArrayList<SBAuction>(no_auctions);
		for (int i = 0; i<no_auctions; i++) {
			// Choose starting ask price & epsilon. For single-shot auctions, ask_price should be 0,
			// or otherwise it will function as a known reserve price (in addition to the hidden
			// reserve_price), and ask_epsilon is essentially ignored.
			
			double ask_price = 0; // starting ask price.
			double ask_epsilon = 1; // for ascending/descending auctions, the epsilon amount per round
						
			if (style == 2) {
				// descending auctions
				ask_price = Math.random();
				
				if (pay == 0)
					System.out.println("Sorry, I don't know how do play an All Pay descending auction.");
				else
					auctions.add(new SBNPDescAuction(i, Math.random(), ask_price, ask_epsilon, agents, pay));
			} else {
				// single shot or ascending
				if (pay == 0)
					auctions.add(new SBAllPayAuction(i, Math.random(), ask_price, ask_epsilon, agents));
				else
					auctions.add(new SBNPAuction(i, 0/*Math.random()*/, ask_price, ask_epsilon, agents, pay));
			}
		}
	
		if (style == 0 && group == 0) {
			//Simultaneous single-shot
			SimSSSimulation s = new SimSSSimulation(agents, auctions);
			s.play();
		} else if (style == 0 && group == 1) {
			//Sequential single-shot
			SeqSSSimulation s = new SeqSSSimulation(agents,auctions);
			s.play();
		} else if (style == 1 && group == 0) {
			//Simultaneous ascending 
			SimAscSimulation s = new SimAscSimulation(agents, auctions);
			s.play();
		} else if (style == 1 && group == 1) {
			//Sequential ascending
			SeqAscSimulation s = new SeqAscSimulation(agents, auctions);
			s.play();
		} else if (style == 2 && group == 0) {
			// Simultaneous descending
			SimDescSimulation s = new SimDescSimulation(agents, auctions);
			s.play();
		} else if (style == 2 && group == 1) {
			// Sequential descending
			SeqDescSimulation s = new SeqDescSimulation(agents, auctions);
			s.play();
		}
	}
}
