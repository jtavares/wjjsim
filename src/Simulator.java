import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Simulator {
	public static void main(String args[]) {	
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Welcome to WJJSIM.");
		System.out.println("------------------");
		System.out.println("");	
		System.out.println("You are simulating a simultaneous sealed-bid auction.");
		System.out.println("");
		
		System.out.print("Enter 0 for all-pay, or 1 thru n for n-th price> ");	
		int pay = sc.nextInt();
		
		System.out.print("Agent bid logic [0=random, 1=straight value]> ");
		int bid_logic = sc.nextInt();
		
		System.out.print("Agent valuation model [0=uniform additive, 1=substitutes, 2=compliments]> ");
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
				v = new PureSubstitutesValuation(no_auctions);
			else if (valuation == 2)
				v = new PureComplimentsValuation(no_auctions);
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
		List<Auction> auctions = new ArrayList<Auction>(no_auctions);
		for (int i = 0; i<no_auctions; i++) {
			if (pay == 0)
				auctions.add(new SBAllPayAuction(i, Math.random(), agents));
			else
				auctions.add(new SBNPAuction(i, Math.random(), agents, pay));
		}

		SSBSimulation sim = new SSBSimulation(agents, auctions);
		sim.play();
		sim.report();
	}
}