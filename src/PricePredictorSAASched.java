import java.util.ArrayList;

public class PricePredictorSAASched extends PricePredictor {
	int no_agents;
	int nth_price;
	double ask_price;
	double ask_epsilon;
	
	PricePredictorSAASched(int no_agents, int no_auctions, int nth_price,
			double ask_price, double ask_epsilon, 
			int no_per_iteration, int max_iterations,
			int avg_iterations, double ks_threshold, double precision) {
		super(no_auctions, no_per_iteration, max_iterations, avg_iterations, ks_threshold, precision);

		this.no_agents = no_agents;
		this.ask_price = ask_price;
		this.ask_epsilon = ask_epsilon;
		this.nth_price = nth_price;
	}

	// Create new agents with new valuations and play the auction when we have yet to have a price prediction.
	// This is the bootstrap phase. We use Straightforward agents.
	@Override
	protected void createAndPlayInitialAuction() {
		// First, create the agents.
		agents = new ArrayList<Agent>(no_agents);
		for (int i = 0; i<no_agents; i++)
			agents.add(new StraightforwardPPAgent(i, new SchedulingValuation(no_auctions)));
		
		// Second, create the individual auctions for each item.
		auctions = new ArrayList<SBAuction>(no_auctions);
		
		for (int i = 0; i<no_auctions; i++)		
			auctions.add(new SBNPAuction(i, ask_price, ask_price, ask_epsilon, agents, nth_price));
		
		// Third, play the simulation.
		SimAscSimulation s = new SimAscSimulation(agents, auctions);
		s.play(true);
	}
	
	// Create new agents (with new valuations) and play the auction when we have an existing price prediction.
	// We use Wellman's DistributionPPAgent.
	@Override
	protected void createAndPlayPPAuction(ArrayList<DiscreteDistribution> pp) {
		// First, create the agents.
		agents = new ArrayList<Agent>(no_agents);
		for (int i = 0; i<no_agents; i++)
			agents.add(new DistributionPPAgent(i, new SchedulingValuation(no_auctions), pp));
		
		// Second, create the individual auctions for each item.
		auctions = new ArrayList<SBAuction>(no_auctions);
		
		for (int i = 0; i<no_auctions; i++)		
			auctions.add(new SBNPAuction(i, ask_price, ask_price, ask_epsilon, agents, nth_price));
		
		// Third, play the simulation.
		SimAscSimulation s = new SimAscSimulation(agents, auctions);
		s.play(true);
	}

	// Use the Wellman Discrete Distribution.
	@Override
	protected DiscreteDistribution createDiscreteDistribution(
			ArrayList<Double> f) {
		return new DiscreteDistributionWellman(f, precision);
	}

}
