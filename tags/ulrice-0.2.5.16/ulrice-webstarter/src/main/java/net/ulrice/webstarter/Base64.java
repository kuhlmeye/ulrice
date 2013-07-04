package net.ulrice.webstarter;

import java.util.Arrays;

/**
 * Encodes/Decodes strings to base64 format.
 * 
 * @author DL10KUH
 */
public class Base64 {

	private static String codepage = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	public static String encode(String text) {
		if (text == null) {
			return null;
		}
		char[] byteArr = text.toCharArray();
		int len = byteArr.length;
		int miss = len % 3;
		char[] inputArr = new char[len + 3 - miss];
		Arrays.fill(inputArr, (char) '=');
		System.arraycopy(byteArr, 0, inputArr, 0, len);

		StringBuffer result = new StringBuffer();
		for (int i = 0; i < inputArr.length; i += 3) {
			char b1 = inputArr[i];
			char b2 = inputArr[i + 1];
			char b3 = inputArr[i + 2];

			char out1 = codepage.charAt((b1 & 0xFC) >> 2);
			char out2 = codepage.charAt(((b1 & 0x03) << 4) + ((b2 & 0xF0) >> 4));
			char out3 = codepage.charAt(((b2 & 0x0F) << 2) + ((b3 & 0xC0) >> 6));
			char out4 = codepage.charAt(b3 & 0x3F);

			result.append(out1).append(out2).append(out3).append(out4);
		}
		return result.toString();
	}

	public static String decode(String text) {
		if (text == null) {
			return null;
		}

		char[] byteArr = text.toCharArray();
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < byteArr.length; i += 4) {
			int b1 = codepage.indexOf(byteArr[i]);
			int b2 = codepage.indexOf(byteArr[i + 1]);
			int b3 = codepage.indexOf(byteArr[i + 2]);
			int b4 = codepage.indexOf(byteArr[i + 3]);

			char out1 = (char) (((b1 & 0x3F) << 2) + ((b2 & 0x30) >> 4));
			char out2 = (char) (((b2 & 0x0F) << 4) + ((b3 & 0x3C) >> 2));
			char out3 = (char) (((b3 & 0x03) << 6) + (b4 & 0x3F));

			result.append(out1);
			if (out2 != '=') {
				result.append(out2);
			}
			if (out3 != '=') {
				result.append(out3);
			}
		}

		return result.toString();
	}
}
