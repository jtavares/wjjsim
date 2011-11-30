import java.util.ArrayList;

public class PricePredictorSeqSSMDP extends PricePredictor {
	int no_agents;
	int nth_price;
	double reserve_price;
	
	PricePredictorSeqSSMDP(int no_agents, int no_auctions, int nth_price,
			double reserve_price, 
			int no_per_iteration, int max_iterations,
			int avg_iterations, double ks_threshold, double precision) {
		super(no_auctions, no_per_iteration, max_iterations, avg_iterations, ks_threshold, precision);

		this.no_agents = no_agents;
		this.reserve_price = reserve_price;
		this.nth_price = nth_price;
	}

	// Create new agents with new valuations and play the auction when we have yet to have a price prediction.
	// This is the bootstrap phase.
	
	// Here we play straightforwarding bidder under a simultaneous ascending auction to determine initial prices.
	
	// Future work may be to implement a sequential bidder which does not rely on price predictions.
	
	@Override
	protected void createAndPlayInitialAuction() {
		double saa_ask_price = 1; 	// starting ask price
		double saa_ask_epsilon = 1;	// ask epsilon
		int saa_nth_price = 1;	// 1st price
		
		// First, create the agents.
		agents = new ArrayList<Agent>(no_agents);
		for (int i = 0; i<no_agents; i++)
			agents.add(new StraightforwardPPAgent(i, new SchedulingValuation(no_auctions)));
		
		// Second, create the individual auctions for each item.
		auctions = new ArrayList<SBAuction>(no_auctions);
		
		for (int i = 0; i<no_auctions; i++)
			auctions.add(new SBNPAuction(i, reserve_price, saa_ask_price, saa_ask_epsilon, agents, saa_nth_price));
		
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
			agents.add(new SeqSSMDPAgent(i, new SchedulingValuation(no_auctions), pp));
		
		// Second, create the individual auctions for each item.
		auctions = new ArrayList<SBAuction>(no_auctions);
		
		for (int i = 0; i<no_auctions; i++)		
			auctions.add(new SBNPAuction(i, reserve_price, 0, 0, agents, nth_price));
		
		// Third, play the simulation.
		SeqSSSimulation s = new SeqSSSimulation(agents, auctions);
		s.play(true);
	}

	// Use the Wellman Discrete Distribution. Note that this is important for our initial
	// prediction (which is done using simultaneous ascending auctions). For the iterations
	// where we use sequential auctions, it does not matter if we use wellman or static
	// because the sequential auctions are single-shot (and therefore no price updating occurs).
	@Override
	protected DiscreteDistribution createDiscreteDistribution(
			ArrayList<Double> f) {
		return new DiscreteDistributionWellman(f, precision);
	}
}
