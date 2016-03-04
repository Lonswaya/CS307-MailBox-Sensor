import java.awt.image.BufferedImage;
import java.io.Serializable;


public class PictureMessage extends Message implements Serializable {
	
	private BufferedImage image;
	
	public PictureMessage(String message, BaseConfig config) {
		super(message, config, MessageType.PICTURE);
		
		//
	}
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage img) {
		this.image = img;
	}
}
