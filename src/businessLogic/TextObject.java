package businessLogic;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;

import javax.crypto.spec.SecretKeySpec;

/**
 * @author fritz
 * Transfer Object that contains the text and
 * the encryption parameters.
 * Gets passed between the different Layers/Classes
 */
public class TextObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public String name = "nonAme.txt";
	public byte text[];
	public String encryption; // e.g. "AES"
	public boolean encrypted;
	public Key symmetricKey;
	public int keyLength;

	public String padding;

	public String blockCipherMode;

	public Key customKey;

	public String password;

	public String pbeMode;

	public String digest;

	public MessageDigest hash;

	public KeyPair keyPair;
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setCustomKey(byte[] key)
	{
		
		
		customKey = new SecretKeySpec(key, encryption);
		
	}


	
	
	
	
	
	

}
