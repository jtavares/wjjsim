import java.util.Set;


public class PureSubstitutesValuation extends Valuation {
	// A valuation function where value 1 is obtained when AT LEAST ONE good is obtained, but NEVER more. 0 if no goods obtained.
	
	public PureSubstitutesValuation(int n) {
		super(n);
	}

	@Override
	public double getValue(Set<Integer> basket) {
		if (basket.size() == 0)
			return 0;
		else
			return 1;
	}

	@Override
	public double getValue(int n) {
		return 1;
	}
}
