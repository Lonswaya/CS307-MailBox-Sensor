import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;


public class PictureMessage extends Message implements Serializable {
	
	private BufferedImage image;
	private byte[] imager;
	
	
	public PictureMessage(String message, ClientConfig config) {
		super(message, config, MessageType.PICTURE);
		
		//
	}
	public BufferedImage getImage() {
		//byte->picture
		ByteArrayInputStream bais = new ByteArrayInputStream(imager);
		try {
			return ImageIO.read(bais);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void setImage(BufferedImage img) {
		//picture->byte
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, "jpg", baos);
			baos.flush();
			this.imager = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("What the hell");
			e.printStackTrace();
		}
		
	}
}
