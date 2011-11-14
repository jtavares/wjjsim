import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Create a power set of the given set

public class PowerSet {	
	public static <T> Set<Set<T>> generate(Set<T> in) {
	    Set<Set<T>> out = new HashSet<Set<T>>();

        // If orig is empty, we're done.
        if (in.isEmpty()) {
            out.add(new HashSet<T>());
	        return out;
		}
	
	    ArrayList<T> in_list = new ArrayList<T>(in);
	    
	    T head = in_list.get(0);
	    
	    // Recursively find power set of the tail of the list, and append to
	    // the each subset to the head item.

	    Set<T> tail = new HashSet<T>(in_list.subList(1, in_list.size())); 
	    
	    for (Set<T> s : generate(tail)) {
	        Set<T> n = new HashSet<T>();
	        
	        n.add(head);
	        n.addAll(s);
	        
	        out.add(n); // the item and the subset
	        out.add(s); // the subset itself
	    }
	    
	    return out;
	}
}
