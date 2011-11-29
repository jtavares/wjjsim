
public class TestHistogramTwo {
	public static void main (String args[])
	{
		Histogram h = new Histogram(1);
		h.add(1.1);
		h.add(12.2);
		h.add(3.3);
		h.add(3.4);
		
		h.add(13.3);
		
		h.print();
	}
}
