package UnitTests;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import businessLogic.Encryption;
import businessLogic.TextObject;
import businessLogic.Utils;

public class EncryptionTest {

	@Test
	public void knownAnswerTestForAES() {
		TextObject textObject = new TextObject();
		try {
			textObject.text = "4e62557798dbef562e8653104f3834ea".getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		textObject.encryption = "AES";
		textObject.blockCipherMode = "ECB";
		textObject.padding = "NoPadding";
		try {
			textObject.setCustomKey("809f5f873c1fd761c02faffec989d1fc".getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Custom Key Algorithm = " + textObject.customKey.getAlgorithm());
		System.out.println("Custom Key Bytes = " + textObject.customKey.getEncoded());
		System.out.println("Custom Key Format = " + textObject.customKey.getFormat());
		try {
			Encryption.encrypt(textObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Encrypted: " + textObject.encrypted);
		assertEquals("95a8d72813daa94d0eec1487dd8c26d5", Utils.toHex(textObject.text));
	}
	
	@Test
	public void simpleAEStest()
	{
		TextObject textObject = new TextObject();
		textObject.text = "4e62557798dbef562e8653104f3834ea".getBytes();
		textObject.encryption = "AES";
		textObject.blockCipherMode = "ECB";
		textObject.padding = "NoPadding";
		textObject.keyLength = 192;

		try {
			Encryption.encrypt(textObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(new String(textObject.text).equals("4e62557798dbef562e8653104f3834ea"))
			fail("Ciphertext is the same as plaintext!");
		
		try {
			Encryption.decrypt(textObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Encrypted: " + textObject.encrypted);
		assertEquals("4e62557798dbef562e8653104f3834ea", new String(textObject.text));
	}
	
	@Test
	public void simpleEncryptionTest(String encryption, String blockCipherMode, String padding, int keyLength)
	{
		TextObject textObject = new TextObject();
		textObject.text = "4e62557798dbef562e8653104f3834ea".getBytes();
		textObject.encryption = encryption;
		textObject.blockCipherMode = blockCipherMode;
		textObject.padding = padding;
		textObject.keyLength = keyLength;

		try {
			Encryption.encrypt(textObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(new String(textObject.text).equals("4e62557798dbef562e8653104f3834ea"))
			fail("Ciphertext is the same as plaintext!");
		
		try {
			Encryption.decrypt(textObject);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Encrypted: " + textObject.encrypted);
		assertEquals("4e62557798dbef562e8653104f3834ea", new String(textObject.text));
	}

}
