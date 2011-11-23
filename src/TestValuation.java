import java.util.HashSet;
import java.util.Set;

public class TestValuation extends Valuation {
	// Only used for testing purposes!!! v({})=0, otherwise v = 1. 
	
	double[] values;
	
	TestValuation(int n) {
		super(n);		
		
		values = new double[no_valuations];
	}

	@Override
	public double getValue(Set<Integer> basket) {
		
		// Four possible sets
		Set<Integer> S1 = new HashSet<Integer>();
		Set<Integer> S2 = new HashSet<Integer>();
		Set<Integer> S3 = new HashSet<Integer>();
		Set<Integer> S4 = new HashSet<Integer>();
		
		S2.add(1);
		S3.add(2);
		S4.add(1);
		S4.add(2);
		
		double total_value;
		
		if (basket == S1){
			total_value = 0.0;
		}
		else {
			total_value = 1.0;
		}
		
		return total_value;
	}
	
	// Useless overrides... 
	
	@Override
	public double getValue(int n) {
		return values[n];
	}
	
	@Override
	public String getInfo(){
		return "Test Valuation";
	}


}
