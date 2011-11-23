import java.util.HashSet;
import java.util.Set;

public abstract class testValuation extends Valuation {
	// Only used for testing purposes!!! v({})=0, otherwise v = 1. 

	double[] values;
	
	public testValuation(int n) {
		super(n);		
		values = new double[no_valuations];
	}
	
	// Four possible sets
	Set<Integer> S1 = new HashSet<Integer>();
	Set<Integer> SS = new HashSet<Integer>();
	Set<Integer> S3 = new HashSet<Integer>();
	Set<Integer> S4 = new HashSet<Integer>();
	
	S4.add(1);
	
	add(1);
	
	
	@Override
	public double getValue(Set<Integer> basket) {
		double total_value;
		if (basket == S1){
			total_value = 0.0;
		}
		else {
			total_value = 1.0;
		}
		
		return total_value;
	}

//	@Override
//	public double getValue(int n) {
//		return values[n];
//	}
}
