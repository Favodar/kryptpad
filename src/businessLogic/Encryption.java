package businessLogic;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;




public class Encryption
{   
	
	
    /**
     * Encrypts the text in a TextObject with any given encryption
     * parameters, as long as they are valid (alone and in combination).
     * Saves keys, hashes etc. inside the TextObject for
     * further use.
     * @param textObject the TextObject that contains the plain text.
     * @return the cipher text as byte array
     * @throws Exception
     */
    public static byte[] encrypt(TextObject textObject) throws Exception
    {
    	
    	//Asymmetric encryption has its own method.
    	if(textObject.encryption.equals("RSA"))
    	{
    		return encryptRSA(textObject);
    	}
//    	if(textObject.encrypted)
//    		return textObject.text;
    	
    	byte[] text = textObject.text;
    	byte[] ivBytes = iv8Bytes;
		if(textObject.encryption.equals("AES")) {
			ivBytes = iv16Bytes; //AES needs 16 Byte long IVs.
		}
		
		/*The following block concatenates the encryption parameters
		 * to a String in the format that BouncyCastle uses.*/
        String encryptionString = textObject.encryption;
        if(textObject.blockCipherMode!=null)
        	encryptionString += "/"+textObject.blockCipherMode;
        if(textObject.padding!=null)
        	encryptionString += "/"+textObject.padding;
        
        
        MessageDigest hash = null;
        if(textObject.digest!=null)
        	hash = MessageDigest.getInstance(textObject.digest, "BC");
        
        //Passing the encryptionString into the Cipher Object, so it knows which parameters it should use.
        Cipher          cipher = Cipher.getInstance(encryptionString, "BC");
        
        KeyGenerator    generator = KeyGenerator.getInstance(textObject.encryption, "BC");

        //Initializing the key generator.
        generator.init(textObject.keyLength);
        
        Key encryptionKey;
        
        //The option for a non-PBE-custom-key exists, but is not implemented in the Interface.
        if(textObject.customKey!=null)
        {
        	encryptionKey = textObject.customKey;
    		System.out.println("using custom key!");
        }
        else if(textObject.password!=null)
        {
        	encryptionKey = generatePasswordBasedKey(textObject.password, textObject.pbeMode);
        	//cipher = Cipher.getInstance(textObject.pbeMode, "BC");
        	System.out.println("using password based key.");
        }
        else
        {
        	encryptionKey = generator.generateKey();
        	System.out.println("using generated key.");
        }
        
        System.out.println("key   : " + Utils.toHex(encryptionKey.getEncoded()));
        
        System.out.println("text : " + Utils.toHex(text));
        
        // encryption pass
        
        if(textObject.blockCipherMode.equals("ECB")||textObject.encryption.equals("ARC4"))
        	cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        else
        	cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new IvParameterSpec(ivBytes));
        
        byte[] cipherText;
        if(textObject.digest!=null)
        	cipherText = new byte[cipher.getOutputSize(text.length + hash.getDigestLength())];
        else
        	cipherText = new byte[cipher.getOutputSize(text.length)];
        
        int ctLength;
        if(textObject.digest!=null)
        {
        	ctLength = cipher.update(text, 0, text.length, cipherText, 0);
        	hash.update(text);
        	ctLength += cipher.doFinal(hash.digest(), 0, hash.getDigestLength(), cipherText, ctLength);
        }
        else
        {
        	ctLength = cipher.update(text, 0, text.length, cipherText, 0);
        	ctLength += cipher.doFinal(cipherText, ctLength);
        }
        
        if(textObject.password==null)
        	textObject.symmetricKey = encryptionKey;
        
        textObject.encrypted = true;
        textObject.text = cipherText;
        textObject.hash = hash;
        return cipherText;
    }
    
    /**
     * Generates public and private RSA keys, encrypts the text object with
     * the public key and saves the key pair inside the text object.
     * @param textObject
     * @return The cipher text.
     * @throws Exception
     */
    public static byte[] encryptRSA(TextObject textObject) throws Exception
    {
        Cipher	         cipher = Cipher.getInstance("RSA/NONE/NoPadding", "BC");
        SecureRandom     random = Utils.createFixedRandom();
        
        // create the keys
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        
        generator.initialize(textObject.keyLength, random);

        KeyPair          pair = generator.generateKeyPair();
        Key              pubKey = pair.getPublic();
        Key              privKey = pair.getPrivate();

        
        
        // encryption step
        
        cipher.init(Cipher.ENCRYPT_MODE, pubKey, random);

        byte[] cipherText = cipher.doFinal(textObject.text);

        System.out.println("cipher: " + Utils.toHex(cipherText));
        
        textObject.keyPair = pair;
        
        textObject.text = cipherText;
        
        return cipherText;
    
    }
    
    public static byte[] decryptRSA(TextObject textObject) throws Exception
    {

        Cipher	         cipher = Cipher.getInstance("RSA/NONE/NoPadding", "BC");

        Key              privKey = textObject.symmetricKey;
        
  
        
        // decryption step

        cipher.init(Cipher.DECRYPT_MODE, privKey);

        byte[] plainText = cipher.doFinal(textObject.text);
        
        System.out.println("plain : " + Utils.toHex(plainText));
        
        textObject.text = plainText;
        
        return plainText;
    
    }
    
    
    
    private static Key generatePasswordBasedKey(String password, String pbeMode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, NoSuchProviderException {
    	char[]              passwordChar = password.toCharArray();
        byte[]              salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte)0xe9, (byte)0xe0, (byte)0xae };
        int                 iterationCount = 2048;
        PBEKeySpec          pbeSpec = new PBEKeySpec(passwordChar, salt, iterationCount);
        SecretKeyFactory    keyFact = SecretKeyFactory.getInstance(pbeMode, "BC");
        Key    				key = keyFact.generateSecret(pbeSpec);


		return key;
	}



	public static byte[] decrypt(TextObject textObject) throws Exception
    {
		System.out.println("encryption is: " + textObject.encryption);
		if(textObject.encryption.equals("RSA"))
    	{
    		return decryptRSA(textObject);
    	}
//    	if(!textObject.encrypted)
//    		return textObject.text;
    	
    	byte[] cipherText = textObject.text;
    	Key encryptionKey;
		if (textObject.pbeMode==null) {
			encryptionKey = textObject.symmetricKey;
		}
		else
			encryptionKey = generatePasswordBasedKey(textObject.password, textObject.pbeMode);
		
    	byte[] ivBytes = iv8Bytes;
    	
		if(textObject.encryption.equals("AES")) {
			ivBytes = iv16Bytes;
		}
    	
        // decryption pass
		String encryptionString = textObject.encryption;
        if(textObject.blockCipherMode!=null)
        	encryptionString += "/"+textObject.blockCipherMode;
        if(textObject.padding!=null)
        	encryptionString += "/"+textObject.padding;
        
        
        Cipher          cipher = Cipher.getInstance(encryptionString, "BC");
    	int ctLength = cipherText.length;
        
        Key	decryptionKey = new SecretKeySpec(encryptionKey.getEncoded(), encryptionKey.getAlgorithm());
        
        
        if(textObject.blockCipherMode.equals("ECB")||textObject.encryption.equals("ARC4"))
        	cipher.init(Cipher.DECRYPT_MODE, decryptionKey);
        else
        	cipher.init(Cipher.DECRYPT_MODE, decryptionKey, new IvParameterSpec(ivBytes));
        
        
        
        byte[] plainText;

        int ptLength = 0;
        
        if (textObject.digest!=null)
        {
        	plainText = cipher.doFinal(cipherText, 0, ctLength);
            int    messageLength = plainText.length - textObject.hash.getDigestLength();
            
            textObject.hash.update(plainText, 0, messageLength);
            
            byte[] messageHash = new byte[textObject.hash.getDigestLength()];
            System.arraycopy(plainText, messageLength, messageHash, 0, messageHash.length);
            byte[] plainTextWithoutHash = new byte[messageLength];
            System.arraycopy(plainText, 0, plainTextWithoutHash, 0, messageLength);
            //plainText = Utils.toString(plainText, messageLength);
            plainText = plainTextWithoutHash;
            System.out.println("plain : " + plainText + " verified: " + MessageDigest.isEqual(textObject.hash.digest(), messageHash));
        
        }
        else
        {
			plainText = new byte[cipher.getOutputSize(ctLength)];
			ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
			ptLength += cipher.doFinal(plainText, ptLength);
		}
		System.out.println("plain : " + Utils.toHex(plainText, ptLength) + " bytes: " + ptLength);
        textObject.text = plainText;
        textObject.encrypted = false;
        return plainText;
        
        
    }
	
	static byte[]	iv16Bytes = new byte[] { 
        0x00, 0x00, 0x00, 0x01, 0x04, 0x05, 0x06, 0x07,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
	static byte[]	iv8Bytes = new byte[] { 
		0x00, 0x00, 0x00, 0x01, 0x04, 0x05, 0x06, 0x07 };
}
