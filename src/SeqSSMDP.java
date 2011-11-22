import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SeqSSMDP {
// Implements MDP for Sequential SPSB auction
	int no_slots, t;

	public SeqSSMDP(ArrayList<DiscreteDistribution> F, SchedulingValuation v) {
		// (X,t) = state: the set of goods obtained at time t is X 
		no_slots = v.getNoValuations();
		HashMap V = new HashMap();			// Value function V((X,t))
		HashMap pi = new HashMap();			// optimal bidding function \pi((X,t))
		// 1) ******************************** Initialize V values for t = M (number of goods)
		
		// Start from the whole set, and assign V values
		Set<Integer> S = new HashSet<Integer>();
		for (int i = 0; i< no_slots; i++){
			S.add(i);
		}
		Set<Set<Integer>> genSet = PowerSet.generate(S);
		
		t = no_slots;
		for (Set<Integer> X : genSet){
				X_t x_t = new X_t(X,t);
				V.put(x_t,v.getValue(X));
		}

		// 2) ******************************** Recursively assign values back
		for (t = no_slots; t>-1; t--){	// we can decrease the index in the loop right? 
			S.remove(t);
			genSet = PowerSet.generate(S);
			
			
			for (Set<Integer> X : genSet){
				
			}
		}
	
	
	}
	
	
}
