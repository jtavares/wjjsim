import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

//Implements agent for Sequential SPSB auction based on MDP. Details of MDP is included in J's write up. 

public class SeqSSMDP extends Agent {
ArrayList<DiscreteDistribution> pd;
	
	public SeqSSMDP(int agent_idx, Valuation valuation, ArrayList<DiscreteDistribution> pd) {
			super(agent_idx, valuation);
			this.pd = pd;
	}
		// Declare some variables. (X,t) is a state in MDP. Meaning: the set of goods obtained at step/auction t is X 

		HashMap<X_t,Double> V = new HashMap<X_t,Double>();			// Value function V((X,t))
		HashMap<X_t,Double> pi = new HashMap<X_t,Double>();			// optimal bidding function \pi((X,t))
		ArrayList<Double> b = new ArrayList<Double>();
		ArrayList<Double> Q = new ArrayList<Double>();
		int no_slots = valuation.getNoValuations(), t, max_idx;
		double temp, bid, optimal_bid, max_value;
		X_t x_t,x_t1,x_t2;
		Set<Integer> X = new HashSet<Integer>();
		Set<Integer> X_more = new HashSet<Integer>();
		
	// Ask the agent to computes optimal bidding policy \pi((X,t)) using MDP. The two steps correspond to the two steps in write-up	
	public void computeMDP(){	
		
		// 1) ******************************** Initialize V values for t = no_slots (number of goods)
		t = no_slots;
		
		// Start from the whole set, and assign V values to all its power sets
		Set<Integer> remaining_set = new HashSet<Integer>();
		for (int i = 0; i< no_slots; i++){
			remaining_set.add(i);
		}
		Set<Set<Integer>> genSet = PowerSet.generate(remaining_set);
		
		Iterator<Set<Integer>> iterator1 = genSet.iterator();
		while (iterator1.hasNext()) {
			X=iterator1.next();
			x_t = new X_t(X,t);
			V.put(x_t,valuation.getValue(X));
		}
		
		// 2) ******************************** Recursively assign values for t = no_slots-1,...,1
		
		// 2.1) Iterate over auction t
		for (t = no_slots; t>-1; t--){ 
			
			// Generate ArrayList of bids we want to test. Specifically, we want to test b = {0,(p_1+p_2)/2,...,(p_{max-1}+p_max)/2,p_max+1}
			DiscreteDistribution p = pd.get(t);
			b.clear();
			b.add((double) 0);
			for (int i = 0; i < p.f.size() - 1; i++){
				b.add(p.precision*(i+(i+1))/2);		// bid = (p_{i}+p_{i+1})/2
			}
			b.add(p.precision*(p.f.size()-1)+1);	// bid = p_{max}+1
			
			remaining_set.remove(t);
			genSet = PowerSet.generate(remaining_set);
			Iterator<Set<Integer>> iterator2 = genSet.iterator();
			
			// 2.2) Iterate over subsets of {0,...,t-1}
			while (iterator2.hasNext()) {
		    	X=iterator2.next();
		    	
		    	// Compute Q((X,t),b) for each bid. Iterative sum into variable temp
		    	for (int i = 0; i < b.size(); i++){
		    		bid = b.get(i);		// for the i^th bid
		    		Q.clear();
		    		temp=0;
		    		// Sum R((X,t),b) using "temp"
		    		for (int j = 0; j < i; j++){
		    			temp += -(j*p.precision)*p.f.get(j);	// add -p*f(p)
		    		}
		    		
		    		X_more = X;
		    		X_more.add(t);
		    		x_t1 = new X_t(X_more,t+1);
		    		x_t2 = new X_t(X,t+1);
		    		temp += p.getCDF(bid, (double) 0) * V.get(x_t1) + (1-p.getCDF(bid, (double) 0)) * V.get(x_t2);
		    		Q.add(temp);
		    	}
		    	
		    	// Find \pi_((X,t)) = argmax_b Q((X,t),b)
		    	max_value = Q.get(0);		// Value of largest Q((X,t),b)
		    	max_idx = 0;				// Index of largest Q((X,t),b)
		    	for (int i = 1; i < Q.size(); i++) {
		    		if (Q.get(i) > max_value) {	// Compare
		    			max_value = Q.get(i);
		    			max_idx = i;
		    		}
		    	}
		    	
		    	// Now we found the optimal bid for state (X,t). Assign values to \pi((X,t))
	    		x_t = new X_t(X,t);
		    	pi.put(x_t,b.get(max_idx));
		    
		    // Speaking to myself: I think this works. No need to use b = V((X \cup {x_{t+1}},t+1)) - V((X,t))... 
		    
		    }
		}
	}	

	// This getBids need to be called once in each SPSB auction
	@Override
	public HashMap<Integer, Double> getBids() {

		// Figure out which auction is currently open? (There can be only one open auction at a time)
		Iterator<Integer> iterator3 = openAuctions.iterator();
		int current_auction = iterator3.next();
		HashMap<Integer,Double> bids = new HashMap<Integer,Double>(current_auction);
		
		Set<Integer> goods_won = new HashSet<Integer>();
		X_t state;			// Current state (X,t)
		if (current_auction == 0){
			state = new X_t(goods_won,0);	// Current State ({},0)
			bids.put(0, pi.get(state));			
		}
		else{
			// Figure out which of past auctions we have won
			for (int i = 0; i < current_auction; i++){
				if (results.get(i).getIsWinner() == true){
					goods_won.add(i);
				}
			}
			state = new X_t(goods_won,current_auction);
			bids.put(current_auction,pi.get(state));
		}
		
		return bids;
	}

}


	
	

