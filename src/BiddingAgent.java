import java.util.ArrayList;
import java.util.UUID;
import java.util.Vector;

public class BiddingAgent {
	String agent_id;
	String agent_name;
	double[] valuation_list;
	double[] bid_list;
	Vector<Result> belongings;
	int item_number;
	
	public BiddingAgent(int auction_number)
	{
		item_number=auction_number;
		String agent_id=UUID.randomUUID().toString();
		belongings=new Vector<Result>();
	}
	public BiddingAgent(String name,int auction_number)
	{
		String agent_id=UUID.randomUUID().toString();
	   agent_name=name;
	   item_number=auction_number;
	   belongings=new Vector<Result>();
	}
	
	
	public double random_interval ()
	{
		double revenue=Math.random();
		return revenue;
	}
	
	
	public  double[] valuate()
	{
		
		valuation_list=new double[item_number];
		for (int k=0;k<item_number; k++)
		{
			valuation_list[k]=random_interval();
		}
		return valuation_list;
	}
	public double[] bid ()
	{
		bid_list=new double[item_number];
		for (int k=0;k<item_number; k++)
		{
			bid_list[k]=random_interval();
		}
		return bid_list;
		
	}
	
	
	public double getSingleBid(int index)
	{
		if(index< bid_list.length)
		return bid_list[index];
		else 
	    return 0;
	}
	
	public double getSingleValue(int index)
	{
		if(index< valuation_list.length)
		return valuation_list[index];
		else
		return 0;
	}
	public double[] getValuationList()
	{
		return valuation_list;
	}
	public double[] getBidList()
	{
		return bid_list;
	}
	public String getName()
	{
		return agent_name;
	}
	public void addBelongings(Result result)
	{
		belongings.add(result);
	}
	public Vector<Result> getBelongings()
	{
		return belongings;
	}
}
