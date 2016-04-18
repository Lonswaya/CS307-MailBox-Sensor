package Utilities;
import java.util.Random;

public class CircularBuffer {
	private Float readings[];
	private int head;
	private int tail;
	
	private final int size = 10;
	
	public CircularBuffer() {
		this.readings = new Float[size];
		for (Float reading : this.readings) {
			reading = null;
		}
		this.head = this.tail = 0;
	}
	
	private boolean isFull() {
		return (this.tail + 1 == this.head || 
				(this.tail == (this.size - 1) 
				 && this.head == 0)) ?
		true :
		false;
	}
	
	public boolean insert(float value) {
		if(!this.isFull()) {
			this.readings[this.tail++] = value;
			if (this.tail == this.size)
				this.tail = 0;
			return true;
		} else {
			this.get();
			return this.insert(value);
		}
	}
	
	public Float get() {
		if (this.head != this.tail) {
			Float value = this.readings[this.head++];
			if (this.head == this.size)
				this.head = 0;
			return value;
		} else {
			return null;
		}
	}
	
	public Float average() {
		int count = 0;
		float sum = 0.0f;
		for (Float reading : this.readings) {
			if (reading != null) {
				count++;
				sum += reading;
			} else {
				break;
			}
		}
		return (count == 0) 
				? 0.0f 
				: sum / count;
	}
	
	public static void main(String[] args)
	{
		CircularBuffer cbuf = new CircularBuffer();
		
		cbuf.insert(85);
		cbuf.insert(75);
		cbuf.insert(65);
		cbuf.insert(55);
		cbuf.insert(45);
		cbuf.insert(35);
		cbuf.insert(25);
		cbuf.insert(15);
		cbuf.insert(10);
		cbuf.insert(5);
		
		float avg = (5 + 10 + 15 + 25 + 35 + 45 + 55 + 65 + 75 + 85) / 10.0f;
		System.out.println(avg);
		System.out.println(cbuf.average());
		
		cbuf.insert(10);
		cbuf.insert(0);
		cbuf.insert(20);
		cbuf.insert(30);
		cbuf.insert(40);
		cbuf.insert(50);
		cbuf.insert(60);
		cbuf.insert(70);
		cbuf.insert(80);
		cbuf.insert(90);
		
		avg *= 10;
		avg += (0 + 10 + 20 + 30 + 40 + 50 + 60 + 70 + 80 + 90);
		avg /= (20.0f);
		System.out.println(avg);
		System.out.println(cbuf.average());
		
		cbuf.insert(100);
		cbuf.insert(110);
		cbuf.insert(120);
		cbuf.insert(130);
		cbuf.insert(140);
		cbuf.insert(150);
		cbuf.insert(160);
		cbuf.insert(170);
		cbuf.insert(180);
		cbuf.insert(190);
		
		avg *= 20;
		avg -= (85 + 75 + 65 + 55 + 45);
		avg += (100 + 110 + 120 + 130 + 140 + 150 + 160 + 170 + 180 + 190);
		avg /= 25.0f;
		
		System.out.println(avg);
		System.out.println(cbuf.average());
	}
	
}
