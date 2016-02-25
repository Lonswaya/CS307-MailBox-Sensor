import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LightSensor extends BaseSensor {

	private float light_intensity;
	private BufferedImage image;
	
	public LightSensor(BaseConfig config){
		super(config);
	}
	
	@Override
	public void sense() {
		take_picture();
		this.light_intensity = get_reading(this.image);
	}
	
	public Message form_message(){
		return new Message("Above threshold", null);
	}

	public void set_webcam_state(boolean state){
		
	}
	
	public void take_picture(){
		//some method via api to take picture
		this.image = null;
	}
	
	//turn image into greyscale
	public BufferedImage greyscale_picture(BufferedImage image){
		//BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Color c;
		int avg;
		
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++){
				c = new Color(image.getRGB(x, y));
				avg = (c.getBlue() + c.getGreen() + c.getRed()) / 3;
				//System.out.println(avg);
				c = new Color(avg, avg, avg);
				image.setRGB(x, y, c.getRGB());
			}
		}
		return image;
	}
	
	public float get_reading(BufferedImage image){
		Color color = new Color(image.getRGB(image.getWidth() / 2, image.getHeight() / 2));
		//System.out.println("R: " + color.getRed() + " G: " + color.getGreen() + " B: " + color.getBlue());
		return color.getBlue() / (this.upperBound - this.lowerBound);
	}

	@Override
	public boolean check_threshold() {
		if(this.light_intensity > config.sensing_threshold)
			return true;
		return false;
	}
	
	/*public static void main(String[] args){ 
		LightSensor s = new LightSensor(new BaseConfig("9:25", "22:10", true, SensorType.LIGHT, 0));
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("C:/Users/Zixuan/Downloads/pic/IMG_1825.JPG"));
			img = s.greyscale_picture(img);
			//File out = new File("C:/Users/Zixuan/Downloads/pic/test.JPG");
			//ImageIO.write(img, "JPG", out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(s.get_reading(img));
		
		
	}*/
}
