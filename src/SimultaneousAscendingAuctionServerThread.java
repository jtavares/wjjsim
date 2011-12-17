/*
 * Author: TJ Goff
 * 
 * This class implements a thread which handles a socket connection to a client.
 * The client participates in an auction on the host, and messages between the
 * host and client are processed through these threads.  The threads also do
 * most of the work related to updating the state of the auction when their
 * corresponding client performs some action (like placing a bid).
 * 
 * You should not have to edit the Server or the ServerThread classes.  Instead,
 * replace the "random" bidding behavior in the client class with some better
 * decision logic.  The Server and ServerThreads may be updated, so any changes
 * you make in these classes will be lost if/when the Server and ServerThread
 * classes get improvements or fixes.
 * 
 * For help, contact: tom_goff@brown.edu
 */

import java.net.*;
import java.util.*;
import java.io.*;

public class SimultaneousAscendingAuctionServerThread extends Thread {
    private Socket socket = null;
    private Auction myAuction;
    private int minNumPlayers;
    private String clientID = "";//unique ID for the client associated with this thread
    
    private boolean isReporter = false;// if true, this thread prints information on Host command line
    
    double minIncrement = 1;
    
    public SimultaneousAscendingAuctionServerThread(Socket socket, Auction a, int minNum) {
    	super("SimultaneousAscendingAuctionServerThread"); //call superclass constructor: Thread(String name)
    	this.socket = socket;
    	this.myAuction = a;
    	this.minNumPlayers = minNum;
    }

    //When the socket is created, opens communication loop with client.
    public void run() {
    	try {
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
					    new InputStreamReader(
					    socket.getInputStream()));
		    
		    Random rand = new Random();
		    
		    
		    //prompt the user for an ID which will uniquely identify them
		    boolean idPass = false;
		    while( !idPass ){
		    	out.println("Send client name");
		    	clientID = in.readLine().replaceAll(" ", "");//can't have spaces... those are delimiters
		    	idPass = myAuction.addBidder( clientID  );
		    	if(idPass && myAuction.bidderIDs.size() == this.minNumPlayers)
		    		myAuction.started = true;//we have enough players to proceed!
		    }
		    
		    System.out.println("Agent ID [" +clientID+ "] added successfully" +
		    					"\nNext, parameters, when auction starts...");
		    
		    //don't start yet... wait for myAuctin.started == true
		    while(!myAuction.started){
		    	//waiting...
		    }
		    
		    
		    //Randomly generate parameters for the agent...
		    int numSlotsNeeded = rand.nextInt(myAuction.numSlots)+1; //random 1 to numSlots (5)
		    
		    //deadline is slot number (0-indexed), after which time player has 0 payoff
		    int deadline = rand.nextInt(myAuction.numSlots-numSlotsNeeded+1) + numSlotsNeeded -1;
		    //always possible to meet deadline (never lower than numSlotsNeeded).
		    
			double[] vals = new double[myAuction.numSlots];//payoffs for finishing at each time
			for(int i=0; i < vals.length; i++)
				vals[i] = rand.nextInt(50)+1; //random from [1...50]
			Arrays.sort(vals);//smallest payoffs first... flip it
			for(int i=0; i < vals.length/2; i++){
				double tmp = vals[i];
				vals[i] = vals[ vals.length-1-i ];
				vals[ vals.length-1-i ] = tmp;
			}//largest payoffs are first.  Utility decreases for later project completion
			
			//any payoffs beyond deadline should be set to 0
			for(int i=deadline+1; i < vals.length; i++ )
				vals[i] = 0;
			
			
			//Send agent's parameters to client
			String params = numSlotsNeeded + " " + deadline;
			for(int i=0; i < vals.length; i++)
				params = params + " " + vals[i];
			
		    boolean paramPass = false;
		    while( !paramPass ){
		    	out.println("agent-parameters: " + params);
		    	paramPass = in.readLine().length() > 0;//any response counts as success
		    	
		    	int myIndex = myAuction.bidderIDs.indexOf(clientID);
		    	myAuction.clientStates.set(myIndex,"params received");
		    }
		    
		    //wait for all agents to reply with "I received my parameters"
		    boolean allParams = false;
		    while( !allParams ){
		    	allParams = true;
		    	for(int i=0; i< myAuction.clientStates.size(); i++)
		    		if( !myAuction.clientStates.get(i).equalsIgnoreCase("params received") )
		    			allParams = false; 
		    }
		    
		    
		    //CHECK THAT ALL AGENTS ARE READY (have been initialized)
		    //if number of players expected == number of ID's in myAuction, start auction
		    if(myAuction.bidderIDs.size() == myAuction.numClients)
		    	myAuction.startAuction();
		    while( !myAuction.started ){
		    	//wait for auction to start... not enough clients
		    }
		    
		    if( isReporter ) 
		    	System.out.println("Update: "+myAuction.bidderIDs.size()+
		    				" clients have joined the auction. Auction starting...");
		   
		    
		    
		    
		    
		    
		    
		    int clientIndex = myAuction.bidderIDs.indexOf(clientID);
		    //Run the auction until nobody places a valid bid in any rounds
		    do{
		    	if( isReporter )
					System.out.println("\nStart of round " + myAuction.round);
		    	//System.out.println("Client " + clientID + " reached point-0 in round " + myAuction.round);
		    	
		    	//reset clients bids (but leave price-paid as current winning bid
		    	for(int j=0; j < myAuction.bids.get(clientIndex).length; j++)
		    		myAuction.bids.get(clientIndex)[j] = -1;//indicate invalid bid
		    	
		    	myAuction.clientStates.set(clientIndex, "ready");//marks thread's progress, set at start of round to "ready"
		    	while( !myAuction.allclientStatesAre("ready", "Point_A") ){
					//wait for other threads to catch up... unless they're at the next checkpoint!
				}
		    	//System.out.println("Client " + clientID + " reached point-1 in round " + myAuction.round);
		    	
		    	
		    	//send state information to client
		    	String auctionState = "observe-auction-state: " + minNumPlayers + " " + 
										myAuction.numSlots + " " + myAuction.round;
		    	//don't bother sending winners/prices before the first round
		    	if(myAuction.round > 0){
		    		//for each slot, get winnerID and current price
		    		for(int s=0; s < myAuction.numSlots; s++){
		    			String winID = ".";//designates no current winner
		    			double winBid = 0;
		    			for(int a=0; a < myAuction.paid.size(); a++){ //for each agent check bid at slot s
		    				if( myAuction.paid.get(a)[s] > 0 ){
		    					winID = myAuction.bidderIDs.get(a);
		    					winBid = myAuction.paid.get(a)[s];
		    				}
		    			}
		    			auctionState = auctionState + " " + winID + " " + winBid;
		    		}
		    	}
				//observe-auction-state: numAgents numSlots round winner0 price0 winner 1 price1...
				out.println(auctionState);
				String response = in.readLine();// ideally "State Observed"
		    	
				
				//Before checking for new valid bid activity, reset bidActivity marker
				if( isReporter )
		    		myAuction.bidActivity = false;
				
				//wait for all agents to make state observation... 
		    	myAuction.clientStates.set(clientIndex, "Point_A");
				while( !myAuction.allclientStatesAre("Point_A", "Point_B") ){
					//wait for other threads to catch up... unless they're already at the next step!
				}
				//System.out.println("Client " + clientID + " reached point-2 in round " + myAuction.round);
		    	
		    	//prompt client for bids
				double[] currBids = new double[myAuction.numSlots];
				out.println("submit-bid");//prompt client for bid
				//read client bids, parse and round to 2 decimal places
				response = in.readLine();
        		String[] tokens = response.split("[ ]");//spaces as delimiters
				for(int i=0; i < currBids.length; i++)
					currBids[i] = Math.max(0,((double)((int)(Double.valueOf(tokens[i])*100)))/100);
        			//convert token to double, round to 2 decimal places, and take max with 0
				//all bids should be at least 0
				
				//record this client's bids
				for(int i=0; i < myAuction.bids.get(clientIndex).length; i++)
					myAuction.bids.get(clientIndex)[i] = currBids[i];
		    	
				
				//System.out.println("Client " + clientID + " reached point-3 in round " + myAuction.round);
		    	//wait for all bids to be received
		    	while( !myAuction.allBidsReceived() ){
		    		//waiting
		    	}
		    	
		    	myAuction.clientStates.set(clientIndex, "Point_B");
				while( !myAuction.allclientStatesAre("Point_B", "Point_C") ){
					//wait for other threads to catch up... unless they're at the next state already!
				}
		    	//System.out.println("Client " + clientID + " reached point-4 in round " + myAuction.round);
		    	
		    	//All bids have been received... check if anything should be updated
		    	//if an update is made anywhere, set bidActivity = true;
		    	//only the reporter thread should run this (once)
		    	for(int s=0; s < myAuction.numSlots && isReporter; s++){
		    		//find existing leading bid (if any) using values from 'paid'
		    		double currLeadBid = -1;
		    		for(int a=0; a < myAuction.numClients; a++)
		    			if(myAuction.paid.get(a)[s] > currLeadBid )
		    				currLeadBid = myAuction.paid.get(a)[s];
		    		
		    		//find top bidder amongst new bids
		    		double topNewBid = -1;
		    		for(int a=0; a < myAuction.numClients; a++)
		    			if(myAuction.bids.get(a)[s] > topNewBid )
		    				topNewBid = myAuction.bids.get(a)[s];
		    		
		    		//find all agents who made the top bid and pick 1 if tie
		    		Vector<String> winnerIDs = new Vector<String>();
		    		for(int a=0; a < myAuction.numClients; a++)
		    			if( myAuction.bids.get(a)[s] == topNewBid )
		    				winnerIDs.add( myAuction.bidderIDs.get(a) );
		    		while(winnerIDs.size() > 1)
		    			winnerIDs.remove( rand.nextInt(winnerIDs.size()) );
		    				    		
		    		
		    		//check if the top new bid beats the current bid (changing the auction)
		    		if( topNewBid >= currLeadBid+minIncrement ){
		    			myAuction.bidActivity = true;
		    			//clear 'paid', then update new winner's 'paid' value
		    			for(int a=0; a < myAuction.paid.size(); a++)
		    				myAuction.paid.get(a)[s] = -1;	
		    			//update with paid.get(indexOfWinner)[s] = topNewBid
		    			int newLeaderIndex = myAuction.bidderIDs.indexOf(winnerIDs.get(0));
		    			myAuction.paid.get(newLeaderIndex)[s] = topNewBid;
		    		}
		    		
		    		//reset all "bids" to -1
		    		for(int a=0; a < myAuction.bids.size(); a++)
		    			myAuction.bids.get(a)[s] = -1;	
		    	}
		    	
		    	
		    	//System.out.println("Client " + clientID + " reached point-5 in round " + myAuction.round);
		    	if(isReporter)
		    		myAuction.round = myAuction.round+1; //only reporter updates this, so only increments once
		    	
		    	//wait for all agents to get here... 
		    	myAuction.clientStates.set(clientIndex, "Point_C");
				while( !myAuction.allclientStatesAre("Point_C", "ready") ){
					//wait for other threads to catch up... unless they're at the next state already!
				}
				if( isReporter )
					System.out.println("End of round " + (myAuction.round-1));
				//System.out.println("Client " + clientID + " reached point-6 in round " + (myAuction.round-1));
		    }while(myAuction.bidActivity);
		    
		   
		    
		    
		    if( isReporter )
		    	System.out.println("\nBidding activity has ended.  Auction is over!");
		   
		 
		    //Determing final state values and send report to client
		    String outcomeReport = "observe-final-outcome:"; 
		    
		    for(int s=0; s < myAuction.numSlots; s++){
		    	String winner = "."; //find winner and price paid for Sth time slot
		    	double pricePaid = -1;
		    	for(int a=0; a < myAuction.numClients; a++)
		    		if( myAuction.paid.get(a)[s] > 0 ){
		    			winner = myAuction.bidderIDs.get(a);
		    			pricePaid = myAuction.paid.get(a)[s];
		    		}
		    	outcomeReport = outcomeReport + " " + winner + " " + pricePaid;
		    }
		    //outcomeReport = "observe-final-outcome: winnerID_0 price_0 winnerID_1 price_1 ..."
		    
		    //send outcomeReport to client
		    out.println(outcomeReport);
		    String reply = in.readLine();//wait for client to respond
		    
		    
		    
		    if(isReporter){
		    	System.out.println("\n\nFINAL EVALUATIONS:");
		    	//calculate final evaluations and totals paid for each agent.  Store in myAuction.clientState
		    	for(int a=0; a < myAuction.numClients; a++){
		    		Vector<Double> slotsWon = new Vector<Double>();
		    		double totalPaid = 0;
		    		for(int s=0; s < myAuction.paid.get(a).length;  s++)
				    	if( myAuction.paid.get(a)[s] > 0 ){
				    		slotsWon.add(new Double(s));
				    		totalPaid += ((double)((int)(myAuction.paid.get(a)[s]*100)))/100 ;
				    	}
		    		
		    		double myEvaluation;   		
		    		if( numSlotsNeeded > slotsWon.size() )
			    		myEvaluation = 0;//didn't get enough slots
			    	else{
			    		//doneTime is the earliest time that client can reach numSlotsNeeded
			    		int doneTime = (int)((double)slotsWon.get(numSlotsNeeded-1));//numNeeded isn't 0-indexed, so -1
			    		myEvaluation = vals[doneTime];//anything after deadline has eval=0
			    	}
			    	myAuction.setClientState(clientID, ""+myEvaluation);
		    		
			    	System.out.println(myAuction.bidderIDs.get(a) + " has final evaluation score of " +
			    						myEvaluation + ".  Total amount paid = " + totalPaid);
		    	}
		    }//end evaluation report
		    
		    
		    
		    out.close();
		    in.close();
		    socket.close();
		}//end try
		catch (IOException e) {
		    e.printStackTrace();
		}
    }//end run
    
    
    
    public void setReporter(boolean b){
    	this.isReporter = b;
    }
    
    
    public void closeConnection(){
    	try {
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    out.println("END");
		    out.close();
		    socket.close();
		}
		catch (IOException e) {
		    e.printStackTrace();
		}    
    }
    
}