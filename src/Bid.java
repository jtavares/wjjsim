
public class Bid {

	protected double bid_price;
	protected BiddingAgent bidding_agent;
	
	public Bid(BiddingAgent agent , double price)
	{
		bidding_agent=agent;
		bid_price=price;
	}
	public BiddingAgent getBiddingAgent()
	{
		return bidding_agent;
	}
	public double getBidPrice()
	{
		return bid_price;
	}
	public String information()
	{
		return "The bidding Agent "+bidding_agent.getName()+" bid for "+ bid_price +" dollars";
	}
	
}
