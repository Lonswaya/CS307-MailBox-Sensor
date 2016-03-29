import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import javax.imageio.ImageIO;

public abstract class BaseSensor{
	
	protected BaseConfig config;
	protected SensorType sType;
	protected float upperBound; //some meaningful boundary values used to calculate percentage
	protected float lowerBound;
	protected BlockingQueue<byte[]> byteQueue;
	protected Thread sendFromQueue;
	
	public BaseSensor(BaseConfig config){
		this.config = config;
		this.sType = config.sensor_type;
		
		switch(this.sType){
			case LIGHT:{
				upperBound = 255;
				lowerBound = 0;
				break;
			}
			default:{
				upperBound = 100;
				lowerBound = 0;
				break;
			}
		}
	}
	
	public void setConfig(BaseConfig config) {
		this.config = config;
	}
	
	/*public void receive_message(){
		Useless now that we have receive thread
	}*/
	
	// this function determins if sensor should be active given the time
	// restriction in config
	public boolean isSensorActive() {
		if (this.config == null) return false;
		return this.config.isSensorActive();
		/*if (hour == startH)
			if (minute >= startM)
				return true;
		if (hour == stopH)
			if (minute < stopM)
				return true;*/
		/*  (hour == startH && minute >= startM) return true;
		  (hour == stopH && minute < stopM) ||
		  (startH > stopH && (hour > startH || hour < stopH)) ||
		  hour > startH && hour < stopH) {
			return true;
		}*/
	}
	public String getIP(){
		String address = "";
		try{
			NetworkInterface ni = NetworkInterface.getByName("eth0");
	        Enumeration<InetAddress> inetAddresses =  ni.getInetAddresses();
	        
	        while(inetAddresses.hasMoreElements()) {
	            InetAddress ia = inetAddresses.nextElement();
	            if(!ia.isLinkLocalAddress()) {
	                address = ia.getHostAddress();
	            }
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		return address;
	}
	
	public String toString() {
		return "Sensor Type set to: " + this.sType;
	}
	
	public abstract void sense();
	//public abstract void streamData(boolean streaming);
	public abstract boolean check_threshold();
	public abstract Message form_message();
	public abstract void close();
	
	//use this to compress a byte array 
	//return: a compressed byte array
	public static byte[] compressByteArray(byte[] array) throws IOException{
		System.out.println("Debug: Starting compression");
		long tempTime = System.currentTimeMillis();
		
		byte[] buffer = new byte[array.length];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Deflater d = new Deflater(Deflater.BEST_COMPRESSION);
		d.setInput(array);
		d.finish();
		while(!d.finished()){
			int size = d.deflate(buffer);
			out.write(buffer, 0, size);
		}
		d.end();
		byte[] compressed = out.toByteArray();
		out.close();
		
		System.out.println("Debug: End compression, Time:" + (int) (System.currentTimeMillis() - tempTime) + "\n");
		return compressed;
	}
	
	//use this to decompress a compressed byte array
	//uncompressedLength could potentionally be parsed from MessageObject.message
	//so pass in that detail when sending a message
	//return: decompressed byte array
	public static byte[] uncompressByteArray(byte[] array, int uncompressedLength) throws DataFormatException{
		System.out.println("Debug: Starting decompression");
		long tempTime = System.currentTimeMillis();
		
		Inflater i = new Inflater();
		i.setInput(array, 0, array.length);
		byte[] decompressed = new byte[uncompressedLength];
		i.inflate(decompressed);
		i.end();
	
		System.out.println("Debug: End decompression, Time:" + (int) (System.currentTimeMillis() - tempTime) + "\n");
		return decompressed;
	}
	
	public static void main(String[] args) throws IOException, DataFormatException{
		//test compressions
		
		String a = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int n = a.length();
		Random r = new Random();
		//String str = "ssdadadqwdwoidoiiiiiqjowidjissssssssssenwenwnwenknkeklfekkelfnkleklekekkkkekekeknfqwlefnwenflweklfnwelnfsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssswnlfnwelfknwlkfneflnlkqwenflnelknlkengl24oi243trjo2iu5285723874274823498293hrrenfdlkfsdfsdfsdgweg";
		String str = "";
		
		for(int i = 0; i < 9999; i++){
			str += a.charAt(r.nextInt(n));
		}
		byte[] ba = str.getBytes(StandardCharsets.UTF_8);
		
		/*  
		//This picture was compressed from 2391976 to 2386050 (-5926, 0.2% of original image) in 100 milliseconds
		
		BufferedImage bi = ImageIO.read(new File("C:/Users/Zixuan/Desktop/CS307/winter.png"));
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ImageIO.write(bi, "png", bao);
		byte[] ba = bao.toByteArray();*/
		
		System.out.println("Before compress len: " + ba.length);
		int len = ba.length;
		byte[] ba2 = BaseSensor.compressByteArray(ba);
		System.out.println("After compress len: " + ba2.length);
		ba = BaseSensor.uncompressByteArray(ba2, len);
		
		//System.out.println("Original String: \t" + str + "\nAfter String: \t\t" + newStr + "\nMatch: " + newStr.equals(str));
	}
}