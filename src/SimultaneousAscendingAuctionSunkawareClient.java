
import java.io.*;
import java.net.*;
import java.util.*;

public class SimultaneousAscendingAuctionSunkawareClient {
	// this PrOfBids is from assuming all agents are probability aware. (this is the most conservative we could be)
	static double PrOfBids[] = {
			0.64578125,
			0.63238125,
			0.606505,
			0.60104875,
			0.58828375,
			0.57891125,
			0.5691625,
			0.552105,
			0.538845,
			0.52385,
			0.50847,
			0.48816125,
			0.45849125,
			0.4290275,
			0.40191375,
			0.37531,
			0.34965,
			0.32523125,
			0.30174375,
			0.2794875,
			0.25861125,
			0.23854375,
			0.220215,
			0.20288,
			0.186395,
			0.17143,
			0.158245,
			0.14570125,
			0.13434875,
			0.12350875,
			0.11401375,
			0.1053,
			0.09730625,
			0.0897925,
			0.08323875,
			0.07696625,
			0.0713275,
			0.06629125,
			0.061695,
			0.05745875,
			0.05344625,
			0.04989125,
			0.04656875,
			0.04368625,
			0.04084125,
			0.03826375,
			0.03588625,
			0.03369125,
			0.0316525,
			0.02974375,
			0.027905,
			0.02623125,
			0.02466125,
			0.02314125,
			0.02181375,
			0.02049125,
			0.01922125,
			0.01815,
			0.0170225,
			0.01600875,
			0.0151775,
			0.01429875,
			0.01346875,
			0.0126275,
			0.01191125,
			0.01118625,
			0.0105525,
			0.009995,
			0.00939875,
			0.00885,
			0.00833625,
			0.0077925,
			0.00732375,
			0.00686625,
			0.006445,
			0.0060375,
			0.00568,
			0.00528125,
			0.0049575,
			0.00463375,
			0.00434375,
			0.0040875,
			0.00380125,
			0.0035425,
			0.00332125,
			0.00312375,
			0.002885,
			0.00266875,
			0.0024525,
			0.00227375,
			0.00211125,
			0.00195625,
			0.00178875,
			0.0016475,
			0.00154125,
			0.00142875,
			0.00132125,
			0.00121375,
			0.0011125,
			0.00102125,
			9.46E-04,
			8.78E-04,
			7.86E-04,
			7.28E-04,
			6.73E-04,
			6.18E-04,
			5.70E-04,
			5.11E-04,
			4.75E-04,
			4.26E-04,
			3.90E-04,
			3.56E-04,
			3.26E-04,
			3.00E-04,
			2.75E-04,
			2.50E-04,
			2.25E-04,
			2.00E-04,
			1.78E-04,
			1.53E-04,
			1.36E-04,
			1.21E-04,
			1.10E-04,
			1.01E-04,
			8.63E-05,
			7.63E-05,
			6.63E-05,
			6.00E-05,
			5.63E-05,
			5.25E-05,
			5.00E-05,
			4.63E-05,
			4.63E-05,
			4.25E-05,
			3.25E-05,
			2.63E-05,
			2.25E-05,
			1.75E-05,
			1.63E-05,
			1.50E-05,
			1.38E-05,
			8.75E-06,
			8.75E-06,
			8.75E-06,
			8.75E-06,
			5.00E-06,
			5.00E-06,
			3.75E-06,
			3.75E-06,
			3.75E-06,
			3.75E-06,
			3.75E-06,
			3.75E-06,
			3.75E-06,
			3.75E-06,
			2.50E-06,
			2.50E-06,
			2.50E-06,
			2.50E-06,
			2.50E-06,
			2.50E-06,
			1.25E-06,
			1.25E-06,
			1.25E-06,
			1.25E-06
	};
	
	public static void main(String[] args) throws IOException {
		System.out
				.println("SimultaneousAscendingAuctionSunkawareClient starting");

		Socket auctSocket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		String hostName = "127.0.0.1";// IP of host (may be local IP)
		int socketNum = 1305; // the luckiest socket

		Agent agent = null;
		Valuation valuationMethod = null;
		String agent_name = "Anonymous";
		List<SBAuction> auctions = null;
		List<Agent> agents = null;

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
			System.err.println("Couldn't get I/O for the connection to: " + hostName + ":" + socketNum);
			System.exit(1);
		}
		System.out.println("Connection to host established");

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(
				System.in));
		String fromServer;

		// continue to prompt commands from host and respond
		while ((fromServer = in.readLine()) != null) {
			System.out.println("Server: " + fromServer);

			// Send host this client's unique ID
			if (fromServer.equalsIgnoreCase("Send client name")) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				System.out.println("Enter a unique ID string...");

				agent_name = "WJJSIM" + (int)(Math.random() * 100); //br.readLine();
				out.println(agent_name);
				System.out.println("NAME: " + agent_name);
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

				// EDIT HERE
				// ?? how to define the number for each agent ?
				// ?? whether to change to another kind of agent

				valuationMethod = new SchedulingValuation(numSlotsNeeded,
						valuations);
				
				//valuationMethod.print();
				
				double sunk_awareness = 1;
				agent = new SunkawarePPStarAgent(0, valuationMethod, sunk_awareness);

				// Send starting information= to agents

				int ask_price = 1;
				int ask_epsilon = 1;
				agents = new ArrayList<Agent>();
				auctions = new ArrayList<SBAuction>(valuations.length);
				agents.add(agent);

				for (int i = 0; i < valuations.length; i++) {
					SBAuction auction = new SBNPAuction(i, 0, ask_price, ask_epsilon, agents, 1);
					auctions.add(auction);
					// the informatio below is sent automatically by SBAuction class.
					//agent.postResult(new Result(auctions.get(i), false, 0,
					//		ask_price, 0, ask_epsilon));
				}

				System.out.println("Auctionsize : " + auctions.size());
				agent.openAllAuctions();

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

				/*
				 * String myBids = ""; Random r = new Random();//make random
				 * bids... 1 for each time slot for(int i=0; i <
				 * valuations.length; i++){ myBids = myBids +
				 * (r.nextDouble()*10);//random bid 0 to 10 if(i <
				 * valuations.length-1) myBids = myBids + " ";//separate bids
				 * with a space! IMPORTANT! }
				 */

				// EDIT HERE!

				HashMap<Integer, Double> a_bids = agent.getBids();
				String myBids = "";
				for (int i = 0; i < valuations.length; i++) {
					if (a_bids.containsKey(i)) {
						myBids = myBids + a_bids.get(i);
					} else {
						myBids = myBids + 0.0;
					}

					if (i < valuations.length - 1)
						myBids = myBids + " ";
				}

				// /////////////////////////////////////////////

				out.println(myBids); // Send agent's bids to server
				System.out.println("Mybids: " + myBids + "\n");
			}

			// ***********************************************************************

			// Observe the state of auction variables. Store information locally
			else if (fromServer.startsWith("observe-auction-state:")) {
				String[] stateVars = fromServer.split("[ ]");// tokens delimited
																// with spaces

				int numAgents = Integer.valueOf(stateVars[1]);
				int numTimeSlots = Integer.valueOf(stateVars[2]);
				int currentRound = Integer.valueOf(stateVars[3]);// 1st round ->
																	// 0
				// currentRound is 0-indexed, so currentRound = 0 for first
				// round

				String[] leaderIDs = new String[numTimeSlots];// leaders and
																// their bids
																// for each
																// time-slot
				double[] leaderBids = new double[numTimeSlots];

				if (currentRound > 0)// no winners before first round
					for (int i = 0; i < (numTimeSlots * 2); i += 2) {	// 2
																		// records
																		// per
																		// time
																		// slot
																		// [leader,
																		// bid]
						leaderIDs[i / 2] = stateVars[4 + i];
						leaderBids[i / 2] = Double.valueOf(stateVars[5 + i]);
					}

				System.out.println("Observing state:\nNumber of agents = "
						+ numAgents + "\nNumber of time slots = "
						+ numTimeSlots);
				System.out.println("Current auction state:");
				for (int i = 0; i < leaderIDs.length; i++)
					System.out
							.println("Time-Slot " + i + ": current-leader: "
									+ leaderIDs[i] + ", current bid = "
									+ leaderBids[i]);
				// /////////////////////////////////////////////
				// YOUR CODE HERE
				// You may want to record some of the state
				// information her, especially the IDs of
				// current auction leaders for each time-slot
				// as well as the current leading bids.
				// /////////////////////////////////////////////

				if (currentRound > 0) {
					for (int i = 0; i < numTimeSlots; i++) {
						boolean is_winner = false;
						if (leaderIDs[i].equals(agent_name)) {
							is_winner = true;
						}

						int ask_epsilon = 1;
						double cur_price = leaderBids[i];
						double payment = 0;
		        		
		        		if (is_winner)
		        			payment = cur_price;
		        		else
		        			payment = 0;
		        		
						double ask_price = cur_price + ask_epsilon;

						agent.postResult(new Result(auctions.get(i), is_winner,
										 payment, ask_price, cur_price, ask_epsilon));
					}
				}

				System.out.println(agent.information());
				
				// /////////////////////////////////////////////
				out.println("State Observed");// let the server know client
												// received state info
				System.out.println("");// helps separate the output
			}

			// ***********************************************************************

			else if (fromServer.startsWith("observe-final-outcome:")) {
				// fromServer =
				// "observe-final-outcome: winnerID_0 price_0 winnerID_1 price_1 ..."
				String[] outcomeVars = fromServer.split("[ ]");// tokens
																// delimited
																// with spaces

				// for each slot, announce winner's ID and their price
				for (int i = 1; i < outcomeVars.length; i += 2) {
					String winnerID = outcomeVars[i];
					double winPrice = Double.valueOf(outcomeVars[i + 1]);
					System.out.println("Time Slot " + ((i + 1) / 2)
							+ " awarded to [" + winnerID + "] for price = "
							+ winPrice);
				}
				out.println("Final Outcome Observed");// let the server know
														// client received state
														// info
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
}
