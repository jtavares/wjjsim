
import java.util.*;

public class Auction {
 
	double reserve_price;
	BiddingAgent winner;
	LinkedList<Bid> rankList;
	
	public Auction(double price)
	{
		reserve_price=price;
	}
	public double get_reserve()
	{
		return reserve_price;
	}
	public LinkedList<Bid> getRankList()
	{
		return rankList;
	}
	public void setRankList(LinkedList<Bid> rank)
	{
		rankList=rank;
	}
	public BiddingAgent getWinner()
	{
		Bid bid_most=rankList.getFirst();
		return bid_most.getBiddingAgent();
	}
	public double getWinnerPayment(int strategy)
	{
		double payment;
		if(rankList.size()<strategy)
		{
		return reserve_price;
		}
		else 
		{
		 payment = rankList.get(strategy-1).getBidPrice();
		}
		return payment;
	}
}
