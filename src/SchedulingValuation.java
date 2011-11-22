import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

// NOTE: We number time slots [0, ..., M-1] rather than the [1, ..., M] specified in the assignment hand out.
// NOTE: Deadline (t_j) is assumed to equal M; in other words, there is no deadline.

public class SchedulingValuation extends Valuation {
	int no_slots_req; 				// number of slots required, \lambda_{j}, [1, ..., M]
	LinkedList<Integer> values;		// v(t)
	
	public SchedulingValuation(int no_slots) {
		super(no_slots);

		// Draw no. of required slots from [1, ..., no_slots]
		no_slots_req = (int) (1 + Math.random() * no_slots); 
				
		// Produce no_slots values from [1, ..., 50]
		values = new LinkedList<Integer>();
		for (int i = 0; i < no_slots; i++)
			values.add((int) (1 + Math.random() * 50));

		// Sort values in decreasing order
		Collections.sort(values);
		Collections.reverse(values);
				
		// NOTE: no need to set values > t_j to zero because t_j is equal no_slots (M). (i.e., there is no deadline--
		//       an agent always gets utility if it completes its task)
	}
	
	@Override
	public double getValue(Set<Integer> basket) {
		// If an insufficient number of timeslots were won, value is zero (the task was not completed)
		if (basket.size() < no_slots_req)
			return 0;
		
		// invariant: basket.size() >= 1
		
		// Otherwise; return v(t) where t = \lambda_j-th item in the basket
		LinkedList<Integer> won = new LinkedList<Integer>();
		won.addAll(basket);
		Collections.sort(won);
		
		return values.get(won.get(no_slots_req-1));
	}

	@Override
	public double getValue(int n) {
		// If an insufficient number of timeslots were won, value is zero
		if (1 < no_slots_req)
			return 0;

		return values.get(n-1);
	}

	@Override
	public String getInfo() {
		String v_t = "";
		
		for (Integer v : values) {
			if (!v_t.equals(""))
				v_t += ",";
			
			v_t += v;
		}
		
		return "Scheduling: no_slots_req=" + no_slots_req + ", v(t)={" + v_t + "}";
	}

}
