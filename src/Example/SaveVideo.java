package Example;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class SaveVideo {
	
	public static void convert(String path, ArrayList<BufferedImage> list){
		BufferedImage image;
		
		IMediaWriter writer = ToolFactory.makeWriter(path);
		Dimension size = new Dimension(list.get(0).getWidth(), list.get(0).getHeight());
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < list.size(); i++) {

			System.out.println("Capture frame " + i);
			image = list.get(i);
			image = ConverterFactory.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
			IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
			IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
			frame.setKeyFrame(i == 0);
			frame.setQuality(0);
			writer.encodeVideo(0, frame);
			try {
				Thread.sleep(50); //silky smooth
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		writer.close();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {

		
		IMediaWriter writer = ToolFactory.makeWriter("C:/Users/Zixuan/Desktop/CS307/new.mov");
		Dimension size = new Dimension(400, 320);

		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

		long start = System.currentTimeMillis();
		BufferedImage image = ImageIO.read(new File("C:/Users/Zixuan/Desktop/CS307/Big-Bright-light-psd38714.jpg"));
		image = ConverterFactory.convertToType(image, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < 10; i++) {

			System.out.println("Capture frame " + i);
			
			
			IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);

			IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);
			frame.setKeyFrame(i == 0);
			frame.setQuality(0);

			writer.encodeVideo(0, frame);
			Thread.sleep(100);
		}

		writer.close();

	}

}
