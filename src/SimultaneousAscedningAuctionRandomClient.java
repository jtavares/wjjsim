import java.io.*;
import java.net.*;
import java.util.*;

public class SimultaneousAscedningAuctionRandomClient {
	
	
    public static void main(String[] args) throws IOException {
    	
    	System.out.println("SimultaneousAscendingAuctionRandomClient starting");	
    	
        Socket auctSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        String hostName = "127.0.0.1";//IP of host (may be local IP)
        int socketNum = 1305; //the luckiest socket
        
        Agent agent = null;
        Valuation valuationMethod = null;
        String agent_name="Anonymous";
        List<SBAuction> auctions=null;
	    List<Agent> agents=null;
	    
	   
        
        try {
            in = new BufferedReader(new FileReader("./src/IP_and_Port.txt"));
            //two lines in this file.  First is hostName/IP address, and second is socket number of host 
            hostName = in.readLine();
            socketNum = Integer.valueOf( in.readLine() );	
            in.close();
        } catch (IOException e) {
        }
       
        
        //These are values the agent should remember and use
        //Values are initialized when Server sends parameters
        int numSlotsNeeded = -1;
        int deadline = -1;
        double[] valuations = null;
        System.out.println("test 3");
        
        try {
            auctSocket = new Socket(hostName, socketNum);
            out = new PrintWriter(auctSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(auctSocket.getInputStream()));
        } 
        catch (UnknownHostException e) {
            System.err.println("Don't know about host: "+hostName+".");
            System.exit(1);
        } 
        catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: host.");
            System.exit(1);
        }
        System.out.println("Connection to host established");
        
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
      
        
        //continue to prompt commands from host and respond
        while ((fromServer = in.readLine()) != null) {
        	System.out.println("Server: " + fromServer);
        	
        	//Send host this client's unique ID
        	if( fromServer.equalsIgnoreCase( "Send client name") ){
            	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Enter a unique ID string...");
                
                agent_name=br.readLine();
                out.println( agent_name );
                //OPTIONAL CHANGE:
                //YOU MAY CHOOSE TO REPLACE THE LINE ABOVE WITH THE LINE BELOW
                //out.println("My_Hardcoded_Name");
                
                System.out.println("User ID sent.  If prompted again, choose another ID/Name string.");
                System.out.println("Waiting for server to start auction...");
        	}
        	
        	//***********************************************************************
        	
        	else if( fromServer.startsWith("agent-parameters:") ){
        		String[] params = fromServer.split("[ ]");//tokens delimited with spaces
       
        		numSlotsNeeded = Integer.valueOf(params[1]);//1st param is number of slots needed
               
        		deadline = Integer.valueOf(params[2]);//2nd param is deadline
                //DEADLINE IS INDEX OF LAST AUCTION OF VALUE (0-indexed).
                //example: deadline = 1 -->  first two time-slots can be used
        		
                valuations = new double[ params.length-3 ];//first 3 stings aren't valuations
                for(int i=3; i< params.length; i++)
                	valuations[i-3] = Double.valueOf(params[i]);
                
                
                ///////////////////////////////////////////////
                //  YOUR CODE HERE
                //  You probably want to store the parameters sent from host.
                //  For example, you could store them in global variables, 
                //  or use them to initialize an agent class you wrote.
                ///////////////////////////////////////////////
                
                //EDIT HERE
                //??  how to define the number for each agent ?
                //?? whether to change to another kind of agent 
              
                valuationMethod=new SchedulingValuation(numSlotsNeeded,valuations);
        		agent = new RandomAgent(0,valuationMethod);
                
        		// Send starting information= to agents
        //		agent.postResult(new Result(null, false, 0, ar.getAskPrice(), 0, ar.getAskEpsilon()));
        		
        		int  ask_price=0;
        	    int  ask_epsilon=0;
        	    agents= new ArrayList<Agent>();
        	    auctions=new ArrayList<SBAuction>(numSlotsNeeded);
        	    agents.add(agent);
        	     
        		for (int i =0;i< valuations.length; i++)
        		{
        		SBAuction auction= new SBNPAuction(i, 0, 1, 1, agents, 1);
        		auctions.add(auction);
        		agent.postResult(new Result(auctions.get(i), false, 0, ask_price, 0, ask_epsilon));
        		}
        
        		System.out.println("Auctionsize : "+auctions.size());
        		agent.openAllAuctions();
        		
                ///////////////////////////////////////////////
                out.println( "Parameters accepted" );
        	}
        	
        	//***********************************************************************
        	
        	else if( fromServer.equalsIgnoreCase("submit-bid") ){
        		//Here are values you should use... be more clever than random!
        		//numSlotsNeeded (int)
                //deadline (int)  index of last timeSlot of value (starts at 0)
                //valuations (double[numSlots])
        		
        		///////////////////////////////////////////////
                //  YOUR CODE HERE
                //  Create a string, like myBids, with your bid(s).
        		//  If placing multiple bids, separate bids with spaces spaces.
        		//  If multiple bids, order bids as follows:
        		//  myBids = "timeSlot1Bid  timeSlot2Bid ... timeslot5Bid";
        		//
        		//Note:  bids get rounded to 2 decimal places by host. 5.031 -> 5.03
                ///////////////////////////////////////////////
                
        		/*
        		String myBids = "";
        		Random r = new Random();//make random bids... 1 for each time slot
        		for(int i=0; i < valuations.length; i++){
        			myBids = myBids + (r.nextDouble()*10);//random bid 0 to 10
        			if(i < valuations.length-1)
        				myBids = myBids + " ";//separate bids with a space!  IMPORTANT!
        		}
        		*/
        		
        		//EDIT HERE!
        		
        		HashMap<Integer,Double> a_bids= agent.getBids();
        		String myBids="";
        		for (int i=0;i<valuations.length;i++) 
        		{
        			if (a_bids.containsKey(i))
        			{
        				myBids=myBids+a_bids.get(i);
        			}
        			else 
        			{
        				myBids=myBids+0.0;
        			}
        			
        			if(i<valuations.length-1)
        				myBids= myBids+" ";
        		}
        			
        		///////////////////////////////////////////////
        		
        		out.println(myBids); //Send agent's bids to server
        		System.out.println("Mybids: " + myBids + "\n");
        	}
        	
        	//***********************************************************************
        	
        	//Observe the state of auction variables. Store information locally
        	else if( fromServer.startsWith("observe-auction-state:") ){
        		String[] stateVars = fromServer.split("[ ]");//tokens delimited with spaces
      
        		int numAgents = Integer.valueOf(stateVars[1]);
        		int numTimeSlots = Integer.valueOf(stateVars[2]);
        		int currentRound = Integer.valueOf(stateVars[3]);//1st round -> 0
        		//currentRound is 0-indexed, so currentRound = 0 for first round
        		

        		String[] leaderIDs = new String[numTimeSlots];//leaders and their bids for each time-slot
        		double[] leaderBids = new double[numTimeSlots];
        		
        		
        		if(currentRound > 0)//no winners before first round
        			for(int i=0; i<(numTimeSlots*2); i+=2){ //2 records per time slot [leader, bid]
        				leaderIDs[i/2] = stateVars[4+i];
        				leaderBids[i/2] = Double.valueOf(stateVars[5+i]);
        			}
        		
        		
        		System.out.println("Observing state:\nNumber of agents = " + numAgents +
        						   "\nNumber of time slots = " + numTimeSlots);
        		System.out.println("Current auction state:");
        		for(int i=0; i < leaderIDs.length; i++)
        			System.out.println("Time-Slot " + i + ": current-leader: " + leaderIDs[i] +
        								", current bid = " + leaderBids[i]);
        		///////////////////////////////////////////////
                //  YOUR CODE HERE
                //  You may want to record some of the state
        		//  information her, especially the IDs of
        		//  current auction leaders for each time-slot
        		//  as well as the current leading bids.
                ///////////////////////////////////////////////
        		
        
        		
        		if (currentRound>0)
        		{
        			for (int i =0;i<numTimeSlots;i++)
	        		{
		        		boolean is_winner=false;
		        		if (leaderIDs[i].equals(agent_name))
		        		{
		        			is_winner=true;
		        		}
		        		
		        		int  ask_epsilon=1;
		        		double ask_price=leaderBids[i]+ask_epsilon;
		        		
		        		agent.postResult(new Result(auctions.get(i), is_winner, 0, ask_price, 0, ask_epsilon));
	        		}
        		}
        		
        		///////////////////////////////////////////////
        		out.println( "State Observed" );//let the server know client received state info
        		System.out.println("");//helps separate the output
        	}
        	
        	//***********************************************************************
        	
        	else if( fromServer.startsWith("observe-final-outcome:") ){
        		//fromServer = "observe-final-outcome: winnerID_0 price_0 winnerID_1 price_1 ..."
        		String[] outcomeVars = fromServer.split("[ ]");//tokens delimited with spaces
      
        		//for each slot, announce winner's ID and their price
        		for(int i=1; i < outcomeVars.length; i+=2){
        			String winnerID = outcomeVars[i];
        			double winPrice = Double.valueOf(outcomeVars[i+1]);
        			System.out.println("Time Slot " + ((i+1)/2) + " awarded to [" + 
        								winnerID + "] for price = " + winPrice);
        		}
        		out.println( "Final Outcome Observed" );//let the server know client received state info
        	}
        	
        	//***********************************************************************
        	
        	//The server says to end the connection
        	else if (fromServer.equals("END")){
        		System.out.println("END called.  closing");
                break;
        	}
        	
        	//***********************************************************************
        	
        	else
        		System.out.println("Unexpected input: " + fromServer);
        }
        
        out.close();
        in.close();
        stdIn.close();
        auctSocket.close();
    }
}