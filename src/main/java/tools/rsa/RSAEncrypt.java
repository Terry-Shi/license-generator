package tools.rsa;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;

/**
 * RSA加密类
 */
public class RSAEncrypt {
	public static void main(String[] args) {
		try {
			RSAEncrypt encrypt = new RSAEncrypt();
			String encryptText = "12345678";
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(1024);
			KeyPair keyPair = keyPairGen.generateKeyPair();

			// Generate keys
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 私钥
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic(); // 公钥
			byte[] e = encrypt.encrypt(publicKey, encryptText.getBytes());
			byte[] de = encrypt.decrypt(privateKey, e);
			System.out.println(encrypt.bytesToString(e));
			System.out.println();
			System.out.println(encrypt.bytesToString(de));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * byte数组转为string
	 * @param encrytpByte
	 * @return
	 */
	protected String bytesToString(byte[] encrytpByte) {
		String result = "";
		for (Byte bytes : encrytpByte) {
			result += (char) bytes.intValue();
		}
		return result;
	}

	/**
	 * 加密方法
	 * @param publicKey
	 * @param obj
	 * @return
	 */
	protected byte[] encrypt(RSAPublicKey publicKey, byte[] obj) {
		if (publicKey != null) {
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.ENCRYPT_MODE, publicKey);
				return cipher.doFinal(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 解密方法
	 * @param privateKey
	 * @param obj
	 * @return
	 */
	protected byte[] decrypt(RSAPrivateKey privateKey, byte[] obj) {
		if (privateKey != null) {
			try {
				Cipher cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, privateKey);
				return cipher.doFinal(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
