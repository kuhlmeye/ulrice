package net.ulrice.webstarter;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class EncryptionUtils {

    private static byte[] salt = {
        (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32,
        (byte)0x56, (byte)0x34, (byte)0xE3, (byte)0x03
    };
    
	private static final char[] PASSWORD = "slvheoelkfvde".toCharArray();

	public static String encrypt(String value) {
		return handleCrypt(Cipher.ENCRYPT_MODE, Base64.encode(value));

	}
	
	public static String decrypt(String value) {
		return Base64.decode(handleCrypt(Cipher.DECRYPT_MODE, value));
	}
	
	private static String handleCrypt(int mode, String value) {
		if(value == null) {
			return null;
		}
		if(value.equals("")) {
			return "";
		}
		try {
			SecretKeyFactory instance = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			SecretKey key = instance.generateSecret(new PBEKeySpec(PASSWORD));
			Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
			

            // Prepare the parameters to the cipthers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, 8);			
			pbeCipher.init(mode, key, paramSpec);
			
			return new String(pbeCipher.doFinal(value.getBytes()));			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		return null;
	}
}
