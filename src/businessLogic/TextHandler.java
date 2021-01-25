/**
 * 
 */
package businessLogic;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;

import data.Storage;

/**
 * The TextHandler is the mediator between presentation layer (Interface),
 * business logic (Encryption) and data layer (Storage).
 * It is called by the Interface for saving and loading and takes
 * appropriate action, like asking Encryption to encrypt the text and
 * then handing it over to Storage for persisting it.
 * @author fritz
 */
public class TextHandler {
	
	//The path that was used last time a file was saved.
	public static String lastPath = "/home/fritz/";
	//The path + filename that was used last time a file was saved.
	public static String lastFilePath;

	
	public TextHandler()
	{

	}
	

	
	
	/**
	 * Encrypts a given text object if applicable,
	 * then saves it to the specified location.
	 * @param path The folder in the filesystem where the file should be saved.
	 * @param filename The filename, including file extension, without the path.
	 * @param to The TextObject.
	 */
	public static void saveAs(String path, String filename, TextObject to)
	{
		System.out.println("Using specified file path: "+ path + filename);
		lastFilePath = path+filename;
		lastPath = path;
		
		//Try encrypting with parameters specified in the TextObject.
		try {
			Encryption.encrypt(to);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("encryption failed");
		}
		System.out.println("Text after encryption: "+new String(to.text));
		
		//Symmetric keys get saved in a separate file and get deleted from the textObject parameters
		if(to.symmetricKey!=null)
		{
			Key keyObject = to.symmetricKey;
			to.symmetricKey = null;
			Storage.saveObject(path+filename+".key", keyObject);
		}
		
		//Key pairs are saved in two separate files and get deleted from the textObject parameters
		if(to.keyPair!=null)
		{
			Key publicKeyObject = to.keyPair.getPublic();
			Key privateKeyObject = to.keyPair.getPrivate();
			to.keyPair = null;
			Storage.saveObject(path+filename+".privatekey", privateKeyObject);
			Storage.saveObject(path+filename+".publickey", publicKeyObject);
		}
		
		//save the textObject in an XML file (text and parameters, but no keys)
		Storage.saveAsXML(path+filename, to);
		saveDefaultValues();
		
		
		
	}
	
	
	
	
	/**
	 * Loads a textObject from file.
	 * @param filePath path of an XML file that contains a TextObject.
	 * @return the TextObject (in TextObject format).
	 */
	public static TextObject load(String filePath)
	{
		TextObject to = new TextObject();
		to = (TextObject) Storage.loadXML(filePath);
		System.out.println("to.tostring = " + new String(to.toString()));
	
		return to;
	}
	
	/**
	 * Loads a key from file.
	 * @param filePath path of a serialized Key.
	 * @return the deserialized Key.
	 */
	public static Key loadKey(String filePath)
	{
		Key keyObject;
		keyObject = (Key) Storage.loadObject(filePath);
		System.out.println("keyObject.getEncoded() = " + new String(keyObject.getEncoded()));
		
		
		return keyObject;
	}
	
	
	/**
	 * Calls the Encryption class for decryption.
	 * @param to The TextObject.
	 */
	public static void decrypt(TextObject to)
	{
		try {
			Encryption.decrypt(to);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Saves the path that was most recently used for saving a file as a default value.
	 * This method should be extended to save all parameters.
	 */
	public static void saveDefaultValues()
	{
		/*DefaultValues dv = new DefaultValues();
		
		dv.lastPath = lastPath;*/
		
		String dv = lastPath;
		
		Storage.saveAsXML("/home/fritz/KryptPadDefaultValues.dv", dv);
	}
	
	public static void loadDefaultValues()
	{
		/*DefaultValues dv = new DefaultValues();
		dv = (DefaultValues) Storage.load("/home/fritz/KryptPadDefaultValues.dv");
		System.out.println("default values not loaded. Pls implement."); */
		
		String dv = (String) Storage.loadXML("/home/fritz/KryptPadDefaultValues.dv");
		
		if(dv!=null)
		{
			//lastPath = dv.lastPath;
			lastPath = dv;
		}
	}
	
	static class DefaultValues implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected String lastPath;
	}

}
