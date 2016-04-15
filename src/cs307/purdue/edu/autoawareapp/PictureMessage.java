package cs307.purdue.edu.autoawareapp;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;


public class PictureMessage extends Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte[] imager; //wtf kind of name is this
	private int width;
	private int height;
	private int imageType;	
	
	public PictureMessage(String message, ClientConfig config) {
		super(message, config, MessageType.PICTURE);
	}
	public BufferedImage getImage() {
		//byte->picture
		//int uncompressedLength = Integer.parseInt(this.message.substring(0, this.message.indexOf(',')));
		try {
			
			//this.imager = BaseSensor.uncompressByteArray(this.imager, uncompressedLength);
			
			//System.out.println("Debug: Starting convert to img: ");
			//long tempTime = System.currentTimeMillis();
			
			BufferedImage image = new BufferedImage(this.width, this.height, this.imageType);
			System.out.println(this.imager.length);
			image.setData(Raster.createRaster(image.getSampleModel(), new DataBufferByte(this.imager, this.imager.length), new Point()));
			//System.out.println("Debug: End Converting to img: " + (int) (System.currentTimeMillis() - tempTime) + "\n");
			return image;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public void setImage(BufferedImage img, boolean already_compressed) {
		//picture->byte
		//System.out.println("Debug: Starting convert to byte: ");
		//long tempTime = System.currentTimeMillis();
			
		this.imager = ((DataBufferByte) img.getData().getDataBuffer()).getData();
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.imageType = img.getType();
       
		/*System.out.println("Debug: End Converting to byte; " + (int) (System.currentTimeMillis() - tempTime) + "\n");
		try{
			this.message = this.imager.length + ", Picture message";
			if(!already_compressed) this.imager = BaseSensor.compressByteArray(this.imager);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println("What the hell");
			e.printStackTrace();
		}*/
		
	}
	
	public static void main(String args[]) throws IOException{
		BufferedImage s = ImageIO.read(new File("C:/Users/Zixuan/Desktop/CS307/1442955066702.jpg"));
		PictureMessage pm = new PictureMessage("", new ClientConfig());
		pm.setImage(s, false);
		s = pm.getImage();
		File f = new File("C:/Users/Zixuan/Desktop/CS307/newpicture.jpg");
		ImageIO.write(s, "jpg", f);
	}
}
