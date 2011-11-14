import java.util.HashSet;
import java.util.Set;

// Simple test for the PowerSet.generate() utility

public class TestPowerSet {
	public static void main(String args[]) {
		// Test that PowerSet successfully creates the non-trivial PowerSet of {1,2,3}
		Set<Set<Integer>> testSet = new HashSet<Set<Integer>>();
		
		Set<Integer> a = new HashSet<Integer>();
		Set<Integer> b = new HashSet<Integer>();
		Set<Integer> c = new HashSet<Integer>();
		Set<Integer> d = new HashSet<Integer>();
		Set<Integer> e = new HashSet<Integer>();
		Set<Integer> f = new HashSet<Integer>();
		Set<Integer> g = new HashSet<Integer>();
		Set<Integer> h = new HashSet<Integer>();
		
		a.add(1);

		b.add(1);
		b.add(2);

		c.add(1);
		c.add(3);

		d.add(1);
		d.add(2);
		d.add(3);

		e.add(2);

		f.add(2);
		f.add(3);
		
		g.add(3);

		// h is empty
		
		testSet.add(a);
		testSet.add(b);
		testSet.add(c);
		testSet.add(d);
		testSet.add(e);
		testSet.add(f);
		testSet.add(g);
		testSet.add(h);
		
		// Generate a power set.
		Set<Integer> set = new HashSet<Integer>();
		set.add(1);
		set.add(2);
		set.add(3);

		Set<Set<Integer>> genSet = PowerSet.generate(set);
		
		if (testSet.containsAll(genSet) && genSet.containsAll(testSet)) {
			System.out.println("Success! PowerSet implementation works.");
		} else {
			System.out.println("Error! PowerSet implementation fails.");
		}
	}
}
