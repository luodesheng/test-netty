package lds.gw.util;


public class ByteUtil {

	private static final String HEX = "0123456789ABCDEF";

	/**
	 * @description byte[]转16进制字符串形式
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			String hv = Integer.toHexString(v).toUpperCase();
			if (2 > hv.length())
				stringBuilder.append(0);
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * @description 16进制形式字符转byte[]
	 * @param hexString
	 * @return
	 */
	public static byte[] hexToBytes(String hexString) {
		hexString = hexString.toUpperCase();
		char[] hexChars = hexString.toCharArray();
		byte[] bytes = new byte[hexString.length() / 2];

		for (int i = 0; i < bytes.length; i++) {
			int pos = i * 2;
			bytes[i] = (byte) (HEX.indexOf(hexChars[pos]) << 4 | HEX
					.indexOf(hexChars[pos + 1]));
		}
		return bytes;
	}

	/**
	 * @description bcd转10进度制字符串
	 * @param bytes
	 * @return
	 */
	public static String bcdToDecimal‎(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			stringBuilder.append((byte) ((bytes[i] & 0xf0) >>> 4));
			stringBuilder.append((byte) (bytes[i] & 0x0f));
		}
		return stringBuilder.toString().substring(0, 1).equalsIgnoreCase("0") ? stringBuilder
				.toString().substring(1) : stringBuilder.toString();
	}

	/**
	 * @description 10进度制字符串转bcd
	 * @param src
	 * @return
	 */
	public static byte[] decimalToBcd(String src) {
		int len = src.length();
		int mod = len % 2;
		if (mod != 0) {
			src = "0" + src;
			len = src.length();
		}
		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}
		byte bytes[] = new byte[len];
		abt = src.getBytes();
		int j, k;
		for (int p = 0; p < src.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}
			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}
			int a = (j << 4) + k;
			byte b = (byte) a;
			bytes[p] = b;
		}
		return bytes;
	}

	/**
	 * @description ASCII码转BCD码
	 * @param ascii
	 * @param length
	 * @return
	 */
	public static byte[] asciiToBcd(String s) {
		if(0 != s.length() % 2)
			s += "0";
		byte[] asc = strToAscii(s);
		int len = asc.length / 2;
		byte[] bcd = new byte[len];
		for (int i = 0; i < len; i++) {
			bcd[i] = (byte) ((asc[2 * i] << 4) | asc[2 * i + 1]);
		}
		return bcd;
	}
	
	/**
	 * @description 字符串转ascii
	 * @param s
	 * @return
	 */
	public static byte[] strToAscii(String s) {
		byte[] str = s.toUpperCase().getBytes();
		byte[] ascii = new byte[str.length];
		for (int i = 0; i < ascii.length; i++) {
			ascii[i] = (byte) asciiValue(str[i]);
		}
		return ascii;
	}
	
	private static int asciiValue(byte asc) {
		byte bcd;
		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}


	/**
	 * @description BCD转ascii字符
	 * @param bytes
	 * @return
	 */
	public static String bcdToAscii(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;
		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	/**
	 * @description bcd转byte[]
	 * @param bcd
	 * @return
	 */
	public static byte[] bcdToBytes(byte[] bcd) {
		byte[] bytes = new byte[bcd.length * 2];

		for (int i = 0; i < bcd.length; i++) {
			bytes[i * 2] = (byte) ((bcd[i] >> 4) & 0x0f);
			bytes[i * 2 + 1] = (byte) (bcd[i] & 0x0f);

		}
		return bytes;
	}

	/**
	 * @description int转byte[]
	 * @param src
	 * @return
	 */
	public static byte[] intToBytes(int src) {
		byte[] targets = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (targets.length - 1 - i) * 8;
			targets[i] = (byte) ((src >>> offset) & 0xff);
		}
		return targets;
	}

	/**
	 * @description byte[]转int
	 * @param bytes
	 * @return
	 */
	public static int bytesToInt(byte[] bytes) {
		byte[] arr = new byte[4];
		if (2 == bytes.length) {
			arr[0] = 0;
			arr[1] = 0;
			arr[2] = bytes[0];
			arr[3] = bytes[1];
		} else
			arr = bytes;
		int intValue = 0;
		for (int i = 0; i < arr.length; i++) {
			intValue += (arr[i] & 0xFF) << (8 * (3 - i));
		}
		return intValue;
	}

	/**
	 * @description 格式化byte[]
	 * @param bytes
	 * @return
	 */
	public static String formatBytes(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder("[");
		for (int i = 0; i < bytes.length; i++) {
			if (0 < i)
				stringBuilder.append(",");
			stringBuilder.append(bytes[i]);
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	/**
	 * @description byte[]转bits
	 * @param bytes
	 * @return
	 */
	public static String bytesToBinary(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			stringBuilder.append(byteToBinary(bytes[i]));
		}
		return stringBuilder.toString();
	}

	/**
	 * @description byte转bit字符
	 * @param b
	 * @return
	 */
	public static String byteToBinary(byte b) {
		int z = b;
		z |= 256;
		String str = Integer.toBinaryString(z);
		int len = str.length();
		return str.substring(len - 8, len);
	}

	/**
	 * @description bits转byte
	 * @param src
	 * @return
	 */
	public static byte binaryToByte(String src) {
		byte result = 0;
		for (int i = src.length() - 1, j = 0; i >= 0; i--, j++) {
			result += (Byte.parseByte(src.charAt(i) + "") * Math.pow(2, j));
		}
		return result;
	}
	
	/**
	 * @description 将short转成2字节bytes
	 * @param src
	 * @return
	 */
	public static byte[] shortToBytes(short src){
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (src >> 8);
		bytes[1] = (byte) (src);
		return bytes;
	}
	
	/**
	 * @description 将2字节bytes转为short
	 * @param bytes
	 * @return
	 */
	public static short bytesToShort(byte[] bytes){
		if(2 != bytes.length)
			throw new IllegalArgumentException("byte数组必须为2字节");
		return (short) (((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));  
	}
}
