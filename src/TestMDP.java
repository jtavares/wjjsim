import java.util.ArrayList;
import java.util.List;

// Runs a SeqSSSimulation3 Simulation to test if SeqSSMDPAgent is working
public class TestMDP {
	public static void main(String args[])
	{		
		
	// Create a simple price distribution for testing purposes
		ArrayList<DiscreteDistribution> pd = new ArrayList<DiscreteDistribution>();
		
		int no_auctions=2;
		int no_agents = 2;
		
		Histogram h1 = new Histogram(1);
		Histogram h2 = new Histogram(1);

		h1.add(1);
		h1.add(2);
		h2.add(1);
		h2.add(2);
		
		pd.add((DiscreteDistribution) new DiscreteDistributionWellman(h1.getDiscreteDistribution(), 1));
		pd.add((DiscreteDistribution) new DiscreteDistributionWellman(h2.getDiscreteDistribution(), 1));
	
		
		// Create valuations		
		Valuation v = null;
		v = new TestValuation(no_auctions);		

		// Create agents
		List<Agent> agents = new ArrayList<Agent>(no_agents);
	
		for (int i = 0; i < no_agents; i++){
			agents.add(new SeqSSMDPAgent(i,v,pd));
		}		
		
		// Create individual SB auctions
		List<SBAuction> auctions = new ArrayList<SBAuction>(no_auctions);
		for (int i = 0; i<no_auctions; i++) {
			double ask_price = 0; // starting ask price.
			double ask_epsilon = 1; // for ascending/descending auctions, the epsilon amount per round
			int pay = 2;		

			auctions.add(new SBNPAuction(i, 0/*Math.random()*/, ask_price, ask_epsilon, agents, pay));
		}

		// Play the auction
		SeqSSSimulation s = new SeqSSSimulation(agents,auctions);
		s.play();
			
	}
}
