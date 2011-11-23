

import java.util.ArrayList;
import java.util.List;


//Work needs to be finished
//1. check the precision 
//2 . contact James and J about how to write the communication with the agents about the new distribution
//3. How to deal with the auctions each with different iteration numbers. For example Auction 0 has F0, F1 and end , 
//But auctionS 1 has F0,F1,F3,F4,F5,F6,F7,F8,F9


public class PricePredictor {
	
	private List<Agent> agents;
	private List<SBAuction> auctions;
	int no_per_iteration;
	int max_iteration;
	double ks_threshold;
	
	public PricePredictor (List<Agent> agents, List<SBAuction> auctions,int no_per_iteration,int max_iteration,double ks_threshold)
	{
		this.agents=agents;
		this.auctions=auctions;
		this.no_per_iteration=no_per_iteration;
		this.max_iteration=max_iteration;
		this.ks_threshold=ks_threshold;
	}
	
	
	
	
	public void process()
	{
		/*
		  1. Initial Distribution
		  2. singleIteration and send the distribution to the agents ?
		  3. start another Iteration and compare the distribution to previous ones 
		*/
		
		ArrayList<DiscreteDistributionWellman> distribution_list= initial();
		
		
		/*****************************************************************/
		/**James and J , you could be free to edit the following codes . Here I want to inform the agents about the distribution**/
	  //@@@	agents.setDistribution(distribution_list);
		
		/*****************************************************************/
	
		//The first real distribution from the agents
		distribution_list=singleIteration();
		
		
		int iteration_number=0;
		ArrayList<DiscreteDistributionWellman> old_list;
	
		do
		{
		old_list=distribution_list;
		/*****************************************************************/
		/**James and J , you could be free to edit the following codes . Here I want to inform the agents about the distribution**/
	  //@@@	agents.setDistribution(distribution_list);
		
		/*****************************************************************/
		distribution_list=singleIteration();
		iteration_number++;
		}
		while((iteration_number< max_iteration)&& !(compareDistribution(old_list,distribution_list)));
			
		
	}
	
	public ArrayList<DiscreteDistributionWellman> initial ()
	{
		
		ArrayList<DiscreteDistributionWellman> distribution_list=new ArrayList<DiscreteDistributionWellman>(auctions.size());
		for (int i=0;i<auctions.size();i++)
		{
			//Convert from histogram to distribution
			Histogram his= new Histogram(1);
			for (int j=0;j<200;j++)
				{
				his.add(Math.floor((Math.random() * 50)) + 1);
				}
			
			ArrayList<Double> F=his.getDiscreteDistribution();
			DiscreteDistributionWellman dd= new DiscreteDistributionWellman(F,1.00);
			distribution_list.add(dd);
		}
		
		
		return distribution_list;
		
	}
	
	public ArrayList<DiscreteDistributionWellman> singleIteration()
	{
		ArrayList<Histogram> histogram_list=new ArrayList<Histogram>(auctions.size());
		
		for(int j=0;j<no_per_iteration;j++)
		{
			//each time when simultaneous ascending simulation start
			SimAscSimulationTest s = new SimAscSimulationTest(agents, auctions);
			s.play();
			
			for (int i =0;i<auctions.size();i++)
			{
				//record the final payment value to each item
				double payment=s.getWinnerPayment(i);
				//Every item adds a new payment record to its histogram
				histogram_list.get(i).add(payment);
			}
		
		}
		
		//Start to calculate the distribution
		ArrayList<DiscreteDistributionWellman> distribution_list=new ArrayList<DiscreteDistributionWellman>(auctions.size());
		for (int i=0;i<auctions.size();i++)
		{
			//Convert from histogram to distribution
			ArrayList<Double> F=histogram_list.get(i).getDiscreteDistribution();
			DiscreteDistributionWellman dd= new DiscreteDistributionWellman(F,1.00);
			distribution_list.add(dd);
		}
		
		return distribution_list;
		
	}
	
	///not fnished yet ....../
	public boolean compareDistribution(ArrayList<DiscreteDistributionWellman> dd, ArrayList<DiscreteDistributionWellman> ee)
	{
		return false;
	}
	
	// 
	public boolean compareIteration(DiscreteDistribution dd, DiscreteDistribution ee)
	{
		
		if (dd.getKSStatistic(ee)<ks_threshold)
		return true;
		else
			return false;
	
	}
}
