
public class Result {

	Auction auction;
	BiddingAgent agent;
	double actual_payment;
	int index;
	public Result(int i , Auction a, double price)
	{
		auction=a;
		index=i;
		actual_payment=price;
		agent=a.getWinner();
	}
	public int getAuctionIndex()
	{
		return index;
	}
	public double getCost()
	{
		return actual_payment;
	}
	public double getRevenue()
	{
		double[] valuation_list=agent.getValuationList();
		if(index<valuation_list.length)
		{
		return valuation_list[index];
		}
		else 
			return 0;
	}
	public double getProfit()
	{
		return getCost()-getProfit();
	}
}
