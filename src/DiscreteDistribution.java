import java.util.ArrayList;


public abstract class DiscreteDistribution {
	protected ArrayList<Double> F;
	protected double precision;
	
	// F is a vector of probabilities. The sum(F) must equal 1.
	// precision is how prices are mapped to index of the vector.
	// for example:
	//    precision=0.1  ==> one decimal place (e.g., 1.23/0.1 == 12.3 == idx 12)
	//    precision=1.0  ==> integer (e.g. 14.67/1.0 == 14.67 == idx 15)
	//    precision=10.0 ==> ten's place (e.g., 12.44/10 == 1.244 == idx 1)
	public DiscreteDistribution(ArrayList<Double> F, double precision) {
		this.F = F;
		this.precision = precision;
	}
	
	public double getProb(double p, double b) {
		return getProb(bin(p), b);
	}

	public void print (double b) {
		for (int i = 0; i<F.size(); i++)
			System.out.println((i*precision) + ": " + (getProb((int)i*precision, b) * 100) + "%");
	}
	
	public double getPrecision() {
		return precision;
	}
	
	protected abstract double getProb(int idx, double b);

	protected int bin(double p) {
		return (int) Math.round(p / precision);
	}
	
	protected double val(int idx) {
		return idx * precision;
	}
}
