package com.rpa.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES 工具类
 * @author esp
 * @version 1.0
 * @date 2022/7/18 10:36
 */
@Slf4j
public class AesUtil {

    /**
     * 加密用的Key 可以用26个字母和数字组成 使用AES-128-CBC加密模式，key需要为16位。
     */
    private static final String IV ="NIfb&95GUY86Gfgh";
    private static final String AES = "AES";
    private static final String AES_CBC_NOPADDING = "AES/CBC/NoPadding";

    /**
     * AES算法加密明文
     * @param data 明文
     *  key 密钥，长度16
     *  iv 偏移量，长度16
     * @return 密文
     * @Date 2022/7/20 16:41
     */
    public static String encrypt(String data, String key){
        try {
            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;

            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AES);
            // CBC模式，需要一个向量iv，可增加加密算法的强度
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext);
            // BASE64做转码。
            return base64Encode(encrypted).trim();
        } catch (Exception e) {
            log.error("AES加密失败： error:{}",e.getMessage());
            return null;
        }
    }
    /**
     * AES算法解密密文
     * @param data 明文
     *  key 密钥，长度16
     *  iv 偏移量，长度16
     * @return 明文
     * @Date 2022/7/20 16:47
     */
    public static String decrypt(String data, String key){
        try {
            //先用base64解密
            byte[] encrypted1 = base64Decode(data);

            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), AES);
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString.trim();
        } catch (Exception e) {
            log.error("AES解密失败： error:{}",e.getMessage());
            return null;
        }
    }
    /**
     * 编码
     * @param byteArray 字节数组
     * @return 编码后字符串
     */
    public static String base64Encode(byte[] byteArray) {
        return new String(new Base64().encode(byteArray));
    }

    /**
     * 解码
     * @param base64EncodedString 需要解码的字符串
     * @return 字节数组
     */
    public static byte[] base64Decode(String base64EncodedString) {
        return new Base64().decode(base64EncodedString);
    }

//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        String phone = "16620952125";
//        String message = "【顺丰速运】感谢您使用电子签约系统，您的验证码为:880643";
//        String key = "88DF7EFDFCB9ADBE83DBE1E95E391CC2";
//
//        String encrypt = encrypt(message, key);
//        System.out.println("密文:" + encrypt);
//        String decrypt = decrypt(encrypt, key);
//        System.out.println("解密:" + decrypt);
//
////        KeyGenerator gen = KeyGenerator.getInstance("AES");
////        gen.init(128); /* 128-bit AES */
////        SecretKey secret = gen.generateKey();
////        byte[] binary = secret.getEncoded();
////        String text = String.format("%032X", new BigInteger(+1, binary));
////        System.out.println(text);
//    }
}
