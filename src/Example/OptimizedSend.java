package Example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public class OptimizedSend {
	
	static BlockingQueue<Integer> q = new LinkedBlockingQueue<Integer>(5); //only 5 spaces in queue just to demenstrate 
																	//blocking queue since we are sending 10 numbers
	static AtomicInteger counter = new AtomicInteger(0);				    
	static AtomicInteger number = new AtomicInteger();
	static boolean stop = false;
	
	static class Taker extends Thread{
		public static void p(){
			int a = 0;
			try {
				Thread.sleep(1000);
				a = q.take();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Dequeued: " + a);
		}
		public void run(){
			while(true){ //some condition to stop picture sender, perhaps messages?
				if(q.peek() != null) p();
				else{
					if(stop) break;
				}
			}
		}
	}
	
	//a process uses more time
	static class PutterOne extends Thread{
		
		static void p(){
			try {
				Thread.sleep(500);
				q.put(counter.getAndIncrement());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public void run(){
			while(true) {
				if(counter.get() < 10){ //some condition to stop processing methods, perhaps messages?
					p();
					PutterTwo p2 = new PutterTwo();//could use two queues to have secondary methods pipelined too.
					p2.start();
				}else break;
			}
		}
	}
	
	//a process uses less time
	static class PutterTwo extends Thread{
		
		static void p(){
			try {
				Thread.sleep(100);
				number.set(number.get() * 1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void run(){
			p();
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		
		/*
		 * Simulating the actual process within a sensor
		 * 
		 * Taker is similar to send method(): 
		 * constantly taking object from a queue and sending it.
		 * The 1 second delay simulate the time it takes to send (exaggerated time)
		 * 
		 * PutterOne is similar to TakePicture():
		 * It gets the number from counter as if it is taking a picture
		 * I need to use counter because the output needs to be compared
		 * and preferablly properly queued (output from 1 to 20)
		 * 1/2 second delay simulates the time it takes to take a picture
		 * 
		 * PutterTwo is similar to any secondary methods such as greay_scalepicture() or compressbytearray():
		 * in this case it takes the number PutterOne obtained and times 1
		 * 1/10 second delay to simulate time it takes to perform that task
		 */
		System.out.println("Not Threaded:");
		long tempTime = System.currentTimeMillis();
		counter.set(0);
		while(counter.get() < 10){
			PutterOne.p();
			PutterTwo.p();
			Taker.p();
		}
		System.out.println("Time:" + (int) (System.currentTimeMillis() - tempTime));
		
		System.out.println("\nThreaded:");
		
		counter.set(0);
		q.clear();
		
		Taker taker = new Taker();
		PutterOne p1 = new PutterOne();
		tempTime = System.currentTimeMillis();
		p1.start();
		taker.start();
		
		while(true){
			if(!p1.isAlive() && q.peek() == null) stop = true;
			if(!taker.isAlive()){
				System.out.println("Time:" + (int) (System.currentTimeMillis() - tempTime));
				break;
			}
		}
	}
}
