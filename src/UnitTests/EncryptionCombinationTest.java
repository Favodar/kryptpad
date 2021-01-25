package UnitTests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import businessLogic.Encryption;
import businessLogic.TextObject;

@RunWith(Theories.class)
public class EncryptionCombinationTest {
	
	@DataPoints("encryption")
	public static String[] encryptionValues()
	{
		return new String[]{"AES", "DES"};
	}
	
	@DataPoints("blockCipherMode")
	public static String[] bcmValues()
	{
		return new String[]{"ECB", "CBC", "CTS", "CTR", "OFB", "CFB"};
	}
	
	@DataPoints("padding")
	public static String[] paddingValues()
	{
		return new String[]{"NoPadding", "PKCS7Padding", "ZeroBytePadding"};
	}
	
	@DataPoints("keyLength")
	public static int[] keyLengthValues()
	{
		return new int[]{128, 192, 256};
	}

	@Theory
	public void simpleEncryptionTest(@FromDataPoints("encryption") String encryption,@FromDataPoints("blockCipherMode") String blockCipherMode,@FromDataPoints("padding") String padding,@FromDataPoints("keyLength") int keyLength)
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
