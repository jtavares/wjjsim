
public class TestHistogramTwo {
	public void main (String args[])
	{
		Histogram h = new Histogram(0.1);
		h.add(1.1);
		h.add(2.2);
		h.add(3.3);
		
		h.print();
	}
}
