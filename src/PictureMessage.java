import java.awt.image.BufferedImage;


public class PictureMessage extends Message{
	
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
