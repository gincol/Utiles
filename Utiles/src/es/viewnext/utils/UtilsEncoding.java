package es.viewnext.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class UtilsEncoding {

	public static final String UTF8_BOM = "\uFEFF";

	public static Charset detectCharset(File f, String[] charsets) {

		Charset charset = null;

		for (String charsetName : charsets) {
			charset = detectCharset(f, Charset.forName(charsetName));
			if (charset != null) {
				break;
			}
		}

		return charset;
	}

	public static Charset detectCharset(File f, Charset charset) {
		try {
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(f));

			CharsetDecoder decoder = charset.newDecoder();
			decoder.reset();

			byte[] buffer = new byte[512];
			boolean identified = false;
			while ((input.read(buffer) != -1) && (!identified)) {
				identified = identify(buffer, decoder);
			}

			input.close();

			if (identified) {
				return charset;
			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
		}
	}

	private static boolean identify(byte[] bytes, CharsetDecoder decoder) {
		try {
			decoder.decode(ByteBuffer.wrap(bytes));
		} catch (CharacterCodingException e) {
			return false;
		}
		return true;
	}

	public static boolean isUTF8BOM(String s) {
		boolean retorno = false;
		if (s.startsWith(UTF8_BOM)) {
			retorno = true;
		}
		return retorno;
	}

	public static String detectEncoding(String file) {
		String retorno = "";
		Path path = Paths.get(file);
		try {
			byte[] data = Files.readAllBytes(path);
			CharsetDetector cd = new CharsetDetector();
			cd.setText(data);
			CharsetMatch cm = cd.detect();
			retorno = cm.getName();
			if ("UTF-8".equals(retorno) || "UTF8".equals(retorno)) {
				retorno += isUTF8BOM(new String(data)) ? " BOM" : "";
			}
		} catch (IOException e) {
			retorno = "Error de acceso al fichero";
		}

		return retorno;
	}

	public static String convertToHex(File file) {
		int bytesCounter = 0;
		int value = 0;
		StringBuilder sbHex = new StringBuilder();
		StringBuilder sbText = new StringBuilder();
		StringBuilder sbResult = new StringBuilder();

		InputStream is;
		try {
			is = new FileInputStream(file);
			while ((value = is.read()) != -1) {
				// convert to hex value with "X" formatter
				sbHex.append(String.format("%02X ", value));

				// If the chracater is not convertable, just print a dot symbol
				// "."
				if (!Character.isISOControl(value)) {
					sbText.append((char) value);
				} else {
					sbText.append(".");
				}

				// if 16 bytes are read, reset the counter,
				// clear the StringBuilder for formatting purpose only.
				if (bytesCounter == 15) {
					sbResult.append(sbHex).append("    ").append(sbText).append("\n");
					sbHex.setLength(0);
					sbText.setLength(0);
					bytesCounter = 0;
				} else {
					bytesCounter++;
				}
			}

			// if still got content
			if (bytesCounter != 0) {
				// add spaces more formatting purpose only
				for (; bytesCounter < 16; bytesCounter++) {
					// 1 character 3 spaces
					sbHex.append("   ");
				}
				sbResult.append(sbHex).append("    ").append(sbText).append("\n");
			}
			is.close();
		} catch (IOException e) {
			sbResult.append("Error de acceso al fichero");
		}

		return sbResult.toString();
	}

	public static byte[] convertEncoding(byte[] arr, Charset encodeCharset, Charset decodeCharset) {
		ByteBuffer inputBuffer = ByteBuffer.wrap(arr);
		byte[] outputData = {};
		try {
			CharBuffer data = encodeCharset.decode(inputBuffer);
			ByteBuffer outputBuffer;
			outputBuffer = decodeCharset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE)
					.onUnmappableCharacter(CodingErrorAction.REPLACE).encode(data);
			outputData = outputBuffer.array();
		} catch (CharacterCodingException e) {
			System.out.println("error: " + e.getMessage());
		}
		return outputData;
	}

	public static char convertEncodingFile2(char c, Charset charsetOrigen, Charset charsetDestino) throws IOException {
		String temp = new String(new char[] { c });
		char[] charArr = {};
		charArr = (new String(temp.getBytes(charsetOrigen))).toCharArray();
		return charArr[0];
	}

	public static String convertEncodingFile(String file, Charset charsetOrigen, Charset charsetDestino)
			throws IOException {
		StringBuffer retorno = new StringBuffer();
		String strOrigen = readFile(file, charsetOrigen);
		byte[] bb = strOrigen.getBytes();
		String str = new String(bb, charsetDestino);
		retorno.append(str);
		return retorno.toString();
	}

	private static String readFile(String filename, Charset charsetOrigen) throws IOException {
		File f = new File(filename);
		StringBuffer sb = new StringBuffer();
		if (f.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), charsetOrigen));

			String nextLine = "";
			while ((nextLine = br.readLine()) != null) {
				sb.append(nextLine + " ");
			}
			br.close();
		}
		return sb.toString();
	}
}
