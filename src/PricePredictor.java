import java.util.ArrayList;
import java.util.LinkedList;
import java.io.*;

public abstract class PricePredictor {
	ArrayList<Agent> agents;
	ArrayList<SBAuction> auctions;
	int no_auctions;		// the number of goods/auctions
	int no_per_iteration;	// no of samples to take per iteration
	int max_iterations;		// max no of iterations to make
	int avg_iterations;		// if max iterations is exceeded, then qw avg the last "avg_iterations" iterations
	double ks_threshold;	// convergence exists when ks_threshold is met for all items simultaneously
	double precision;		// precision for histograms/discrete distributions
	BufferedWriter bw;
	StringBuilder contents;
	
	PricePredictor(int no_auctions, int no_per_iteration, int max_iterations,
			int avg_iterations, double ks_threshold, double precision) {
		this.no_auctions = no_auctions;
		this.no_per_iteration = no_per_iteration;
		this.max_iterations = max_iterations;
		this.avg_iterations = avg_iterations;
		this.ks_threshold = ks_threshold;
		this.precision = precision;
		contents= new StringBuilder("");
	}
	
	// A sub-class implements this to create a fresh set of agents & auctions when
	// creating the initial auction (when we don't have a price prediction).
	// The function should also play the auction to completion.
	protected abstract void createAndPlayInitialAuction();
	
	// A sub-class implements this to create a fresh set of agents & auctions.
	// The current price prediction is given as parameter pp. The function
	// should also play the auction to completion.
	protected abstract void createAndPlayPPAuction(ArrayList<DiscreteDistribution> pp);
	
	// A sub-class implements this to convert a histogram to a discrete distribution of the
	// type desired by the sub-class (such as DiscreteDistributionWellman). f has the precision
	// specified by local variable "precision".
	protected abstract DiscreteDistribution createDiscreteDistribution(ArrayList<Double> f);

	// A user can call this function to predict prices.
	
	public void printFile()
	{
		try {
			bw=new BufferedWriter(new FileWriter(new File ("./src/1.txt")));
		////	bw.write("HIHI");
			bw.write(contents.toString());
			bw.close();
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
	}
	
	public String getPrintTable(ArrayList<DiscreteDistribution> pp_list)
	{
		StringBuilder table=new StringBuilder("");
		
		int max_size = 0;
		for (DiscreteDistribution p : pp_list)
			if (p.f.size() > max_size)
				max_size = p.f.size();
		table.append("\n");
		for (int j=0;j<pp_list.size();j++)
		{
		////table.append(","+j);
		}
		
		//for every row
		for(int i=0;i<max_size;i++)
		{
			//for every column
			table.append("\n"+i);
			for(int j=0;j<pp_list.size();j++)
			{
				DiscreteDistribution p=pp_list.get(j);
				if(p.f.size()>i)
				table.append(","+p.f.get(i));
				else
				table.append(","+"0.0");
			}
		}
		return table.toString();
	}
	
	
	public ArrayList<DiscreteDistribution> predict() {
		// Keep a history in case we fail to converge.
		LinkedList<ArrayList<DiscreteDistribution>> pp_history = new LinkedList<ArrayList<DiscreteDistribution>>();
		
		// Create the initial price prediction
	////	System.out.print("Initial: ");
	////	contents.append("Initial\n");
		ArrayList<DiscreteDistribution> pp_new = initial();
		System.out.println("");
		contents.append("");
		// output initial
		contents.append(getPrintTable(pp_new));
		
		// Iterate up to "max_iterations" times.
		for (int i = 0; i<max_iterations; i++) {
			// Add latest price prediction to the history buffer.
			pp_history.add(pp_new);
			
			// Trim the history by removing the oldest entry, if necessary
			if (pp_history.size() > avg_iterations)
				pp_history.removeFirst();
			
			// Obtain a new price prediction, which is based on the last prediction
			System.out.print("Iteration " + i + "/" + max_iterations + ": ");
			////contents.append("Iteration " + i + "/" + max_iterations + ": ");
			pp_new = singleIteration(pp_history.getLast());
			System.out.println("");
			contents.append("");
			contents.append(getPrintTable(pp_new));
			
			// Check for convergence
			if (converged(pp_history.getLast(), pp_new)) {
				contents.append("The pp_new is ");
				printFile();
				return pp_new;
			}
		}

		// Complete history by adding most recently generated price prediction.
		pp_history.add(pp_new);

		if (pp_history.size() > avg_iterations)
			pp_history.removeFirst();

		// We have failed to converge. Return the average of the last "avg_iterations"
		ArrayList<DiscreteDistribution> pp_avg = new ArrayList<DiscreteDistribution>();
		
		// We need to do an index swap on pp_history to provide DiscreteDistribution.mean()
		// with an ArrayList of distributions for the /same/ item.
		for (int i = 0; i<auctions.size(); i++) {
			ArrayList<DiscreteDistribution> pp_history_i = new ArrayList<DiscreteDistribution>();
			
			for (ArrayList<DiscreteDistribution> h : pp_history)
				pp_history_i.add(h.get(i));
			
			pp_avg.add(createDiscreteDistribution(DiscreteDistribution.computeMean(pp_history_i)));
		}
		
		////contents.append(getPrintTable(pp_avg));
		printFile();
		return pp_avg;
	}
	
	private ArrayList<DiscreteDistribution> initial() {
		ArrayList<Histogram> histogram_list = new ArrayList<Histogram>(no_auctions);
		
		for (int i = 0; i<no_auctions; i++)
			histogram_list.add(new Histogram(precision));
		
		for(int j = 0; j<no_per_iteration; j++) {
			System.out.print(".");
			
			// Ask our sub-class to create a brand new set of agents & auctions and play the simulation
			createAndPlayInitialAuction();
			
			// Every item adds a new payment record to its histogram
			for (int i = 0; i < auctions.size(); i++) 
				histogram_list.get(i).add(auctions.get(i).getWinnerPayment());
		}
		
		// Start to calculate the distribution
		ArrayList<DiscreteDistribution> distribution_list = new ArrayList<DiscreteDistribution>(auctions.size());

		for (int i = 0; i<auctions.size(); i++)
			distribution_list.add(createDiscreteDistribution(histogram_list.get(i).getDiscreteDistribution()));
		
		return distribution_list;		
	}
	
	private ArrayList<DiscreteDistribution> singleIteration(ArrayList<DiscreteDistribution> pp_old) {
		ArrayList<Histogram> histogram_list = new ArrayList<Histogram>(no_auctions);
		
		for (int i = 0; i<no_auctions; i++)
			histogram_list.add(new Histogram(precision));
		
		for(int j = 0; j<no_per_iteration; j++) {
			System.out.print(".");

			// Ask our sub-class to create a brand new set of agents & auctions and play the simulation
			createAndPlayPPAuction(pp_old);
			
			// Every item adds a new payment record to its histogram
			for (int i = 0; i < auctions.size(); i++) 
				histogram_list.get(i).add(auctions.get(i).getWinnerPayment());
		}
		
		// Start to calculate the distribution
		ArrayList<DiscreteDistribution> distribution_list = new ArrayList<DiscreteDistribution>(auctions.size());

		for (int i = 0; i<auctions.size(); i++)
			distribution_list.add(createDiscreteDistribution(histogram_list.get(i).getDiscreteDistribution()));
		
		return distribution_list;		
	}
	
	// Returns true if the two vectors of price distributions have converged to each other. We check the marginal
	// ks-statistic on each in a pair-wise fashion, and return true if and only if all are under our stated threshold.
	// Note that dd.size() must equal ee.size()
	private boolean converged(ArrayList<DiscreteDistribution> dd, ArrayList<DiscreteDistribution> ee) {
		boolean pass = true;
		
		System.out.print("\tKS: ");
		
		/////notice KS
		contents.append("\n");
		
		for (int i = 0; i<dd.size(); i++) {
			double ks = dd.get(i).getKSStatistic(ee.get(i));
			System.out.print(ks + ", ");
		////	contents.append(ks + ", ");

			if (ks > ks_threshold)
				pass = false;
		}

		System.out.println("\n");
		contents.append("\n");
		return pass;
	}
}
