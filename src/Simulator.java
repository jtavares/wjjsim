
public class Simulator {
	public static void main(String args[]) {
		int nth_price = 2;
		int no_auctions = 2; // a.k.a. no of items
		int no_agents = 5;
		
		SSBNPSimulation sim = new SSBNPSimulation(nth_price, no_auctions, no_agents);
		sim.play();
		sim.report();
	}
}