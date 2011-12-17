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

public class Simultaneous2ndPriceAuctionServerThread extends Thread {
    private Socket socket = null;
    private Auction myAuction;
    private int minNumPlayers;
    private String clientID = "";//unique ID for the client associated with this thread
    
    private boolean isReporter = false;
    //if true, this thread prints information on Host command line
    
    
    private double[] vals;//this stores the valuation of associated agent
    int numSlotsNeeded;//number of slots this agent needs
    int deadline; //deadline is slot number (0-indexed), after which time player has 0 payoff
    
    
    public Simultaneous2ndPriceAuctionServerThread(Socket socket, Auction a, int minNum) {
    	super("Simultaneous2ndPriceAuctionServerThread"); //call superclass constructor: Thread(String name)
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
		    //String inputLine, outputLine;
		    
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
		    }
		    
		    
		    if( isReporter ) 
		    	System.out.println("Prompting client for bids.");
		    
		    boolean bidsSubmitted = false;
		    while( !bidsSubmitted ){
				out.println("submit-bid"); //prompt client to submit bids
				String response = in.readLine();
				String delims = "[ ,]";//spaces or commas as delimiters
        		String[] tokens = response.split(delims);
        		
				if( tokens.length == vals.length ){ //should be a bid for each time slot, even $0
					int clientIndex = myAuction.bidderIDs.indexOf(clientID);
					for(int i=0; i<tokens.length; i++ ){
						//round to 2 decimal places
						double currBid = ((double)((int)(Double.valueOf(tokens[i])*100)))/100;
						myAuction.bids.get(clientIndex)[i]= Math.max(0,currBid);
					}
					bidsSubmitted = true;
				}
		    }
		    
		    
		    if( isReporter )
		    	System.out.println("Bids have been submitted... are we done?");
		    
		    //wait until the auction actually ends...
		    boolean auctionOver = false;
		    while( !auctionOver ){
		    	auctionOver = myAuction.isOver();
		    }
		    
		    
		    if( myAuction.isOver() ){
		    	if(isReporter){ //print Agents and their bids
			    	System.out.println("\n\nALL CLIENTS HAVE SUBMITTED BIDS");
			    	for(int i=0; i < myAuction.bids.size(); i++){
			    		System.out.print("Agent: " + myAuction.bidderIDs.get(i) + " bids: [");
			    		for(int j=0; j < myAuction.bids.get(i).length; j++)
			    			System.out.print(myAuction.bids.get(i)[j] + " ");
			    		System.out.println("]");
			    	}
		    	}
		    	
		    	//Send final outcome to client
		    	boolean outcomeObserved = false;
		    	double myEvaluation = -1;//calculate the final evaluation for this agent
			    while( !outcomeObserved ){
			    	//create a string containing a report of the auction outcome...
			    	String outcomeReport = "observe-final-outcome:";
			    	Vector<Double> slotsWon = new Vector<Double>();
			    	
			    	//For each slot, determine winnerID and price paid
			    	for(int i=0; i < myAuction.numSlots; i++){
			    		//find highest bidder (or break a tie)
			    		double fp = -1; //first price
			    		double sp = -1; //second price
			    		Vector<String> winnerIDs = new Vector<String>();
			    		
			    		//find first price and second price
			    		for(int j=0; j < myAuction.bids.size(); j++){
			    			if( myAuction.bids.get(j)[i] > fp ){
			    				sp = fp;//bump current fp to second price
			    				fp = myAuction.bids.get(j)[i];
			    			}
			    			else if(myAuction.bids.get(j)[i] > sp){
			    				sp = myAuction.bids.get(j)[i];//update second price
			    			}
			    		}//fp and sp are first and second prices
			    		
			    		//find all bidder who bid first price
			    		for(int j=0; j < myAuction.bids.size(); j++){
			    			if( myAuction.bids.get(j)[i] == fp )
			    				winnerIDs.add( myAuction.bidderIDs.get(j) );
			    		}
			    		//pick a winner at random (eliminate all but 1)
			    		while( winnerIDs.size() > 1)
			    			winnerIDs.remove( rand.nextInt(winnerIDs.size()) );
			    		
			    		String winner = "_";//"_" denotes "no winner"
			    		if( !winnerIDs.isEmpty() )
			    			winner = winnerIDs.get(0);
			    		else
			    			sp = 0;//no winner...
			    		
			    		if( winner.equals(clientID) )
			    			slotsWon.add(new Double(i) );//this client won the ith slot
			    		
			    		//add results for this slot to outcomeReport
			    		outcomeReport = outcomeReport + " " + winner + " " + sp;
			    	}//end foreach slot find winner and price
			    	
			    	
			    	//calculate this client's valuation
			    	if( numSlotsNeeded > slotsWon.size() )
			    		myEvaluation = 0;//didn't get enough slots
			    	else{
			    		//doneTime is the earliest time that client can reach numSlotsNeeded
			    		int doneTime = (int)((double)slotsWon.get(numSlotsNeeded-1));//numNeeded isn't 0-indexed, so -1
			    		myEvaluation = vals[doneTime];//anything after deadline has eval=0
			    	}
			    	//set each agent's state to their final evaluation
			    	myAuction.setClientState(clientID, ""+myEvaluation);
			    	
			    	
			    	if(isReporter){
			    		System.out.println("Final outcome report\n" + outcomeReport);
			    		String[] outcomeVars = outcomeReport.split("[ ]");
			    		for(int i=1; i < outcomeVars.length; i+=2){
	        				//for ith time-slot, winner and price
	        				String winnerID = outcomeVars[i];
	        				double winPrice = Double.valueOf(outcomeVars[i+1]);
	        				System.out.println("Time Slot " + ((i+1)/2) + " awarded to [" + 
	        									winnerID + "] for price = " + winPrice);
	        			}
			    	}		    	
			    	
			    	
					out.println(outcomeReport); //tell client that final outcome
					String response = in.readLine();
	        		
					if( response.equalsIgnoreCase("Final Outcome Observed") )
						outcomeObserved = true;
					else{ //error: client didn't get full outcome report...
						System.out.println("Client didn't get full outcome report");
					}
			    }// end while( !outcomeObserved )
		    }//end if(myAuction.isOver())
		    
		    
		    //if Reporter, wait until all clients have calculated their final evaluations
		    if( isReporter ){
			    boolean allEvaluationsDone = false;
			    while( !allEvaluationsDone ){
			    	allEvaluationsDone = true;
			    	for(int i=0; i < myAuction.clientStates.size(); i++)
			    		if( !isNumber(myAuction.clientStates.get(i))  )
			    			allEvaluationsDone = false;
			    }
			    //print out final evaluations for each client
			    System.out.println("Final Evaluations:");
			    for(int i=0; i < myAuction.clientStates.size(); i++){
    				//for ith time-slot, winner and price
    				String currID = myAuction.bidderIDs.get(i);
    				String currEval = myAuction.clientStates.get(i);
    				System.out.println(currID + ": evaluation = " + currEval);
    			}
		    }
		    
		    out.close();
		    in.close();
		    socket.close();
		}
	
		catch (IOException e) {
		    e.printStackTrace();
		}
    }//end run
    
    
    
    public void setReporter(boolean b){
    	this.isReporter = b;
    }
    
    
    public boolean isNumber(String s){    
        for (char c: s.toCharArray()){
            if(!Character.isDigit(c) && c!= '.')//digits and decimals only
                return false;
        }
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