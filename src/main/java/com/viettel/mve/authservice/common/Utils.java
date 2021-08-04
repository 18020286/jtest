package com.viettel.mve.authservice.common;

import java.nio.charset.Charset;
import java.util.Random;

import com.google.common.hash.Hashing;

public class Utils {
	public static String generationPassword(int length) {
		Random r = new Random();
		String alphabet = "1234567890abcdefghiABCDEFGHI";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
		}
		return sb.toString();
	}
	
	public static String generationSceretCode(String username) {
		String salt = "9306d36da49ef387712e23d4e7d0578c";
		StringBuilder sb = new StringBuilder(username);
		sb.append(System.currentTimeMillis());
		sb.append(salt);
		String code = Hashing.sha512().hashString(sb.toString(), Charset.forName("UTF-8")).toString();
		return code;
	}
}
