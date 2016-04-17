package Utilities;

public class CircularBuffer {
	private Float readings[];
	private int head;
	private int tail;
	
	private final int size = 25;
	
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
	
}
