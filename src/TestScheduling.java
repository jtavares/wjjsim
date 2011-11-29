import java.util.Set;


public class TestScheduling {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SchedulingValuation valuation = new SchedulingValuation(5);

		System.out.println("VALUATION: " + valuation.getInfo());
		
		System.out.print("ALL-BASKET: ");
		printSet(valuation.getItems());
		System.out.println("");
		
		System.out.println("");
		
		double prices[] = {11, 9, 6, 4, 2}; // samples prices
		Set<Set<Integer>> ps = valuation.getPowerSetOfitems();
		
		Set<Integer> max_basket = null;
		double max_surplus = Double.NEGATIVE_INFINITY;
		for (Set<Integer> basket : ps) {
			double surplus = valuation.getValue(basket) - cost(basket, prices);
			
			if (surplus > max_surplus) {
				max_surplus = surplus;
				max_basket = basket;
			}
			
			printSet(basket);
			System.out.println(": value=" + valuation.getValue(basket) + ", cost=" + cost(basket, prices) + ", surplus=" + surplus);
		}
		
		System.out.println("");
		System.out.print("MAX: ");
		printSet(max_basket);
		System.out.println(": value=" + valuation.getValue(max_basket) + ", cost=" + cost(max_basket, prices) + ", surplus=" + max_surplus);
	}

	private static double cost(Set<Integer> basket, double[] prices) {
		double total_price = 0.0;
		
		for (Integer i : basket)
			total_price += prices[i];
		
		return total_price;
	}
	
	private static void printSet(Set<Integer> basket) {
		System.out.print("{");
		
		int total = 0;
		for (Integer i : basket) {
			if (total < (basket.size()-1))
				System.out.print(i + ",");
			else
				System.out.print(i + "");
			
			total++;
		}
		
		System.out.print("}");
	}
}
