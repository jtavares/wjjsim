
import java.util.*;


public class Auction {

	//each index tracks an agent by unique ID and their bid
	Vector<String> bidderIDs = new Vector<String>();
	Vector<double[]> bids = new Vector<double[]>();
	Vector<double[]> paid = new Vector<double[]>();//what an agent pays may differ from bid 
	
	Vector<String> clientStates = new Vector<String>();//stores state of each client
	
	int numClients;//number of clients that we expect
	int numSlots = 5;
	
	volatile boolean started = false;
	boolean bidActivity = false;//used in simultaneous ascending auctions to track whether active bids were made
	public int round = 0;//tracks the current round of the auction if sequential
	
	
	
	public Auction(){
		numSlots = 5;//default
	}
	
	public Auction(int i){
		numSlots = i;
	}
	
	public Auction(int slots, int clients){
		numSlots = slots;
		numClients = clients;
	}
	
	
	public void startAuction(){
		this.started = true;
	}
	
	public void printMe(){
		System.out.println("Number of slots is " + this.numSlots);
	}
	
	//return true if bids have been received for all agents in specified round
	public boolean isRoundOver(int round){
		for(int i=0; i < bids.size(); i++)
			if(bids.get(i)[round] < 0 ) //-1 indicated that agent[i] hasn't bid yet
				return false;
		return true;
	}
	
	//return true only if ALL bids for all agent-slot combinations are >= 0
	public boolean allBidsReceived(){
		for(int i=0; i < bids.size(); i++)
			for(int j=0; j < bids.get(i).length; j++)
				if( bids.get(i)[j] < 0)
					return false;
		return true;
	}
	
	//return true if all clientStates are equal to param string
	public boolean allclientStatesAre(String s){
		for(int i=0; i < clientStates.size(); i++)
			if( !s.equals(clientStates.get(i)) )
				return false;
		return true;
	}
	
	
	//return true if all clientStates are equal to either param string
	public boolean allclientStatesAre(String s1, String s2){
		for(int i=0; i < clientStates.size(); i++)
			if( !s1.equals(clientStates.get(i)) &&  !s2.equals(clientStates.get(i)))
				return false;
		return true;
	}
	
	
	//true when conditions for ending the auction have occurred
	public Boolean isOver(){
		if(bids.size() < 1)
			return false;//can't have started yet...
		for(int i=0; i < bids.size(); i++)
			for(int j=0; j < bids.get(i).length; j++)
				if( bids.get(i)[j] < 0)
					return false;//someone didn't bid on one of the auctions... just bid 0
		return true;
	}

	
	//return True if the ID is already present in bidderIDs
	public boolean idInUse(String id){
		return bidderIDs.contains(id);
	}
	
	
	//return True if added bidder is successfully (no duplicates)
	public boolean addBidder(String id){
		if(id == null || bidderIDs.contains(id))
			return false;
		
		//IDs must meet some criteria
		if(id.length() < 2)
			return false;
		
		bidderIDs.add(id);
		double[] b = new double[numSlots];
		double[] p = new double[numSlots];
		for(int i=0; i < b.length; i++){
			b[i] = -1;//-1 marks that a bid has not been made
			p[i] = -1;
		}
		bids.add( b ); //an invalid bid means bid hasn't been made yet
		paid.add( p );
		
		clientStates.add( new String("ready") );//make sure this string is NOT a number!
		
		//double check that the id is not a duplicate
		int index1 = bidderIDs.indexOf(id);
		if(index1 == bidderIDs.size()-1 )
			return true;
		
		int index2 = bidderIDs.indexOf(id, index1+1);//first occurrence after index
		if(index2 != -1){ //duplicate found
			bidderIDs.remove(index2);
			bids.remove(index2);
			clientStates.remove(index2);
			paid.remove(index2);
			return false;
		}
		return true;
	}
	
	
	public void setClientState(String clientID, String state){
		int myIndex = bidderIDs.indexOf(clientID);
		clientStates.set(myIndex, state);//
	}
	
}
