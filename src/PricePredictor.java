import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class PricePredictor {
	
	int agent_number;
	int auction_number;
	int no_per_iteration;
	int max_iteration;
	
	public PricePredictor (int agent_number, int auction_number,int no_per_iteration,int max_iteration)
	{
		this.agent_number=agent_number;
		this.auction_number=auction_number;
		this.no_per_iteration=no_per_iteration;
		this.max_iteration=max_iteration;
	}
	

		
		
	public PricePredictor (PricePredictor pp, Agent strategy_agent)
	{
		//this.agent=strategy_agent;
	}

	public void initial ()
	{
		//Historgram his= new Histogram();
	}
}
