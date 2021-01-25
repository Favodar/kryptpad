package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;

import businessLogic.TextObject;

public class Storage {
	
	/**
	 * Writes any Object to a file at the specified location with the
	 * specified name by using an output stream.
	 * @param filepath Desired path + filename ( + file extension).
	 * @param object Object that shall be persisted.
	 * @return Returns true if the process was successful, otherwise returns false.
	 */
	public static boolean saveObject(String filepath, Object object)
	{
		try {
			
			
					
			FileOutputStream f = new FileOutputStream(new File(filepath));
			ObjectOutputStream o = new ObjectOutputStream(f);

			// Write objects to file
			o.writeObject(object);

			o.close();
			f.close();
			return true;

		

		} catch (FileNotFoundException e) {
			System.out.println("File not found" + filepath);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error initializing output stream");
			e.printStackTrace();
		} 
		return false;
	}
	
	/**
	 * Reads any Object from a file at the specified location with the
	 * specified name by using an input stream.
	 * File must be a serialized object.
	 * @param filepath path + filename ( + file extension).
	 * @return An object (if the file contains one).
	 */
	public static Object loadObject(String filepath)
	{
		Object o = null;
		try {
			System.out.println("Loading File: " + new File(filepath).getAbsolutePath());
			

			FileInputStream fi = new FileInputStream(new File(filepath));
			ObjectInputStream oi = new ObjectInputStream(fi);

			// Read objects
			o = oi.readObject();
			

			

			oi.close();
			fi.close();
			return o;

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + filepath);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error initializing input stream");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return o;
	}
	
	/**
	 * Writes any Object to an XML file at the specified location with the
	 * specified name by using an output stream.
	 * @param filepath Desired path + filename ( + file extension).
	 * @param f Object that shall be persisted.
	 * @return Returns true if the process was successful, otherwise returns false.
	 */
	public static boolean saveAsXML(String filepath, Object f) {
        try {
        	System.out.println("Saving File: " + new File(filepath).getAbsolutePath());
			XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(
					new FileOutputStream(new File(filepath))));
			encoder.writeObject(f);
			encoder.close();
			return true; //should return false if not successful
		} catch (Exception e) {
			System.out.println("Saving as XML not successful");
		}
        return false;
    }

    public static Object loadXML(String filepath) {
        try {
        	System.out.println("Loading File: " + new File(filepath).getAbsolutePath());
			XMLDecoder decoder =
			    new XMLDecoder(new BufferedInputStream(
			        new FileInputStream(new File(filepath))));
			Object o = (Object)decoder.readObject();
			decoder.close();
			return o;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

}
