/*
 * Author: TJ Goff
 * 
 * This is a server for a simultaneous series of ascending auctions.
 * Clients connect to the server through SimultaneousAscentingAuctionServerThread
 * objects, which communicate with the clients and update the state of the auction 
 * based on actions taken by the competing agents trying to win the auctions.
 * 
 * The auction itself is instantiated here, but most of the mechanics of running
 * the auction are handled in the SimultaneousAscentingAuctionServerThread
 * class.
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


public class SimultaneousAscendingAuctionServer {
	
    public static void main(String[] args) throws IOException {
    	
    	System.out.println("Auction Server starting");
    	try {  // Get hostname by textual representation of IP address
    	    InetAddress addr = InetAddress.getLocalHost();//= InetAddress.getByName("127.0.0.1");
    	    String hostname = addr.getHostName(); // Get the host name
    	    System.out.println("ip address = "+addr.toString()+"\nHost name = "+hostname);
    	} 
    	catch (UnknownHostException e){
    	}
    	
    	
    	//used to let host user control flow of program
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	System.out.println("How many players are needed?");
        int numPlayersNeeded = Integer.valueOf(br.readLine());
    	
        
    	//make an auction with which clients interact
    	Auction myAuction = new Auction(5, numPlayersNeeded);//auction with 5 slots
    	
    	
    	//SELECT TYPE OF AUCTION
    	Vector<SimultaneousAscendingAuctionServerThread> myThreads = new Vector<SimultaneousAscendingAuctionServerThread>();

    	
        ServerSocket serverSocket = null;
        int portNum = 7; //the luckiest port there is!
        //Try reading in the IP and socket number from the text file...
        try {
            BufferedReader in = new BufferedReader(new FileReader("./src/IP_and_Port.txt"));
            //two lines in this file.  First is hostName/IP address, and second is socket number of host 
            String hName = in.readLine();//use the hostname found above and skip this one
            portNum = Integer.valueOf( in.readLine() );	
            in.close();
        } catch (IOException e) {
        }
     
        try {
            serverSocket = new ServerSocket(portNum);
        } 
        catch (IOException e) {
            System.err.println("AuctionServer could not listen on port: "+portNum+".");
            System.exit(-1);
        }
        
        
        System.out.println("Clients should join the auction now...");
        boolean started = false;
        boolean firstThread = true;//used to designate first thread as "reporter"
        while(!started){ //(listening && !myAuction.isOver() ){
        	
        	SimultaneousAscendingAuctionServerThread aThread = 
        		new SimultaneousAscendingAuctionServerThread( serverSocket.accept(), myAuction, numPlayersNeeded);

        	
        	//set first thread as the "reporter", so only one copy of info printed to cmd prompt
        	aThread.start();
        	if(firstThread){
        		aThread.setReporter(true);
        		firstThread = false;
        	}
        		
        	myThreads.add( aThread );
        	
        	if(myThreads.size() >= numPlayersNeeded){
        		started = true;
        		System.out.println("Auction should be ready to go...");
        	}
        	else
        		System.out.println("need more players.  Thread-count = " + myThreads.size() +
        				"  , idCount = " + myAuction.bidderIDs.size());
        	//myAuction was passed as a shallow copy... so only one auction exists
        }
        

        
        
        System.out.println("\nAuction has started with " + myThreads.size() + " agents!\n");
        
        //The auction is running right now... can you feel it?
        
        System.out.println("Press Enter to end host connections and close...");
        br.readLine();

        System.out.println("Auction is complete... closing connections");
        for(int i=0; i < myThreads.size(); i++)
        	if( myThreads.get(i).isAlive() )
        		myThreads.get(i).closeConnection();
        
        serverSocket.close();
    }//end main()
    
    
}//end class