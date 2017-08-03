package tools.rsa;

import java.io.File;

/**
 * 生成license
 * @author zhao
 * 2014.6.15
 */
public class LicenseGenerator {

    /**
     * serial：由客户提供
     * timeEnd：过期时间
     */
    private static String licensestatic = "serial=568b8fa5cdfd8a2623bda1d8ab7b7b34;" +
                                          "timeEnd=1404057600000";

    private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaa8y9g+ioR2OSs8njT5uCGWm0YmOA1elSu/P5\n"
            + "D3XYPCHsUPab74V5Og+NEZeTk9/LtG+jPSpKekTWm67gS2lQYWoygTwnoWsr4woaqNXmWw7L8Ty0\n"
            + "LQFTojZgyynvu2RSIJp4c76z6SV/khiP/ireGtt8uzXPswPO5uaPU38tQQIDAQAB";

    /**
     * RSA算法
     * 公钥和私钥是一对，此处只用私钥加密
     */
    public static final String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJprzL2D6KhHY5KzyeNPm4IZabRi\n"
            + "Y4DV6VK78/kPddg8IexQ9pvvhXk6D40Rl5OT38u0b6M9Kkp6RNabruBLaVBhajKBPCehayvjChqo\n"
            + "1eZbDsvxPLQtAVOiNmDLKe+7ZFIgmnhzvrPpJX+SGI/+Kt4a23y7Nc+zA87m5o9Tfy1BAgMBAAEC\n"
            + "gYAVnlfohEoTHQN0q1TtTNzRhutEhK23gLsMiSGr0Z1G64w4QFF2HT9LbHR25GqbD426QAWNDegY\n"
            + "yytN/DesUQJqNXx8vuEuqs7+MQDgKgJqpAx+Fg3Iwsk/SVjq7meaSVGCgPKhtWHJk5oXoRMpsrlT\n"
            + "AwUjpdpAZXIIKW3mrqkW0QJBANq4INw6lZlqRFtxT4uzYQYtzmB/nxMnCbL2SQ4ZQ/4CWlQpOnR/\n"
            + "mH2JxIBCVtTADFlPM0DWF4aoqykYs9tu2X0CQQC0vgEk8DpkQbh1kgIyBGWCqYSKISTSXia0rbYo\n"
            + "FPnzdldgtZVirNGNmiJGL8RPz0YKpZNOg9FLHq/oYXSNFI4VAkAJ4OcbC0pWc4ZC2wtMs/1d2hPI\n"
            + "J/t3UfwOKTGDgYCgqFqMEpChUmIAyYgmgtiJI2NrZThbZVAKtPOGF6eH8anBAkAbxkL4wS3H8E1/\n"
            + "S7OoqgJLZO9oJpW4+hzqkPM4D5klb58Xzm+pXTNKllAEBx0cwpZZ1n3fh+Qmrg2MIUW+1FTNAkBt\n"
            + "WECowLUqW014M96WsFpiof7kjteOBNOjFyxhIbx2eT7//bnrADfq2Xu1/mSedUKrjGr/O+FRi7PO\n"
            + "u7WhF6C9";

    public static void generator() throws Exception {
        System.err.println("私钥加密——公钥解密");
        //String source = "568b8fa5cdfd8a2623bda1d8ab7b7b34";
        System.out.println("原文字：\r\n" + licensestatic);
        byte[] data = licensestatic.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
        System.out.println("加密后：\r\n" + new String(encodedData)); //加密后乱码是正常的

        Base64Utils.byteArrayToFile(encodedData, FileUtil.getBasePath()+File.separator+"license.dat");
        System.out.println("license.dat：\r\n" + FileUtil.getBasePath()+File.separator+"license.dat");

        //解密
        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
        String target = new String(decodedData);
        System.out.println("解密后: \r\n" + target);
    }

    public static void main(String[] args) throws Exception {
        generator();
    }
}