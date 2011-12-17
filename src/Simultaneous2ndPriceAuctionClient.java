/*
 * Author: TJ Goff
 * 
 * This class implements a client to participate in an auction on a remote host.
 * The client waits to receive a message from the host, and when a message is
 * received, the client should respond to the host with some information (such
 * as the clients bids in the auction).
 * 
 * When the client starts, it will try to connect to the host at the IP and with
 * the port number specified in the text file IP_and_Port.txt (which should be in
 * the same folder as the client class).  This should allow you to easily change
 * the connection information to reach the host without having to recompile.
 * 
 * You may choose to replace the line (91) which prompts the user for a unique name
 * with the commented line below it, which uses a hard-coded string for the name.
 * 
 * You should enter your code for the following messages from the server:
 * 1: "agent-parameters:"  (initialize your agent with parameters from host)
 * 2: "submit-bid"         (return a string containing bids for slots 1-5 respectively)
 * (remember to separate bids with spaces!)
 * 
 * Remember to use the same formatting for output messages as the example code.
 * 
 * For help, contact: tom_goff@brown.edu
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class Simultaneous2ndPriceAuctionClient {
	
	
    public static void main(String[] args) throws IOException {
    	
    	System.out.println("AuctionClient starting");	
    	
        Socket auctSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        String hostName = "192.168.40.1";//IP of host (may be local IP)
        int socketNum = 7; //the luckiest socket
        //Try reading in the IP and socket number from the text file...
        try {
            in = new BufferedReader(new FileReader("./src/IP_and_Port.txt"));
            //two lines in this file.  First is hostName/IP address, and second is socket number of host 
            hostName = in.readLine();
            socketNum = Integer.valueOf( in.readLine() );	
            in.close();
        } catch (IOException e) {
        }
        System.out.println("HostName = " + hostName + ".  port = " + socketNum);
        
        //These are values the agent should remember and use
        //Values are initialized when Server sends parameters
        int numSlotsNeeded = -1;
        int deadline = -1;
        double[] valuations = null;
        
        
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
                
                out.println( br.readLine() );
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
                //  For example, you could store them in a global variable, 
                //  or use them to initialize an agent class you wrote.
                ///////////////////////////////////////////////
                
                //EDIT HERE!
                
                
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
        		//Note: bids rounded to 2 decimal places by host. 5.031 -> 5.03
                ///////////////////////////////////////////////
        		String myBids = "";
                
        		//EDIT HERE!
        		Random r = new Random();//make random bids...
        		for(int i=0; i < valuations.length; i++){
        			myBids = myBids + (r.nextDouble()*10);//random bid 0 to 10
        			if(i < valuations.length-1)
        				myBids = myBids + " ";//separate bids with a space!  IMPORTANT!
        		}
        		
        		///////////////////////////////////////////////
        		
        		out.println(""+myBids); //Send agent's bids to server
        		System.out.println("My bids: " + myBids);
        	}
        	
        	//***********************************************************************
        	
        	else if( fromServer.startsWith("observe-final-outcome:") ){
        		String[] outcomeVars = fromServer.split("[ ]");//tokens delimited with spaces
      
        		//for each slot, announce winner's ID and their price
        		if( outcomeVars.length-1 < valuations.length*2){
        			out.println( "incomplete state" );//let server know state was incomplete
        		}
        		else{
        			for(int i=1; i < outcomeVars.length; i+=2){
        				//for ith time-slot, winner and price
        				String winnerID = outcomeVars[i];
        				double winPrice = Double.valueOf(outcomeVars[i+1]);
        				System.out.println("Time Slot " + ((i+1)/2) + " awarded to [" + 
        									winnerID + "] for price = " + winPrice);
        			}
        			out.println( "Final Outcome Observed" );//let the server know client received state info
        		}	
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