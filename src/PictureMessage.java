import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;


public class PictureMessage extends Message implements Serializable {
	
	private BufferedImage image;
	private byte[] imager; //wtf kind of name is this
	
	
	public PictureMessage(String message, ClientConfig config) {
		super(message, config, MessageType.PICTURE);
		
		//
	}
	public BufferedImage getImage() {
		//byte->picture
		ByteArrayInputStream bais;
		int uncompressedLength = Integer.parseInt(this.message.substring(0, this.message.indexOf(',')));
		try {
			bais = new ByteArrayInputStream(BaseSensor.uncompressByteArray(this.imager, uncompressedLength));
			return ImageIO.read(bais);
		} catch (Exception e) {
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
			this.imager = BaseSensor.compressByteArray(this.imager);
			this.message = this.imager.length + ", Picture message";
			baos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("What the hell");
			e.printStackTrace();
		}
		
	}
}
