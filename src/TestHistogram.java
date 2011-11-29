
public class TestHistogram {
	public static void main(String[] args) {
		Histogram h = new Histogram(1);
		
		for (int i = 0; i<10000; i++)
			h.add(Math.floor(Math.random() * 50.0) + 1); // generates U(1,50)
		
		System.out.println("HISTOGRAM:");
		h.print();
		
		DiscreteDistributionWellman F = new DiscreteDistributionWellman(h.getDiscreteDistribution(), h.getPrecision());
				
		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $0:");
		F.print(0);

		System.out.println("CDF($0 | $0)=" + F.getCDF(0, 0) * 100.0 + "%");
		System.out.println("CDF($1 | $0)=" + F.getCDF(1, 0) * 100.0 + "%");
		System.out.println("CDF($2 | $0)=" + F.getCDF(2, 0) * 100.0 + "%");
		
		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $1:");
		F.print(1);

		System.out.println("CDF($0 | $1)=" + F.getCDF(0, 1) * 100.0 + "%");
		System.out.println("CDF($1 | $1)=" + F.getCDF(1, 1) * 100.0 + "%");
		System.out.println("CDF($2 | $1)=" + F.getCDF(2, 1) * 100.0 + "%");

		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $2:");
		F.print(2);

		System.out.println("CDF($0 | $2)=" + F.getCDF(0, 2) * 100.0 + "%");
		System.out.println("CDF($1 | $2)=" + F.getCDF(1, 2) * 100.0 + "%");
		System.out.println("CDF($2 | $2)=" + F.getCDF(2, 2) * 100.0 + "%");

		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $5:");
		F.print(5);

		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $49:");
		F.print(49);
		
		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $50:");
		F.print(50);

		System.out.println("");
		System.out.println("WELLMAN DISTRIBUTION | $51:");
		F.print(51);
		
		System.out.println("Expected Final Price:");
		for (int i = 0; i<55; i++)
			System.out.println("|$" + i + ": " + F.getExpectedFinalPrice(i));

	}
}
