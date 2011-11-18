
public class TestHistogram {
	public static void main(String[] args) {
		Histogram h = new Histogram(0.1);
		
		for (int i = 0; i<10000; i++)
			h.add(Math.random() * 10.0); // generates U[0,10)
		
		System.out.println("HISTOGRAM:");
		h.print();
		
		DiscreteDistributionWellman F = new DiscreteDistributionWellman(h.getDiscreteDistribution(), h.getPrecision());
		
		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $0:");
		F.print(0);

		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $1:");
		F.print(1);

		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $5:");
		F.print(5);

		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $9.9:");
		F.print(9.9);
		
		System.out.println("NOTE: We expect 66%/33% here because our random numbers are generated U[0,10), but histogram bins");
		System.out.println("      are generated a result of rounding values.. i.e., bin 10.0 == [9.95.10.05)");
	}
}
