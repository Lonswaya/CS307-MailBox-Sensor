package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cs307.purdue.edu.autoawareapp.ClientConfig;

public class IO {

	static void writeObjectToFile(String path, Object obj) {
		try {
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(obj);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static <T> T readObjectFromFile(String path) {
		T obj = null;
		try {
			FileInputStream fin = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fin);
			obj = (T)ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static void main(String[] args)
	{
		ClientConfig conf = new ClientConfig();
		
		String path = System.getProperty("user.home");
		String path1 = (path + File.separator + "file.bin");
		
		IO.writeObjectToFile(path1, conf);
		
		ClientConfig newConfig = IO.<ClientConfig>readObjectFromFile(path1);
		
		System.out.println("Shit");
	}
}

