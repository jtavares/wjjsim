import java.util.*;

public class Simulator {

    static ArrayList<BiddingAgent> agents;
	static ArrayList<Auction> items;
	static int item_number;
	static int agent_number;
	
	static int price_strategy;
	public Simulator (int n)
	{
		price_strategy=n;
	}
	public static LinkedList<Bid> insert(LinkedList<Bid> agent_rank, Bid bid)
	{
		boolean allocated=false;
		if(agent_rank.isEmpty())
		{
			agent_rank.add(bid);
			allocated=true;
		}
		else 
		{
			for(int k=0;k<agent_rank.size();k++)
			{
				if(bid.getBidPrice()>agent_rank.get(k).getBidPrice())
				{
					agent_rank.add(k,bid);
					allocated=true;
					break;
				}
			}
			if(!allocated)
			{
				agent_rank.add(bid);
			}
		}
		return agent_rank;
	}
	
    public static void main (String args[])
    {
    	int n = 10;
    	price_strategy=n;
    	double[] auction_prices={0.50,0.1,0.23,0.8,0.05};
    	agent_number=4;
    	items= new ArrayList<Auction> ();
		for(int k=0;k<auction_prices.length;k++)
		{
			items.add(new Auction(auction_prices[k]));
			item_number++;
		}
		
	    agents= new ArrayList<BiddingAgent> ();
	    agents.add(new BiddingAgent("Alice",item_number));
		agents.add(new BiddingAgent("Bill",item_number));
		agents.add(new BiddingAgent("Collin",item_number));
		agents.add(new BiddingAgent("David",item_number));
		
    	double[] reserve_prices=auction_prices;
    	
		//each agent will create a valuation vector for all the items
		for (int k=0;k<agents.size();k++)
    	{
			BiddingAgent agent=agents.get(k);
    		agent.valuate();
    		System.out.println("\n"+agent.getName()+":");
    		agent.bid();

    		
    		for(int i=0; i< item_number; i++)
    		{
    			System.out.print("Value  "+agent.getSingleValue(i)+". Bid  "+agent.getSingleBid(i)+"|| reserve_price");
    			System.out.println(items.get(i).get_reserve());
    		}
    		
    	}
    	
    	for (int i=0;i<item_number; i++)
    	{
    		
    		LinkedList<Bid> agent_rank=new LinkedList<Bid>();
    		for (int j=0;j<agent_number;j++)
    		{
    			Bid bid=new Bid(agents.get(j),agents.get(j).getSingleBid(i));
    			agent_rank=insert(agent_rank,bid);
    		}
    		items.get(i).setRankList(agent_rank);
    	}
    
    	//determine who is the winner of each auction
    	for (int i=0 ; i <item_number; i++)
    	{
    		BiddingAgent winner=items.get(i).getWinner();
    		double actual_price=items.get(i).getWinnerPayment(price_strategy);
    		Result result= new Result(i,items.get(i),actual_price);
    		winner.addBelongings(result);
    		System.out.println("\nThe winner of auction "+i+" is "+winner.getName());
    		System.out.println("The actural price winner pays is "+actual_price);
    	}
    	
    	//determin items won by each bidder
    	for (int i =0; i<agent_number; i++)
    	{
    		Vector<Result> v=agents.get(i).getBelongings();
    		String item_descrip="";
    		double total_cost=0;
    		double total_revenue=0;
    		double total_profit=0;
    		for(int j=0;j<v.size();j++)
    		{
    			item_descrip+=v.elementAt(j).getAuctionIndex()+", ";
    			total_revenue+=v.elementAt(j).getRevenue();
    			total_cost+=v.elementAt(j).getCost();
    			
    		}
    		if(v.size()==0)item_descrip="None";
    		total_profit=total_revenue-total_cost;
    		System.out.println("\nThe agent "+ agents.get(i).getName()+" has items :"+item_descrip+" ");
    		System.out.println("Its total cost is "+total_cost);
    		System.out.println("Its total revenue is " +total_revenue);
    		System.out.println("Its total profit is " +total_profit);
    	}
    		
    		
	}
}