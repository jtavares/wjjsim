import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class GenerateSAASchedPricePredictions extends Thread {	
	// Setup price predictor options
	int no_agents = 8;
	int no_auctions = 5;
	int nth_price = 1;
	double ask_price = 1;
	double ask_epsilon = 1;
	int no_per_iteration = 5000;	// no. of auctions to run in each iteration. the more, the better the histogram
	int max_iterations = 10;
	int avg_iterations = 2;
	double ks_threshold = 0.10;
	double precision = 1.0;

	int no_to_generate = 10; // no of predictions to generate per thread

	// thread id 
	int thread_id;

	public GenerateSAASchedPricePredictions(int thread_id) {
		this.thread_id = thread_id;
	}
	
	public void run() {
		for (int i = 0; i<no_to_generate; i++) {
			int no = no_to_generate*thread_id + i;
			
			System.out.println("Price Prediction " + i + "/" + (no_to_generate-1) + " by thread id " + thread_id);
			
			PricePredictorSAASched pp = new PricePredictorSAASched(no_agents, no_auctions, nth_price, ask_price, ask_epsilon,				
					no_per_iteration, max_iterations, avg_iterations, ks_threshold, precision);
		
			ArrayList<DiscreteDistribution> pp_data = pp.predict();

			try {
				FileOutputStream fos = new FileOutputStream("saa_pp/" + no + ".obj");
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(pp_data);
				oos.flush();
				oos.close();
				
				/* DEBUGGING FileInputStream fis = new FileInputStream("saa_pp/" + no + ".obj");
				ObjectInputStream ois = new ObjectInputStream(fis);
				try {
					@SuppressWarnings("unchecked")
					ArrayList<DiscreteDistribution> pp2 = (ArrayList<DiscreteDistribution>) ois.readObject();
					
					System.out.println("pp2 size: " + pp2.size());
					
					//for (int j = 0; j<pp2.size(); j++)
					
					System.out.println("++++++++++++++++++++++pp_data:");
					pp_data.get(4).print(0);
					
					System.out.println("++++++++++++++++++++++pp2:");
					pp2.get(4).print(0);
					
			
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				System.out.println("Thread ID " + thread_id + " wrote price prediction " + no + " to disk.");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			
	}
	
	public static void main(String[] args) {
		int no_threads = 5;
		
		GenerateSAASchedPricePredictions t[] = new GenerateSAASchedPricePredictions[no_threads];
		
		for (int i = 0; i<no_threads; i++) {
			t[i] = new GenerateSAASchedPricePredictions(i);
			t[i].start();
		}
		
		for (int i = 0; i<no_threads; i++) {
			try {
				t[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("----ALL DONE----");
	}

}
