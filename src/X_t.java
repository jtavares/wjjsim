import java.util.Set;

public class X_t {
	Set<Integer> X;
	int t;
	
	// This class implements the (X,t) pair in MDP iteration, where X is the set of goods obtained, and t is the time
	public X_t (Set<Integer> X, int t){
		this.X = X;
		this.t = t;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		
		if (! (that instanceof X_t))
			return false;
		
		X_t aThat = (X_t)that;
		
		return this.t == aThat.t && this.X.containsAll(aThat.X) && aThat.X.containsAll(this.X);
	}
	
	@Override
	public int hashCode() {
		int hash = 0;

		for (Integer a : X)
			hash += a;
		
		return hash * (t+1); 
	}
	
	// prints this X_t
	public String toString() {
		String message = "({";
		for (int i : X){
			message+=" " ;
			message+=i;
		}
		message=message+"},"+t+")";
		return message;
	}
}
