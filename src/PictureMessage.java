import java.awt.image.BufferedImage;


public class PictureMessage extends Message{
	
	private BufferedImage image;
	
	public PictureMessage(String message, BaseConfig config) {
		super(message, config, MessageType.READING);
		
		//
	}
	public BufferedImage getImage() {
		return image;
	}
}
