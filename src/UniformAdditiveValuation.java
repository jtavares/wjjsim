
public class UniformAdditiveValuation extends AdditiveValuation {
	// Produces a valuation with values randomly and uniformly assigned from U[0,1].
	// Baskets are straight additive, per AdditiveValuation subclass.
	UniformAdditiveValuation(int n) {
		super(n);
		
		for (int i = 0; i<no_valuations; i++)
			values[i] = Math.random();
	}

	@Override
	public String getInfo() {
		return "UniformAdditive";
	}
}
