import java.util.ArrayList;
import java.util.Set;


public class PureComplimentsValuation extends Valuation {
	// A valuation function where value 1 is obtained when ALL the goods are secured, or 0 otherwise.

	ArrayList<Integer> allAuctions;
	
	public PureComplimentsValuation(int n) {
		super(n);

		// Valuations for strict subsets of the total basket return 0. Valuations for the entire set return 1.
		allAuctions = new ArrayList<Integer>(no_valuations);
		
		for (int i = 0; i<no_valuations; i++)
			allAuctions.add(i);
	}

	@Override
	public double getValue(Set<Integer> basket) {
		if(basket.containsAll(allAuctions))
			return 1;
		else
			return 0;
	}

	@Override
	public double getValue(int n) {
		// if the auction size is 1, and we're requesting value for auction 0, then we do have value 1.
		if (no_valuations == 1 && n == 0)
			return 1;
		else
			return 0;
	}

}
