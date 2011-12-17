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

public class Sequential2ndPriceAuctionServerThread extends Thread {
    private Socket socket = null;
    private Auction myAuction;
    private int minNumPlayers;
    private String clientID = "";//unique ID for the client associated with this thread
    
    private boolean isReporter = false;
    //if true, this thread prints information on Host command line
    
    
    private double[] vals;//this stores the valuation of associated agent
    int numSlotsNeeded;//number of slots this agent needs
    int deadline; //deadline is slot number (0-indexed), after which time player has 0 payoff
    
    
    public Sequential2ndPriceAuctionServerThread(Socket socket, Auction a, int minNum) {
    	super("Sequential2ndPriceAuctionServerThread"); //call superclass constructor: Thread(String name)
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
		    	clientID = in.readLine().replaceAll(" ", "");
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
		    numSlotsNeeded = rand.nextInt(myAuction.numSlots)+1; //random 1 to numSlots (5)
		    
		    //deadline is (int) slot number (0-indexed), after which time player has 0 payoff
		    deadline = rand.nextInt(myAuction.numSlots-numSlotsNeeded+1) + numSlotsNeeded -1;
		    //always possible to meet deadline (never lower than numSlotsNeeded).
		    
		    //double[] vals
			vals = new double[myAuction.numSlots];//payoffs for finishing at each time
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
		    
		    boolean allParams = false;
		    while( !allParams ){
		    	allParams = true;
		    	for(int i=0; i< myAuction.clientStates.size(); i++)
		    		if( !myAuction.clientStates.get(i).equalsIgnoreCase("params received") )
		    			allParams = false; 
		    }
		    
		    
		    if( isReporter ) 
		    	System.out.println("Prompting client for bids.");
		    
		    
		    
		    
		    //run each round of the auction in order
		    for(int round=0; round < myAuction.numSlots; round++){
		    	//send state info to client
		    	String auctionState = "observe-auction-state: " + minNumPlayers + " " + 
		    						   myAuction.numSlots + " " + round;
		    	for(int r=0; r < round; r++){
		    		//at this point, only winner has non-zero price-paid
		    		String winID = ".";//designates no winner
		    		double winPrice = -1;
		    		double winBid = -1;
		    		for(int agent=0; agent < myAuction.bidderIDs.size(); agent++){
		    			if( myAuction.paid.get(agent)[r] > 0 ){
		    				winID = myAuction.bidderIDs.get(agent);
		    				winPrice = myAuction.paid.get(agent)[r];
		    				winBid = myAuction.bids.get(agent)[r];
		    			}
		    		}
		    		auctionState = auctionState+" "+winID+" "+winPrice+" "+winBid;
		    	}
		    	//observe-auction-state: numAgents numSlots round winner0 price0 winner 1 price1...
		    	out.println(auctionState);
				String response = in.readLine();// ideally "State Observed"
		    	
				
				
				//collect bid from client.  
				double currBid = 0;
				out.println("submit-bid");//prompt client for bid
				//read client bid and round to 2 decimal places
				currBid = ((double)((int)(Double.valueOf(in.readLine())*100)))/100;
				currBid = Math.max(0, currBid );
				//record this client's bid
				int clientIndex = myAuction.bidderIDs.indexOf(clientID);
				myAuction.bids.get(clientIndex)[round] = currBid;
				
				
				
				
				//If all bids have been received (all are not -1), determine winner 
				//and update auction records.  Also advance Auction.round
				if( myAuction.isRoundOver(round) ){
					double fp = -1; //first price
		    		double sp = -1; //second price
		    		Vector<String> winnerIDs = new Vector<String>();
		    		
		    		//find first price and second price in current round
		    		for(int i=0; i < myAuction.bids.size(); i++){
		    			if( myAuction.bids.get(i)[round] > fp ){
		    				sp = fp;//bump current fp to second price
		    				fp = myAuction.bids.get(i)[round];
		    			}
		    			else if(myAuction.bids.get(i)[round] > sp){
		    				sp = myAuction.bids.get(i)[i=round];//update second price
		    			}
		    		}//fp and sp are first and second prices
					
		    		//find all bidder who bid first price
		    		for(int i=0; i < myAuction.bids.size(); i++){
		    			if( myAuction.bids.get(i)[round] == fp )
		    				winnerIDs.add( myAuction.bidderIDs.get(i) );
		    		}
		    		//pick a winner at random (eliminate all but 1)
		    		while( winnerIDs.size() > 1)
		    			winnerIDs.remove( rand.nextInt(winnerIDs.size()) );
		    		
		    		//for all bids in this round, if they were from winner,
		    		//set to second price, otherwise set to 0
		    		for(int i=0; i < myAuction.bids.size(); i++){
		    			if( myAuction.bidderIDs.get(i).equals(winnerIDs.get(0)) )
		    				myAuction.paid.get(i)[round] = sp;
		    			else
		    				myAuction.paid.get(i)[round] = 0;//this ensures winner is permanent
		    		}
		    		
					myAuction.round = myAuction.round + 1;//breaks waiting loop to start next round
				}//end if( myAuction.isRoundOver(round) )
				
				
				//wait for auction to be updated with winner
		    	while( round == myAuction.round ){
		    		//wait for auction to move to next round...
		    	}	
		    }//end foreach round (to run each round)
		    
		    
		    
		    
		    //All rounds have been completed.  Winner of each round is
		    //the only non-zero bidder, and the winner's bid has been
		    //changed to the price that they paid. 
		    if(isReporter){ //print Agents and their bids
		    	System.out.println("All auction rounds have been completed! Computing Evaluations...");
			   	
		    	//this actually shows winner in each auction
		    	/*for(int i=0; i < myAuction.bids.size(); i++){
			   		System.out.print("Agent: " + myAuction.bidderIDs.get(i) + " bids: [");
			   		for(int j=0; j < myAuction.bids.get(i).length; j++)
			   			System.out.print(myAuction.bids.get(i)[j] + " ");
			   		System.out.println("]");
			   	}*/
		   	}
		    	
		    
		    //calculate client's final valuation and store in Auction.clientState
		    int clientIndex = myAuction.bidderIDs.indexOf(clientID);
		    Vector<Double> slotsWon = new Vector<Double>();
		    for(int i=0; i < myAuction.paid.get(clientIndex).length;  i++)
		    	if( myAuction.paid.get(clientIndex)[i] > 0 )
		    		slotsWon.add(new Double(i));
		    //now we know which slots this client won...
		    double myEvaluation;
	    	if( numSlotsNeeded > slotsWon.size() )
	    		myEvaluation = 0;//didn't get enough slots
	    	else{
	    		//doneTime is the earliest time that client can reach numSlotsNeeded
	    		int doneTime = (int)((double)slotsWon.get(numSlotsNeeded-1));//numNeeded isn't 0-indexed, so -1
	    		myEvaluation = vals[doneTime];//anything after deadline has eval=0
	    	}
	    	myAuction.setClientState(clientID, ""+myEvaluation);
	    	
		    
		    while( !allEvaluationsDone(myAuction) ){
		    	//wait for all clients to calculate final valuations
		    }
		    
		    
		    
		    
		    //auction is over and all final evaluations, winners and prices paid
		    //are available in the myAuction object... make a final report to client
		    
		    //for each slot, announce winnerID and price paid
		    String outcomeReport = "observe-final-outcome:"; 
		    if(isReporter)
		    	System.out.println("\n\nFinal outcome report:");
		    for(int i=0; i < myAuction.numSlots; i++){
		    	String winner ="."; //indicates no winner
		    	double pricePaid = -1;
		    	double winBid = -1;
		    	for(int j=0; j < myAuction.bidderIDs.size(); j++)
		    		if( myAuction.paid.get(j)[i] > 0){
		    			winner = myAuction.bidderIDs.get(j);
		    			pricePaid = myAuction.paid.get(j)[i];
		    			winBid = myAuction.bids.get(j)[i];
		    		}
		    	outcomeReport = outcomeReport + " " + winner + " " + pricePaid + " " + winBid;
		    	if(isReporter)
		    		System.out.println("Time Slot " + i + " awarded to [" + winner + 
		    						   "] for price = " + pricePaid + " with bid " + winBid);
		    }
		    //send outcomeReport to client
		    out.println(outcomeReport);
		    String reply = in.readLine();
		    
		    //print out final evaluations
		    if(isReporter){
		    	System.out.println("\nFinal Evaluations:");
		    	for(int i=0; i < myAuction.bidderIDs.size(); i++)
		    		System.out.println(myAuction.bidderIDs.get(i) + 
		    							": evaluation = " + myAuction.clientStates.get(i));
		    }

		
		    //clean up
		    out.close();
		    in.close();
		    socket.close();
		}
	
		catch (IOException e) {
		    e.printStackTrace();
		}
    }//end run
    
    
    //sets this thread to be the reporter or not (only reporter prints to command prompt)
    public void setReporter(boolean b){
    	this.isReporter = b;
    }
    
    
    //return true if the string is a number with only digits and/or decimal point chars
    public boolean isNumber(String s){    
        for (char c: s.toCharArray()){
            if(!Character.isDigit(c) && c!= '.')//digits and decimals only
                return false;
        }
        return true;
    }
    
    
    //return true if all client states have been set to numbers indicating final
    //evaluations have been calculated
    public boolean allEvaluationsDone(Auction myAuction){
    	for(int i=0; i< myAuction.clientStates.size(); i++)
    		if( !isNumber(myAuction.clientStates.get(i)) )
    			return false;
    	return true;
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