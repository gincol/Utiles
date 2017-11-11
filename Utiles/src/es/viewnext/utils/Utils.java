package es.viewnext.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.jar.JarFile;

import com.strobel.assembler.InputTypeLoader;
import com.strobel.assembler.metadata.ITypeLoader;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.ITextOutput;
import com.strobel.decompiler.PlainTextOutput;

import es.viewnext.modelo.Fichero;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class Utils {

	static NumberFormat formatter = new DecimalFormat("#0.00");

	public static String doubleToString(double numero) {
		String retorno = formatter.format(numero);
		return retorno;
	}

	public static String longToString(long numero) {
		String retorno = Long.toString(numero);
		return retorno;
	}

	public static String integerToString(int numero) {
		String retorno = Integer.toString(numero);
		return retorno;
	}

	public static String intToString(int numero) {
		String retorno = Integer.toString(numero);
		return retorno;
	}

	public static Integer stringToInteger(String numero) {
		Integer retorno = new Integer(numero);
		return retorno;
	}

	public static int stringToInt(String numero) {
		Integer retorno = 0;
		if (numero != null && !numero.trim().isEmpty()) {
			retorno = new Integer(numero).intValue();
		}
		return retorno;
	}

	public static void decompile(Fichero f) {
		try {
			Path filePath = Paths.get(f.getRuta());
			JarFile jf = new JarFile(filePath.toAbsolutePath().toString());

			ITypeLoader it = new JarTypeLoader(jf);
			InputTypeLoader itl = new InputTypeLoader(it);

			DecompilerSettings ds = new DecompilerSettings();
			ds.setTypeLoader(itl);
			ITextOutput ito = new PlainTextOutput();

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Decompiler");
			alert.setHeaderText("Clase '" + f.getNombre() + "'");

			TextArea textArea;
			if (f.getNombre().endsWith(".class")) {
				Decompiler.decompile(f.getNombre(), ito, ds);
				textArea = new TextArea(ito.toString());
			} else {
				URL url = new URL("jar:file:/" + filePath.toAbsolutePath() + "!/" + f.getNombre());
				InputStream is = url.openStream();
				Scanner sc = new Scanner(is).useDelimiter("\\A");
				String theString = sc.hasNext() ? sc.next() : "";
				textArea = new TextArea(theString);
				sc.close();
			}
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.getDialogPane().setExpanded(true);

			alert.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

	public static <T> void refreshTableView(TableView<T> tableView, int col) {
		tableView.getColumns().get(col).setVisible(false);
		tableView.getColumns().get(col).setVisible(true);
	}

	public static String getFileChecksum(String digestName, String fileName) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(digestName);
		File file = new File(fileName);
		// Get file input stream for reading the file content
		FileInputStream fis = new FileInputStream(file);

		// Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;

		// Read file data and update in message digest
		while ((bytesCount = fis.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		}
		// close the stream; We don't need it now.
		fis.close();

		// Get the hash's bytes
		byte[] bytes = digest.digest();

		// This bytes[] has bytes in decimal format;
		// Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		// return complete hash
		return sb.toString();
	}
	public static String getHash(String digestName, String entrada) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		MessageDigest md = MessageDigest.getInstance(digestName);
        md.reset();
        byte[] buffer = entrada.getBytes("UTF-8");
        md.update(buffer);
        byte[] digest = md.digest();

        String hexStr = "";
        for (int i = 0; i < digest.length; i++) {
            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return hexStr;
	}
	
	public static String getErrorText(Exception ex){
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
