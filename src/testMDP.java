import java.util.ArrayList;
import java.util.List;

// Runs a SeqSSSimulation3 Simulation to test if SeqSSMDPAgent is working
public class testMDP {
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
	
		// Create agents
		List<SeqSSMDPAgent> agents = new ArrayList<SeqSSMDPAgent>(no_agents);
		
		// Create valuations
		Valuation v = 
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

		
	}
}
	
