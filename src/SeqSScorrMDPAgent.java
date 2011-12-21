import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// An Agent that adjusts price distributions according to the previous auction outcomes, and bid accordingly.  


public class SeqSScorrMDPAgent extends SeqSSMDPAgent {

	// Declarations
	ArrayList<DiscreteDistribution> pd;
	ArrayList<ArrayList<ArrayList<Double>>> beta;
//	ArrayList<ArrayList<Double>> beta;
	ArrayList<Double> mean;
	ArrayList<DiscreteDistribution> pd_adjusted;

	public SeqSScorrMDPAgent(int agent_idx, Valuation valuation, ArrayList<DiscreteDistribution> pd) {
			super(agent_idx, valuation, pd);
			this.pd = pd;
			// get distribution means and regression/correlation coefficients
			this.mean = getMean(pd);
			this.beta = getBeta();
//			this.beta = getBeta2();

//			System.out.println("Agent "+agent_idx+": Betas assigned");
/*
			// Print out input price prediction
			if (valuation.getValue(0) > 0) {
				for (int i = 0; i < pd.size(); i++) {
					System.out.println("Slot " + i + ":" );
					pd.get(i).print(0.0);
				}

				System.out.println("\nAgent " + agent_idx + " Valuation:");
				valuation.print();

				// Print out the mapping \pi: state --> optimal bid
				System.out.println("\nAgent " + agent_idx + ": I have done my MDP computation and here is my /pi mapping: ");
				for (X_t key : pi.keySet()) {
					System.out.println("pi(" + key.toString() + ") --> " + pi.get(key));
				}

				for (X_t key : V.keySet()) {
					System.out.println("V(" + key.toString() + ") --> " + V.get(key));
				}
			}
*/			

	}
	
	// Return means of original price prediction distributions
	public ArrayList<Double> getMean(ArrayList<DiscreteDistribution> pd) {
			
			ArrayList<Double> mean = new ArrayList<Double>();
			for (int i = 0; i< valuation.getNoValuations(); i++){
				mean.add(pd.get(i).getExpectedFinalPrice((double) 0));
			}
			return mean;
	}
	
	// Return hardcoded beta coefficients. 
	// Structure: beta.get(current_auction).get(i).get(j) is price i's regression coefficient on price j when at the "current auction"
	public ArrayList<ArrayList<ArrayList<Double>>> getBeta() { 

		// Regression coefficients supplied by MATLAB. We basically hard code them into "beta". 

							//	round 1
					//	    0.4489
					//	    0.2431
					//	    0.1400
					//	    0.0253		
							//	round 2
					//	    0.0453    0.4406
					//	    0.0544    0.1908
					//	    0.0145    0.0240
							//	round 3
					//	    0.0422    0.0727    0.2680
					//	    0.0132    0.0112    0.0292
							//	round 4
					//	    0.0100    0.0058    0.0093    0.0740

		ArrayList<ArrayList<ArrayList<Double>>>beta = new ArrayList<ArrayList<ArrayList<Double>>>();

		// for each round (= current_auction)
		ArrayList<ArrayList<Double>> beta_round_0 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> beta_round_1 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> beta_round_2 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> beta_round_3 = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> beta_round_4 = new ArrayList<ArrayList<Double>>();
		
		ArrayList<Double> beta_1_0 = new ArrayList<Double>();
		ArrayList<Double> beta_1_1 = new ArrayList<Double>();
		ArrayList<Double> beta_1_2 = new ArrayList<Double>();
		ArrayList<Double> beta_1_3 = new ArrayList<Double>();
		ArrayList<Double> beta_1_4 = new ArrayList<Double>();		
		
		beta_1_1.add(0.45);
		beta_1_2.add(0.24);
		beta_1_3.add(0.14);
		beta_1_4.add(0.03);
		
		beta_round_1.add(beta_1_0);
		beta_round_1.add(beta_1_1);
		beta_round_1.add(beta_1_2);
		beta_round_1.add(beta_1_3);
		beta_round_1.add(beta_1_4);
		
		ArrayList<Double> beta_2_0 = new ArrayList<Double>();
		ArrayList<Double> beta_2_1 = new ArrayList<Double>();
		ArrayList<Double> beta_2_2 = new ArrayList<Double>();
		ArrayList<Double> beta_2_3 = new ArrayList<Double>();
		ArrayList<Double> beta_2_4 = new ArrayList<Double>();		
		
		beta_2_2.add(0.05);
		beta_2_2.add(0.44);
		beta_2_3.add(0.05);
		beta_2_3.add(0.19);
		beta_2_4.add(0.01);
		beta_2_4.add(0.02);

		beta_round_2.add(beta_2_0);
		beta_round_2.add(beta_2_1);
		beta_round_2.add(beta_2_2);
		beta_round_2.add(beta_2_3);
		beta_round_2.add(beta_2_4);
		
		ArrayList<Double> beta_3_0 = new ArrayList<Double>();
		ArrayList<Double> beta_3_1 = new ArrayList<Double>();
		ArrayList<Double> beta_3_2 = new ArrayList<Double>();
		ArrayList<Double> beta_3_3 = new ArrayList<Double>();
		ArrayList<Double> beta_3_4 = new ArrayList<Double>();		
				
		beta_3_3.add(0.04);
		beta_3_3.add(0.07);
		beta_3_3.add(0.27);
		beta_3_4.add(0.01);
		beta_3_4.add(0.01);
		beta_3_4.add(0.03);
		
		beta_round_3.add(beta_3_0);
		beta_round_3.add(beta_3_1);
		beta_round_3.add(beta_3_2);
		beta_round_3.add(beta_3_3);
		beta_round_3.add(beta_3_4);
		
		ArrayList<Double> beta_4_0 = new ArrayList<Double>();
		ArrayList<Double> beta_4_1 = new ArrayList<Double>();
		ArrayList<Double> beta_4_2 = new ArrayList<Double>();
		ArrayList<Double> beta_4_3 = new ArrayList<Double>();
		ArrayList<Double> beta_4_4 = new ArrayList<Double>();

		beta_4_4.add(0.01);
		beta_4_4.add(0.01);
		beta_4_4.add(0.00);
		beta_4_4.add(0.07);

		beta_round_4.add(beta_4_0);
		beta_round_4.add(beta_4_1);
		beta_round_4.add(beta_4_2);
		beta_round_4.add(beta_4_3);
		beta_round_4.add(beta_4_4);
		
		// Lastly, add everything to beta		
		beta.add(beta_round_0);
		beta.add(beta_round_1);
		beta.add(beta_round_2);
		beta.add(beta_round_3);
		beta.add(beta_round_4);
	
		return beta;
	}
	
	// An alternative way to hard code beta : just plug in correlation coefficients. 
	public ArrayList<ArrayList<Double>> getBeta2(){
	
		ArrayList<ArrayList<Double>> beta = new ArrayList<ArrayList<Double>>();

		// Correlation coefficients of slots 1 ~ 5
		ArrayList<Double> beta_0 = new ArrayList<Double>();
		ArrayList<Double> beta_1 = new ArrayList<Double>();
		ArrayList<Double> beta_2 = new ArrayList<Double>();
		ArrayList<Double> beta_3 = new ArrayList<Double>();
		ArrayList<Double> beta_4 = new ArrayList<Double>();
		
		beta_1.add(0.45);	// 1's correlation with 0
		beta_2.add(0.05);	// 2's correlation with 0
		beta_2.add(0.44);	// 2's correlation with 1
		beta_3.add(0.04);	// etc... 
		beta_3.add(0.07);
		beta_3.add(0.27);
		beta_4.add(0.01);
		beta_4.add(0.01);
		beta_4.add(0.01);
		beta_4.add(0.07);
		
		beta.add(beta_0);
		beta.add(beta_1);
		beta.add(beta_2);
		beta.add(beta_3);
		beta.add(beta_4);

		return beta;		
	}

	// Generate a new price prediction conditional on prices already realized
	public ArrayList<DiscreteDistribution> UpdateDistribution(ArrayList<DiscreteDistribution> pd, ArrayList<Double> prices) {
		
		// figure out which auction we are in
		Iterator<Integer> iterator3 = openAuctions.iterator();
		int current_auction = iterator3.next();

		// Update prices given past information
		ArrayList<DiscreteDistribution> pd_adjusted = new ArrayList<DiscreteDistribution>();
		
		// No need to change price prediction for past auctions; just copy-paste
		for (int i = 0; i < current_auction + 1; i++){
			pd_adjusted.add(pd.get(i));
		}
		
		// shift price distributions for future auctions
		double shift;
		for (int i = current_auction; i < valuation.getNoValuations(); i++) {
			shift = 0.0;
			for (int j = 0; j < current_auction; j++) {
				// System.out.println("current auction " + current_auction + ", how "+i+" depends on "+j+": beta = "+beta.get(current_auction).get(i).get(j));
				shift += beta.get(current_auction).get(i).get(j)*(prices.get(j)-mean.get(j));				
//				shift += beta.get(i).get(j)*(prices.get(j)-mean.get(j));	// Calculate the amount of adjustment necessary
			}

			// shift prices if the shift is larger than 1, but out of the range... otherwise don't adjust prices
			int int_shift = (int)Math.floor(shift+0.5f);
				//		System.out.println("Current auction "+current_auction+", shift = "+shift+" and int_shift = "+int_shift);
			
			if (int_shift != 0 && int_shift < 50 && int_shift > -50){
				pd_adjusted.add(DiscreteDistribution.shiftDistribution(pd.get(i),int_shift));	
			}
			else {
				pd_adjusted.add(pd.get(i));
			}
		}
		
		return pd_adjusted;
	}

	// This getBids need to be called once in each SPSB auction
	@Override
	public HashMap<Integer, Double> getBids() {

		// Figure out which auction is currently open? (There can be only one open auction at a time)
		Iterator<Integer> iterator3 = openAuctions.iterator();
		int current_auction = iterator3.next();

		ArrayList<Double> prices = new ArrayList<Double>();
		
		// Figure out what we have won in the past and the winner prices
		Set<Integer> goods_won = new HashSet<Integer>();
		for (int i = 0; i < current_auction; i++){
			prices.add(results.get(i).getPayment());
			if (results.get(i).getIsWinner() == true){
				goods_won.add(i);
			}
		}			
				
		if (current_auction == 0) {
			computeMDP(pd);	
		}
		
		if (current_auction > 0 && current_auction < 5){
			// Update price distribution
			pd_adjusted = UpdateDistribution(pd,prices);
			computeMDP(pd_adjusted);
		}
				
		X_t state = new X_t(goods_won,current_auction);			// Current state (X,t)		
		HashMap<Integer, Double> bids = new HashMap<Integer, Double>();
		bids.put(current_auction, pi.get(state));
		return bids;
	}
	

}
