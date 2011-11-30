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
		if (basket.size() == 0)
			return 0.0;
		else if (basket.size() == 1){
			if (basket.contains(0) == true)
				return 50;
			else
				return 100;
		}
		else
			return 150.0;
	}
	
	// Useless overrides... for formality purposes
	@Override
	public double getValue(int n) {
		return values[n];
	}
	
	@Override
	public String getInfo(){
		return "Test Valuation";
	}


}
