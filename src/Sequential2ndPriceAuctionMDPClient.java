import java.io.*;
import java.net.*;
import java.util.*;

public class Sequential2ndPriceAuctionMDPClient {

	public static void main(String[] args) throws IOException {

		System.out.println("AuctionClient starting");
		Socket auctSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		String hostName = "192.168.40.1";// IP of host (may be local IP)
		int socketNum = 7; // the luckiest socket

		SeqSSMDPAgent agent = null;
		Valuation valuationMethod = null;
		String agent_name = "Anonymous";
		List<SBAuction> auctions = null;

		List<Agent> agents = null;
		int agent_id = 0;
		int auction_index_number = 0;

		// Try reading in the IP and socket number from the text file...
		try {
			in = new BufferedReader(new FileReader("./src/IP_and_Port.txt"));
			// two lines in this file. First is hostName/IP address, and second
			// is socket number of host
			hostName = in.readLine();
			socketNum = Integer.valueOf(in.readLine());
			in.close();
		} catch (IOException e) {
		}

		// These are values the agent should remember and use
		// Values are initialized when Server sends parameters
		int numSlotsNeeded = -1;
		int deadline = -1;
		double[] valuations = null;

		try {
			auctSocket = new Socket(hostName, socketNum);
			out = new PrintWriter(auctSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					auctSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + hostName + ".");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to: host.");
			System.exit(1);
		}
		System.out.println("Connection to host established");

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		String fromServer;

		// continue to prompt commands from host and respond
		while ((fromServer = in.readLine()) != null) {
			System.out.println("\n\nServer: " + fromServer);

			// Send host this client's unique ID
			if (fromServer.equalsIgnoreCase("Send client name")) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				System.out.println("Enter a unique ID string...");
				agent_name = "WJJSIM" + (int) (Math.random() * 100); // br.readLine();
				out.println(agent_name);// prompt user for an ID string
				System.out.println(agent_name);
				// OPTIONAL CHANGE:
				// YOU MAY CHOOSE TO REPLACE THE LINE ABOVE WITH THE LINE BELOW
				// out.println("My_Hardcoded_Name");

				System.out
						.println("User ID sent.  If prompted again, choose another ID/Name string.");
				System.out.println("Waiting for server to start auction...");
			}

			// ***********************************************************************

			else if (fromServer.startsWith("agent-parameters:")) {
				String[] params = fromServer.split("[ ]");// tokens delimited
															// with spaces
				numSlotsNeeded = Integer.valueOf(params[1]);// 1st param is
															// number of slots
															// needed

				deadline = Integer.valueOf(params[2]);// 2nd param is deadline
				// DEADLINE IS INDEX OF LAST AUCTION OF VALUE (0-indexed).
				// example: deadline = 1 --> first two time-slots can be used

				valuations = new double[params.length - 3];// first 3 stings
															// aren't valuations
				for (int i = 3; i < params.length; i++)
					valuations[i - 3] = Double.valueOf(params[i]);

				// /////////////////////////////////////////////
				// YOUR CODE HERE
				// You probably want to store the parameters sent from host.
				// For example, you could store them in global variables,
				// or use them to initialize an agent class you wrote.
				// /////////////////////////////////////////////
				valuationMethod = new SchedulingValuation(numSlotsNeeded,
						valuations);
				agents = new ArrayList<Agent>();
				auctions = new ArrayList<SBAuction>(valuations.length);

				// Let's feed everyone the same input price prediction
				ArrayList<Double> weight = new ArrayList<Double>(); // dummy
																	// weights
				ArrayList<DiscreteDistribution> pp = genPrediction(8, 5, false,
						weight);

				agent = new SeqSSMDPAgent(agent_id, valuationMethod, pp);
				agents.add(agent);

				// int ask_price=0;
				// int ask_epsilon=1;
				int nth_price = 2;
				double reserve_price = 0;
				// int auction_id=0;

				for (int auction_id = 0; auction_id < valuations.length; auction_id++) {
					SBAuction auction = new SBNPAuction(auction_id,
							reserve_price, 0, 0, agents, nth_price);
					auctions.add(auction);
					// agent.postResult(new Result(auctions.get(auction_id),
					// false, 0, ask_price, 0, ask_epsilon));
				}

				// /////////////////////////////////////////////
				out.println("Parameters accepted");
			}

			// ***********************************************************************

			else if (fromServer.equalsIgnoreCase("submit-bid")) {
				// Here are values you should use... be more clever than random!
				// numSlotsNeeded (int)
				// deadline (int) index of last timeSlot of value (starts at 0)
				// valuations (double[numSlots])

				// /////////////////////////////////////////////
				// YOUR CODE HERE
				// Create a string, like myBids, with your bid(s).
				// If placing multiple bids, separate bids with spaces spaces.
				// If multiple bids, order bids as follows:
				// myBids = "timeSlot1Bid  timeSlot2Bid ... timeslot5Bid";
				//
				// Note: bids get rounded to 2 decimal places by host. 5.031 ->
				// 5.03
				// /////////////////////////////////////////////

				// EDIT HERE!

				/*
				 * Random r = new Random();//make random bids...String myBid =
				 * ""+ (r.nextDouble()*10);
				 */
				HashMap<Integer, Double> a_bids = agent.getBids();
				String myBids = "";

				if (a_bids.size() == 1) {
					// this is one item, as expected
					for (int r : a_bids.keySet())
						myBids = "" + a_bids.get(r);

				} else if (a_bids.size() == 0) {
					// there are no items, bid 0.
					myBids = "0.0";
				} else {
					System.out
							.println("ERROR: agent submitted wrong number of bids: "
									+ a_bids.size());
					System.exit(-1);
				}

				// /////////////////////////////////////////////
				out.println("" + myBids); // Send agent's bids to server (as a
											// string)
				System.out.println("My bid: " + myBids);
			}

			// ***********************************************************************

			// Observe the state of auction variables. Store information locally
			else if (fromServer.startsWith("observe-auction-state:")) {
				auction_index_number++;
				System.out.println("The auction command has come "
						+ auction_index_number);

				String[] stateVars = fromServer.split("[ ]");// tokens delimited
																// with spaces

				int numAgents = Integer.valueOf(stateVars[1]);
				int numTimeSlots = Integer.valueOf(stateVars[2]);
				int currentRound = Integer.valueOf(stateVars[3]);// 1st round ->
																	// 0
				// currentRound is 0-indexed, so currentRound = 0 for first
				// round

				String[] winnerIDs = new String[currentRound];//
				double[] prices = new double[currentRound];
				double[] winBids = new double[currentRound];

				for (int i = 0; i < (currentRound * 3); i += 3) { // 3 records
																	// per
																	// round:
																	// winnerID,
																	// pricePaid,
																	// bid
					winnerIDs[i / 3] = stateVars[4 + i];
					prices[i / 3] = Double.valueOf(stateVars[5 + i]);
					winBids[i / 3] = Double.valueOf(stateVars[6 + i]);
				}

				agent.closeAllOpenAuctions();
				agent.openAuction(currentRound);

				System.out.println("+++++++++++Opening current round: "
						+ currentRound + "++++++++++++++++++++\n");

				System.out.println("Observing state:\nCurrent round = "
						+ currentRound + "\nNumber of agents = " + numAgents
						+ "\nNumber of time slots = " + numTimeSlots);
				System.out.println("Previous round results:");
				for (int i = 0; i < winnerIDs.length; i++)
					System.out.println("Round " + i + ": winner: "
							+ winnerIDs[i] + ", price paid = " + prices[i]
							+ ", with bid = " + winBids[i]);

				// /////////////////////////////////////////////
				// YOUR CODE HERE
				// You may want to record some of the state
				// information here, especially the results
				// from previous auction rounds in winnerIDs
				// and prices. The for round i (0-indexed),
				// winnerIDs[i] is a unique string ID for the
				// agent who won the time-slot and paid prices[i].
				// /////////////////////////////////////////////

				// EDIT HERE
				if (currentRound > 0) {

					boolean is_winner = false;
					if (winnerIDs[currentRound - 1].equals(agent_name)) {
						is_winner = true;
					}

					int ask_epsilon = 1;
					double cur_price = prices[currentRound - 1];
					double ask_price = cur_price + ask_epsilon;
					double payment = 0;

					if (is_winner)
						payment = cur_price; // payment =
												// prices[currentRound-1];
					else
						payment = 0;

					agent.postResult(new Result(auctions.get(currentRound - 1),
							is_winner, payment, ask_price, cur_price,
							ask_epsilon));
					System.out.println(agent.information());
				}

				// /////////////////////////////////////////////
				out.println("State Observed");// let the server know client
												// received state info
			}

			// ***********************************************************************

			else if (fromServer.startsWith("observe-final-outcome:")) {
				String[] outcomeVars = fromServer.split("[ ]");// tokens
																// delimited
																// with spaces
				// for each slot, announce winner's ID, price paid, and their
				// bid
				if (outcomeVars.length - 1 < valuations.length * 3) {
					out.println("incomplete state");// let server know state was
													// incomplete
				} else {
					for (int i = 1; i < outcomeVars.length; i += 3) {
						// for ith time-slot, winner and price
						String winnerID = outcomeVars[i];
						double winPrice = Double.valueOf(outcomeVars[i + 1]);
						double winBid = Double.valueOf(outcomeVars[i + 2]);
						System.out.println("Time Slot " + (1 + (i / 3))
								+ " awarded to [" + winnerID + "] for price = "
								+ winPrice + "  with bid = " + winBid);
					}
					out.println("Final Outcome Observed");// let the server know
															// client received
															// state info
				}
			}

			// ***********************************************************************

			// The server says to end the connection
			else if (fromServer.equals("END")) {
				System.out.println("END called.  closing");
				break;
			}

			// ***********************************************************************

			else
				System.out.println("Unexpected input: " + fromServer);
		}

		out.close();
		in.close();
		stdIn.close();
		auctSocket.close();
	}

	private static ArrayList<DiscreteDistribution> genPrediction(int no_agents,
			int max_iterations, boolean smoothed, ArrayList<Double> weight) {
		int no_auctions = 5;
		int nth_price = 2;
		double reserve_price = 0;
		int no_per_iteration = 500;
		int avg_iterations = 1; // number of PPs to average over
		double ks_threshold = 0.01;
		double precision = 1.0;

		// Setup price predictor options
		PricePredictorSeqSSMDP pp = new PricePredictorSeqSSMDP(no_agents,
				no_auctions, nth_price, reserve_price, no_per_iteration,
				max_iterations, avg_iterations, ks_threshold, precision);

		// Whether to employ smoothed price updating
		if (smoothed) {
			return pp.predict_smoothed(weight);
		} else {
			return pp.predict();
		}
	}

}