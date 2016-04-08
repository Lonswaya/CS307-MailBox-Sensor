package Example;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.twilio.sdk.TwilioRestException;
//PLEASE DO NOT ACTUALLY RUN THIS. IT SENDS YOU 25 TEXTS.
public class NotifyTest {
	static HashMap<String, Timer> timers = new HashMap<String, Timer>();
	public static void testSend() {
		for (int i = 0; i < 5; i++) {
			Timer t = new Timer(Integer.toString(i));
			TimerTask tt = new TimerTask() { 
				@Override
				public void run() {
//this will only run on a system that has bash
//					try {
//						Sender.send("ilepow@purdue.edu", "This is a test notification");
//					} catch (IOException e){
//					//handle the exception
//					}
					try {
						TwilioSender.send("18004206969", "This is a test notification"); //replace this with an actual phone number if you want to test
					} catch (TwilioRestException tre) {
					//handle it
					}
				}
			};
		
			t.scheduleAtFixedRate(tt, new Date(System.currentTimeMillis() + 1000), 1000); //wait an extra second so the first one happens
			timers.put(Integer.toString(i), t);
		}
	}
	
	public static void clearTimers() {
		for (int i = 0; i < 5; i++) {
			Timer t = timers.get(Integer.toString(i));
			t.cancel();
			timers.remove(Integer.toString(i));
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		testSend();
		TimeUnit.SECONDS.sleep(5);
		clearTimers();
	}
}