package com.amster.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmsterUtils {
	
	private static final Pattern PATTERN = Pattern.compile("[^A-Za-z0-9_\\-]");

	private static final int MAX_LENGTH = 127;

	public static String escapeStringAsFilename(String in){

	    StringBuffer sb = new StringBuffer();

	    // Apply the regex.
	    Matcher m = PATTERN.matcher(in);

	    while (m.find()) {

	        // Convert matched character to percent-encoded.
	        String replacement = "%"+Integer.toHexString(m.group().charAt(0)).toUpperCase();

	        m.appendReplacement(sb,replacement);
	    }
	    m.appendTail(sb);

	    String encoded = sb.toString();

	    // Truncate the string.
	    int end = Math.min(encoded.length(),MAX_LENGTH);
	    return encoded.substring(0,end);
	}
	
	public static String truncateForText(String s, int maxBytes) {
	    int b = 0;
	    for (int i = 0; i < s.length(); i++) {
	        char c = s.charAt(i);

	        // ranges from http://en.wikipedia.org/wiki/UTF-8
	        int skip = 0;
	        int more;
	        if (c <= 0x007f) {
	            more = 1;
	        }
	        else if (c <= 0x07FF) {
	            more = 2;
	        } else if (c <= 0xd7ff) {
	            more = 3;
	        } else if (c <= 0xDFFF) {
	            // surrogate area, consume next char as well
	            more = 4;
	            skip = 1;
	        } else {
	            more = 3;
	        }

	        if (b + more > maxBytes) {
	            return s.substring(0, i);
	        }
	        b += more;
	        i += skip;
	    }
	    return s;
	}
	
	public static Charset getFileCharEncoding(String path, String inputFile){
		
		//This method tries to determine the character encoding of the file
		//using trial and error (literally).
		
		String line;
		BufferedReader reader;
		
		try {
			reader = new BufferedReader(
					Files.newBufferedReader(
					FileSystems.getDefault().getPath(path,inputFile),StandardCharsets.UTF_8));
			
			line = reader.readLine();
			reader.close();
			return StandardCharsets.UTF_8;
		} catch (IOException e) {
			try {
				reader = new BufferedReader(
						Files.newBufferedReader(
						FileSystems.getDefault().getPath(path, inputFile),StandardCharsets.UTF_16));
				
				line = reader.readLine();
				reader.close();
				return StandardCharsets.UTF_16;
			} catch (IOException e1) {
				try {
					reader = new BufferedReader(
							Files.newBufferedReader(
							FileSystems.getDefault().getPath(path, inputFile),StandardCharsets.ISO_8859_1));
					
					line = reader.readLine();
					reader.close();
					return StandardCharsets.ISO_8859_1;
				} catch (IOException e2) {
					return StandardCharsets.US_ASCII;
				}
			}
			

		}
		
	}

}
