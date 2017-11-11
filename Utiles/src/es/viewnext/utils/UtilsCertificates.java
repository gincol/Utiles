package es.viewnext.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import es.viewnext.Main;
import es.viewnext.modelo.Alias;
import es.viewnext.modelo.Certificado;
import es.viewnext.modelo.KeyStoreApp;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class UtilsCertificates {

	static final private char SEP = File.separatorChar;
	static final private String tmpKeystorePassword = "changeit";
	static final private String tmpKeystore = System.getProperty("java.io.tmpdir") + SEP + "keystore.jks";
	static final private String javaKeystore = System.getProperty("java.home") + SEP + "lib" + SEP + "security" + SEP
			+ "cacerts";
	static final private String javaKeystorePassword = "changeit";

	/**
	 * Recupera el certificado desde una url
	 * 
	 * @param host
	 *            servidor remoto
	 * @param port
	 *            puerto. Si no se informa se toma el por defecto 443
	 * @return devuelve el certificado recuperado
	 * @throws Exception
	 */
	public static Certificado getCertificateFromServerNew(String host, int port) {
		boolean excepcion = false;
		StringWriter errors = new StringWriter();
		Certificado certificado = new Certificado();
		X509Certificate[] certs = null;
		if (port == 0) {
			port = 443;
		}
		String https_url = "https://" + host + ":" + port;
		System.out.println("getCertFromURL: https://" + host + ":" + port);
		URL url;
		HttpsURLConnection con = null;
		try {
			url = new URL(https_url);
			con = (HttpsURLConnection) url.openConnection();
			if (con != null) {
				System.out.println("Response Code : " + con.getResponseCode());
				System.out.println("Cipher Suite : " + con.getCipherSuite());
				System.out.println("\n");
				certs = (X509Certificate[]) con.getServerCertificates();
				for (Certificate cert : certs) {
					System.out.println("Cert Type : " + cert.getType());
					System.out.println("Cert Hash Code : " + cert.hashCode());
					System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
					System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
					System.out.println("\n");
				}
				
				certificado.setCertificateList(Arrays.asList(certs));
				certificado.setNumCertsInChain(certs.length);
				certificado.setSubject(certs[0].getIssuerDN().getName());
				certificado.setFechaCaducidad(DateUtil.parse(certs[0].getNotAfter()));
			}
		} catch (MalformedURLException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (SSLHandshakeException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (IOException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		}
		
		if (excepcion) {
			UtilsWindow.customAlert("Import certificado en KeyStore", errors.toString());
		} 
		return certificado;
	}

	public static String verResumenCertificado(Certificado certificado) {
		System.out.println("verResumenCertificado");
		boolean excepcion = false;
		StringWriter errors = new StringWriter();
		StringBuffer buf = new StringBuffer();
		try {
			int i = 0;
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			for (X509Certificate cr : certificado.getCertificateList()) {
				X509Certificate cert = (X509Certificate) cr;
				buf.append(" " + (i + 1) + " Subject " + cert.getSubjectDN() + "\n");
				buf.append("   Issuer  " + cert.getIssuerDN() + "\n");
				sha1.update(cert.getEncoded());
				buf.append("   sha1    " + UtilsCertificates.toHexString(sha1.digest()) + "\n");
				md5.update(cert.getEncoded());
				buf.append("   md5     " + UtilsCertificates.toHexString(md5.digest()) + "\n");
				buf.append("\n");
				i++;
			}
		} catch (CertificateException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (NoSuchAlgorithmException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (Exception e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		}
		if (excepcion) {
			UtilsWindow.customAlert("Import certificado en KeyStore", errors.toString());
		}
		return buf.toString();
	}

	public static void verDetalleCertificado(Certificado certificado) {
		System.out.println("verDetalleCertificado");
		StringBuffer buf = new StringBuffer("Número de certificados: " + certificado.getNumCertsInChain() + "\n");
		int i = 0;
		for (X509Certificate cert : certificado.getCertificateList()) {
			buf.append("Certificado nº: " + (i + 1) + ".\n Subject: " + cert.getSubjectDN() + "\n");
			buf.append("----------------------------------------------------------------------------------------\n");
			buf.append(cert.toString() + "\n");
			i++;
			if (i < certificado.getNumCertsInChain()) {
				buf.append("\n###################################\n\n");
			}
		}
		UtilsWindow.openAlert("Certificado", "Certificado de la URL", certificado.getUrl(), buf.toString());
	}

	public static Certificado getCertificateFromAliasKeystore(KeyStoreApp ksApp, Alias alias) {
		boolean excepcion = false;
		StringWriter errors = new StringWriter();
		InputStream in;
		Certificado certificado = new Certificado();
		try {
			in = new FileInputStream(ksApp.getRuta());
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, ksApp.getPassword().toCharArray());
			in.close();

			Certificate[] chain = ks.getCertificateChain(alias.getNombre());
			X509Certificate certificate = (X509Certificate) ks.getCertificate(alias.getNombre());
			List<X509Certificate> listaX509 = new ArrayList<X509Certificate>();
			if (chain != null) {
				for (Certificate cer : Arrays.asList(chain)) {
					listaX509.add((X509Certificate) cer);
				}
				certificado.setNumCertsInChain(chain.length);
			} else {
				listaX509.add((X509Certificate) ks.getCertificate(alias.getNombre()));
				certificado.setNumCertsInChain(1);
			}
			certificado.setCertificateList(listaX509);
			certificado.setKeyStoreApp(ksApp);
			certificado.setAlias(alias);
			certificado.setSubject(certificate.getSubjectDN().getName());
			certificado.setFechaCaducidad(DateUtil.parse(certificate.getNotAfter()));
		} catch (FileNotFoundException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (KeyStoreException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (NoSuchAlgorithmException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (CertificateException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (IOException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		}
		if (excepcion) {
			UtilsWindow.customAlert("Import certificado en KeyStore", errors.toString());
		}
		return certificado;
	}

	public static List<Alias> getAliasListFromKeystore(ObservableList<KeyStoreApp> ksList) {
		boolean excepcion = false;
		StringWriter errors = new StringWriter();
		List<Alias> listAlias = new ArrayList<Alias>();
		FileInputStream is = null;
		Alias alias = null;
		Enumeration enumAlias = null;
		try {
			for (KeyStoreApp keystoreApp : ksList) {
				is = new FileInputStream(keystoreApp.getRuta());
				KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
				keystore.load(is, keystoreApp.getPassword().toCharArray());

				enumAlias = keystore.aliases();
				while (enumAlias.hasMoreElements()) {
					alias = new Alias();
					alias.setKeyStoreApp(keystoreApp);
					alias.setNombre(enumAlias.nextElement().toString());
					listAlias.add(alias);
				}
			}
		} catch (FileNotFoundException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (KeyStoreException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (NoSuchAlgorithmException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (CertificateException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (IOException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				excepcion = true;
				e.printStackTrace(new PrintWriter(errors));
			}
		}
		if (excepcion) {
			UtilsWindow.customAlert("Import certificado en KeyStore", errors.toString());
		}
		return listAlias;
	}

	public static void deleteAliasFromKeystore(Alias alias) {
		try {
			String command = "keytool -delete -alias \"" + alias.getNombre() + "\" -keystore \""
					+ alias.getKeyStoreApp().getRuta() + "\" -storepass " + alias.getKeyStoreApp().getPassword();
			Process child = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void exportarCertificado(Main main, Certificado certificado) {
		boolean excepcion = false;
		StringWriter errors = new StringWriter();
		try {
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Cert files (*.cer)", "*.cer");
			fileChooser.getExtensionFilters().add(extFilter);
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
			fileChooser.setInitialFileName(certificado.getAlias().getNombre() + ".cer");
			File file = fileChooser.showSaveDialog(main.getPrimaryStage());

			if (file != null) {
				file = new File(file.getPath());
				try {
					FileOutputStream fos = new FileOutputStream(file.getPath());
					fos.write(certificado.getCertificateList().get(0).getEncoded());
					fos.flush();
					fos.close();
				} catch (IOException e) {
					excepcion = true;
					e.printStackTrace(new PrintWriter(errors));
				} catch (CertificateEncodingException e) {
					excepcion = true;
					e.printStackTrace(new PrintWriter(errors));
				} catch (CertificateException e) {
					excepcion = true;
					e.printStackTrace(new PrintWriter(errors));
				}
				String command = "keytool -export -alias \"" + certificado.getAlias().getNombre() + "\" -keystore \""
						+ certificado.getKeyStoreApp().getRuta() + "\" -file " + file.getPath();
				Process child = Runtime.getRuntime().exec(command);
			}
		} catch (IOException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		}
		if (excepcion) {
			UtilsWindow.customAlert("Import certificado en KeyStore", errors.toString());
		}
	}

	public static Certificado addCertificadoToKeystore(Main main, KeyStoreApp ksa) {
		boolean excepcion = false;
		StringWriter errors = new StringWriter();
		InputStream in;
		Certificado certificado = null;
		try {
			in = new FileInputStream(ksa.getRuta());
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, ksa.getPassword().toCharArray());
			in.close();
			certificado = UtilsWindow.selectNewCertificadoDialog(main, ksa);

			if (certificado != null) {
				if (!ks.containsAlias(certificado.getAlias().getNombre())) {
					String command = "keytool -importcert -file \"" + certificado.getRutaLocal() + "\" -alias \""
							+ certificado.getAlias().getNombre() + "\" -keystore \"" + ksa.getRuta() + "\" -storepass "
							+ ksa.getPassword();
					Process child = Runtime.getRuntime().exec(command);
					OutputStream child_stdin = child.getOutputStream();
					BufferedWriter child_writer = new BufferedWriter(new OutputStreamWriter(child_stdin));
					child_writer.write("s");
					child_writer.newLine();
					child_writer.flush();
					child_writer.close();
					child_stdin.close();
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Error");
					alert.setHeaderText("No se puede insertar el certificado con alias '"
							+ certificado.getAlias().getNombre() + "'");
					alert.setContentText("Este alias ya existe");
					alert.showAndWait();
					certificado = null;
				}
			}
		} catch (FileNotFoundException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (KeyStoreException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (NoSuchAlgorithmException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (CertificateException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (IOException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		}
		if (excepcion) {
			UtilsWindow.customAlert("Import certificado en KeyStore", errors.toString());
		}
		return certificado;
	}

	public static void addCertificadoToSelectKeystore(Main main, Certificado certificado) {
		boolean excepcion = false;
		StringWriter errors = new StringWriter();
		File file = null;
		try {
			Alias alias = UtilsWindow.selectKeystoreDialog(main, false);
			if (alias != null) {
				certificado.setAlias(alias);
				KeyStoreApp ksa = alias.getKeyStoreApp();
				String rutaKeystore = ksa.getRuta();

				InputStream in = new FileInputStream(rutaKeystore);
				KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
				ks.load(in, ksa.getPassword().toCharArray());
				in.close();
				
				file = new File(System.getProperty("java.io.tmpdir") + SEP + "tmp.cer");
				FileOutputStream fos = new FileOutputStream(file.getPath());
				fos.write(certificado.getCertificateList().get(0).getEncoded());
				fos.flush();
				fos.close();
				
				if (certificado != null) {
					if (!ks.containsAlias(certificado.getAlias().getNombre())) {
						String command = "keytool -importcert -file \"" + file.getPath() + "\" -alias \""
								+ certificado.getAlias().getNombre() + "\" -keystore \"" + ksa.getRuta()
								+ "\" -storepass " + ksa.getPassword();
						System.out.println("command: " + command);
						Process child = Runtime.getRuntime().exec(command);
						OutputStream child_stdout = child.getOutputStream();
						Thread.sleep(2000);
						BufferedWriter child_writer = new BufferedWriter(new OutputStreamWriter(child_stdout));
						
						child_writer.write("s");
						child_writer.newLine();
						child_writer.flush();
						child_writer.close();
						Thread.sleep(2000);
						child_stdout.close();
						child.destroy();
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Error");
						alert.setHeaderText("No se puede insertar el certificado con alias '"
								+ certificado.getAlias().getNombre() + "'");
						alert.setContentText("Este alias ya existe");
						alert.showAndWait();
					}
				}
			}
		} catch (FileNotFoundException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (KeyStoreException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (NoSuchAlgorithmException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (CertificateException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (IOException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (file != null)
				file.delete();
		}

		if (excepcion) {
			UtilsWindow.customAlert("Import certificado en KeyStore", errors.toString());
		}
	}

	public static KeyStoreApp createKeyStore(Main main) {
		boolean excepcion = false;
		StringWriter errors = new StringWriter();
		KeyStoreApp ksa = new KeyStoreApp();

		Alias alias = UtilsWindow.selectKeystoreDialog(main, true);
		File file = new File(alias.getKeyStoreApp().getRuta());
		ksa.setNombre(file.getName());
		ksa.setRuta(file.getPath());
		ksa.setPassword(alias.getKeyStoreApp().getPassword());

		KeyStore ks = null;
		FileOutputStream fos = null;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, ksa.getPassword().toCharArray());
			fos = new FileOutputStream(file.getPath());
			ks.store(fos, ksa.getPassword().toCharArray());
			fos.close();
		} catch (KeyStoreException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (NoSuchAlgorithmException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (CertificateException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} catch (IOException e) {
			excepcion = true;
			e.printStackTrace(new PrintWriter(errors));
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					excepcion = true;
					e.printStackTrace(new PrintWriter(errors));
				}
		}
		if (excepcion) {
			UtilsWindow.customAlert("Import certificado en KeyStore", errors.toString());
		}
		return ksa;
	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	public static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}

	// public static KeyStore createKeystoreTmp() {
	// KeyStore ks = null;
	// try {
	// ks = KeyStore.getInstance(KeyStore.getDefaultType());
	// ks.load(null, tmpKeystorePassword.toCharArray());
	// FileOutputStream fos = new FileOutputStream(tmpKeystore);
	// ks.store(fos, tmpKeystorePassword.toCharArray());
	// fos.close();
	// } catch (KeyStoreException e) {
	// e.printStackTrace();
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// } catch (CertificateException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return ks;
	// }
	//
	// public static KeyStore getKeystoreTmp() {
	// KeyStore ks = null;
	// try {
	// ks = createKeystoreTmp();
	// File file = new File(tmpKeystore);
	// InputStream in = new FileInputStream(file);
	// ks.load(in, tmpKeystorePassword.toCharArray());
	// } catch (NoSuchAlgorithmException e) {
	// e.printStackTrace();
	// } catch (CertificateException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return ks;
	// }
	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			// PARA JAVA7 y siguientes
			return new X509Certificate[0];
			// throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}

	// public static String getCertBase64Encoded(X509Certificate cert) {
	// try {
	// BASE64Encoder encoder = new BASE64Encoder();
	// String sTmp = new String(encoder.encode(cert.getEncoded()));
	// String sEncoded = X509Factory.BEGIN_CERT + "\r\n";
	// for (int iCnt = 0; iCnt < sTmp.length(); iCnt += 64) {
	// int iLineLength;
	// if (iCnt + 64 > sTmp.length())
	// iLineLength = sTmp.length() - iCnt;
	// else
	// iLineLength = 64;
	// sEncoded = sEncoded + sTmp.substring(iCnt, iCnt + iLineLength) + "\r\n";
	// }
	//
	// sEncoded = sEncoded + X509Factory.END_CERT + "\r\n";
	// return sEncoded;
	// } catch (CertificateException e) {
	// System.out.println(e.getLocalizedMessage());
	// }
	// return null;
	// }

	public static Certificado getCertificateFromServer(String host, int port) throws Exception {
		
		if(host.contains("http")){
			URL aURL = new URL(host);
			host = aURL.getHost();
		}
		System.out.println("host: " + host);
		
		if (port == 0) {
			port = 443;
		}

		InputStream in = new FileInputStream(javaKeystore);
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, javaKeystorePassword.toCharArray());
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();

		System.out.println("Opening connection to " + host + ":" + port + "...");
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		try {
			System.out.println("Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			System.out.println();
			System.out.println("No errors, certificate is already trusted");
		} catch (SSLException e) {
			System.out.println("Error en el Handshake, la CA no existe en nuestro almacen, proseguimos");
			//////////////////////
			// Para recuperar certificados sin tener en cuenta de si existe o no
			// la CA en nuestro almacén
			// TrustManager[] trustAllCerts = new TrustManager[] {
			// new X509TrustManager() {
			// public java.security.cert.X509Certificate[] getAcceptedIssuers()
			// {
			// return new X509Certificate[0];
			// }
			// public void checkClientTrusted(
			// java.security.cert.X509Certificate[] certs, String authType) {
			// }
			// public void checkServerTrusted(
			// java.security.cert.X509Certificate[] certs, String authType) {
			// }
			// }
			// };
			// try {
			// SSLContext sc = SSLContext.getInstance("SSL");
			// sc.init(null, trustAllCerts, new java.security.SecureRandom());
			// socket = (SSLSocket) sc.getSocketFactory().createSocket(host,
			// port);
			// socket.setSoTimeout(10000);
			// System.out.println("Starting SSL handshake...");
			// socket.startHandshake();
			// socket.close();
			// System.out.println();
			// System.out.println("No errors, certificate is already trusted");
			// } catch (GeneralSecurityException ee) {
			// ee.printStackTrace();
			// }
			///////////////////////
		}
		X509Certificate[] chain = tm.chain;

		if (chain == null) {
			System.out.println("Could not obtain server certificate chain");
			return null;
		}

		System.out.println();
		System.out.println("Server sent " + chain.length + " certificate(s):");
		System.out.println();
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		X509Certificate cert = chain[0];

		Certificado certificado = new Certificado();
		certificado.setCertificateList(Arrays.asList(chain));
		certificado.setNumCertsInChain(chain.length);
		certificado.setSubject(cert.getIssuerDN().getName());
		certificado.setFechaCaducidad(DateUtil.parse(cert.getNotAfter()));
		return certificado;
	}
}
