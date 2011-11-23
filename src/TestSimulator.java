import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TestSimulator {
	

	public void main (String args[])
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Welcome to WJJSIM!!!");
		
		System.out.print("Enter 1 for Simultaneous Ascending, or 2 for Sequential SBSP> ");
		int style = sc.nextInt();
		System.out.print("Please enter no. of agents> ");
		int no_agents = sc.nextInt();
		System.out.print("Please enter no. of goods> ");
		int no_auctions = sc.nextInt();
		
		List<Agent> agents = new ArrayList<Agent>(no_agents);
		
		// For SAA auctions
		if (style ==1 && no_agents>0)
		{
			
			
			System.out.print  ("  Please input the agent types for each agent for SAA ");
			
			/*****Start to register each agent type*****/
			for (int i =0; i<no_agents;i++)
			{
				Agent a =null;
				Valuation v = new SchedulingValuation(no_auctions);
				
				System.out.println("Please input the type of the agent No."+i+":: [3=straightforward PP, 4=sunkaware PP, 5=distribution price]>");
				int bid_logic = sc.nextInt();
				
				//parameter k for the sunk-aware bidder
				double k = 0.0;
				//distribution arraylist for distribustion PP agent
				ArrayList<DiscreteDistribution> pd = new ArrayList<DiscreteDistribution>();
				
				if (bid_logic == 3)
				{
					a = new StraightforwardPPAgent(i, v); 
				}
				else if (bid_logic == 4)	
				{
					System.out.print("Enter [0, 1] k-value for sunk-aware bidder [0=fully sunk, 1=straight]> ");
					k = sc.nextDouble();
					a = new SunkawarePPAgent(i, v, k);
				}
				else if (bid_logic == 5)
				{
					System.out.println("WARNING: The same uniform (1,50) distribution set will be used across all agents.");
					Histogram h = new Histogram(1);
					for (int j = 0; j<no_auctions; j++) {
						for (int x = 0; x<128; x++)
							h.add(Math.floor((Math.random() * 50)) + 1); // generates 1,50
					
						pd.add((DiscreteDistribution)new DiscreteDistributionWellman(h.getDiscreteDistribution(), 1));
					}
					a = new DistributionPPAgent(i, v, pd);
				}
				else
					System.out.println("Invalid Bidder Logic");
				
				agents.add(a);
			}
			System.out.println("End of the registration of agent type");
			/*****end of registration for each agent type******/
			
			List<SBAuction> auctions = new ArrayList<SBAuction>(no_auctions);
			for (int i = 0; i<no_auctions; i++) {
				
				double ask_price = 0; // starting ask price.
				double ask_epsilon = 1; // for ascending/descending auctions, the epsilon amount per round
				
				//here the parameter style stands for Nth price
				auctions.add(new SBNPAuction(i, 0/*Math.random()*/, ask_price, ask_epsilon, agents, style));
				
			}
			
			
			
			SimAscSimulation s = new SimAscSimulation(agents, auctions);
			s.play();
		}
		
	}

}
