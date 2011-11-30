import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// Backup of old SeqSSMDPAgent 

public class SeqSSMDPAgent2 extends Agent {
ArrayList<DiscreteDistribution> pd;

	public SeqSSMDPAgent2(int agent_idx, Valuation valuation, ArrayList<DiscreteDistribution> pd) {
			super(agent_idx, valuation);
			this.pd = pd;
			
			// Do MDP calculation when initiating agent
			computeMDP();
			
			// Print out the mapping \pi: state --> optimal bid
			//System.out.println("\nAgent " + agent_idx + ": I have done my MDP computation and here is my /pi mapping: ");
			//for (X_t key : pi.keySet())
			//	System.out.println("pi(" + key.toString() + ") --> " + pi.get(key));
	}
		// Declare some variables. (X,t) is a state in MDP. Meaning: the set of goods obtained at step/auction t is X 
		HashMap<X_t,Double> V = new HashMap<X_t,Double>();			// Value function V((X,t))
		HashMap<X_t,Double> pi = new HashMap<X_t,Double>();			// optimal bidding function \pi((X,t))
		ArrayList<Double> b = new ArrayList<Double>();
		ArrayList<Double> Q = new ArrayList<Double>();
		int no_slots = valuation.getNoValuations(), t, max_idx;
		double R_base, temp, bid, optimal_bid, max_value;
		X_t x_t,x_t1,x_t2;
		Set<Integer> X = new HashSet<Integer>();
		Set<Integer> X_more = new HashSet<Integer>();
		
	// Ask the agent to computes optimal bidding policy \pi((X,t)) using MDP. The two steps correspond to the two steps in write-up	
	public void computeMDP(){	
		
		// 1) ******************************** Initialize V values for t = no_slots; corresponding to after all auctions are closed. 
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
			V.put(x_t,valuation.getValue(X));		// This is just the valuation assigned 
		}
		
		// 2) ******************************** Recursively assign values for t = no_slots-1,...,1
		
		// Iterate over auction t
		for (t = no_slots-1; t>-1; t--){ 
		
			R_base = 0.0;	// baseline of R values
			// Generate ArrayList of bids we want to test. Specifically, we want to test b = {0,p_1/2,(p_1+p_2)/2,...,(p_{max-1}+p_max)/2,p_max+1}
			DiscreteDistribution p = pd.get(t);
			b.clear();
			b.add(0.0);
			for (int i = 0; i < p.f.size(); i++){
				b.add(p.precision* ((double) (i+(i+1))/2 - 0.1) );		// bid = (p_{i}+p_{i+1})/2 - 0.1*precision
			}
			
			// Iterate over subsets of goods {0,...,t-1}
			remaining_set.remove(t);
			genSet = PowerSet.generate(remaining_set);
			Iterator<Set<Integer>> iterator2 = genSet.iterator();
			while (iterator2.hasNext()) {
		    	X=iterator2.next();
		    	x_t = new X_t(X,t);
				Q.clear();
		    	
		    	// Compute Q((X,t),b) for each bid. Q() is a sum, which is stored in variable temp
		    	for (int i = 0; i < b.size(); i++){
		    		temp = 0;
		    		// Compute R((X,t),b)
		    		for (int j = 0; j < i; j++){
		    			temp += -(j*p.precision)*p.f.get(j);	// add -p*f(p)
		    		}

		    		// Compute the rest 2 components
		    		X_more = new HashSet<Integer>();
		    		X_more.addAll(X);
		    		X_more.add(t);
		    		x_t1 = new X_t(X_more,t+1);
		    		x_t2 = new X_t(X,t+1);
		    		temp += p.getCDF(b.get(i), (double) 0) * V.get(x_t1) + (1-p.getCDF(b.get(i), (double) 0)) * V.get(x_t2);

		    		// Add this value to Q
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
		    	
		    	// Now we found the optimal bid for state (X,t). Assign values to \pi((X,t)) and V((X,t))
		    	V.put(x_t,Q.get(max_idx));
	    		pi.put(x_t,b.get(max_idx));
		    }
		}
	}	

	// This getBids need to be called once in each SPSB auction
	@Override
	public HashMap<Integer, Double> getBids() {

		
		// Figure out which auction is currently open? (There can be only one open auction at a time)
		Iterator<Integer> iterator3 = openAuctions.iterator();
		int current_auction = iterator3.next();
		
		// Figure out what we have won in the past
		Set<Integer> goods_won = new HashSet<Integer>();
		for (int i = 0; i < current_auction; i++){
			if (results.get(i).getIsWinner() == true){
				goods_won.add(i);
			}
		}
	
		X_t state = new X_t(goods_won,current_auction);			// Current state (X,t)
		
		HashMap<Integer, Double> bids = new HashMap<Integer, Double>();
		bids.put(current_auction, pi.get(state));
		
		return bids;
	}

}
