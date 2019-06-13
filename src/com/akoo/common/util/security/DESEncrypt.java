package com.akoo.common.util.security;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * 安全加密?
 *
 */
public class DESEncrypt {


	/**
	 * DES加解?
	 *
	 * @param plainText
	 *            要处理的byte[]
	 * @param key
	 *            密钥
	 * @param mode
	 *            模式
	 */
	private static byte[] coderByDES(byte[] plainText, String key, int mode)
			throws InvalidKeyException, InvalidKeySpecException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			BadPaddingException, IllegalBlockSizeException,
			UnsupportedEncodingException {
		SecureRandom sr = new SecureRandom();
		byte[] resultKey = makeKey(key);
		DESKeySpec desSpec = new DESKeySpec(resultKey);
		SecretKey secretKey = SecretKeyFactory.getInstance("DES")
				.generateSecret(desSpec);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(mode, secretKey, sr);
		return cipher.doFinal(plainText);
	}

	/**
	 * 生产8位的key
	 *
	 * @param key
	 *            字符串形?
	 */
	private static byte[] makeKey(String key)
			throws UnsupportedEncodingException {
		byte[] keyByte = new byte[8];
		byte[] keyResult = key.getBytes("UTF-8");
		for (int i = 0; i < keyResult.length && i < keyByte.length; i++) {
			keyByte[i] = keyResult[i];
		}
		return keyByte;
	}

	/**
	 /**
	 * DES加密
	 *
	 * @param plainText
	 *            明文
	 * @param privateKey
	 *            密钥
	 */
	public static String encoderByDES(String privateKey, String plainText) {
		try {
			byte[] result = coderByDES(plainText.getBytes("UTF-8"), privateKey,
					Cipher.ENCRYPT_MODE);
			return byteArr2HexStr(result);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	/**
	 * DES解密
	 *
	 * @param secretText
	 *            密文
	 * @param privateKey
	 *            密钥
	 */
	public static String decoderByDES(String privateKey, String secretText) {
		try {
			byte[] result = coderByDES(hexStr2ByteArr(secretText), privateKey,
					Cipher.DECRYPT_MODE);
			return new String(result, "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	/**
	 * 将byte数组转换为表?16进制值的字符串， 如：byte[]{8,18}转换为：0813? 和public static byte[]
	 * hexStr2ByteArr(String strIn) 互为可?的转换过程
	 *
	 * @param arrB
	 *            ?要转换的byte数组
	 * @return 转换后的字符?
	 */
	private static String byteArr2HexStr(byte[] arrB) {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，?以字符串的长度是数组长度的两?
		StringBuilder sb = new StringBuilder(iLen * 2);
		for (byte anArrB : arrB) {
			int intTmp = anArrB;
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数?要在前面?0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表?16进制值的字符串转换为byte数组? 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可?的转换过程
	 *
	 * @param strIn
	 *            ?要转换的字符?
	 * @return 转换后的byte数组
	 */
	private static byte[] hexStr2ByteArr(String strIn) throws NumberFormatException {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;
		// 两个字符表示?个字节，?以字节数组长度是字符串长度除?2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

}
