import java.util.ArrayList;

public class Histogram {
	double precision; 
	long denom;
	ArrayList<Integer> h;

	// precision is how prices are mapped to index of the vector.
	// for example:
	//    precision=0.1  ==> one decimal place (e.g., 1.23/0.1 == 12.3 == idx 12)
	//    precision=1.0  ==> integer (e.g. 14.67/1.0 == 14.67 == idx 15)
	//    precision=10.0 ==> ten's place (e.g., 12.44/10 == 1.244 == idx 1)
	
	public Histogram(double precision) {
		this.h = new ArrayList<Integer>();
		this.precision = precision;
		this.denom = 0;
	}
	
	// add a hit to the histogram
	public void add(double p) {
		int idx = (int) Math.round(p / precision);
		
		// Ignore values < 0 -- these make no sense for prices AND have special meaning
		// in our framework (price of -1 says nobody has won).
		if (p < 0)
			return;
		
		while (idx >= h.size())
			h.add(0);
		
		h.set(idx, h.get(idx)+1);
		
		denom++;
	}
	
	public ArrayList<Double> getDiscreteDistribution() {
		ArrayList<Double> pd = new ArrayList<Double>(h.size());
		
		for (Integer i : h)
			pd.add(i / (double)denom);
	
		return pd;
	}
	
	public double getPrecision() {
		return precision;
	}
	
	public void print() {
		for (int i = 0; i<h.size(); i++)
			System.out.println(i + ": " + h.get(i) + " (" + (h.get(i) / (double)denom * 100) + "%)");
	}
}
