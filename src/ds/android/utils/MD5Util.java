package ds.android.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @Title: MD5Util.java
 * @Description: MD5摘要信息辅助类
 * @author kuguobing<kuguobing@snda.com>
 * @date 2011-10-3 下午03:55:43
 * @version V1.0
 */
public abstract class MD5Util {
	/**
	 * MD5签名
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String getMD5Mac(String key, String value) {
		return getMD5Mac(key, value.getBytes());
	}
	/**
	 * MD5签名
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String getMD5Mac(String key, byte[] value) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(value);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		
		byte[] byteArray = null;
		if (key == null)
			byteArray = messageDigest.digest();
		else
			byteArray = messageDigest.digest(key.getBytes());
		
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		
		return md5StrBuff.toString();
	}
}
