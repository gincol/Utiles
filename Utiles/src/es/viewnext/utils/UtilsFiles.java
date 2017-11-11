package es.viewnext.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class UtilsFiles {
	/**
	 * Creación de una carpeta no visible y no editable
	 * 
	 * @param path
	 */
	public static void createFolder(String path) {
		Path folder = Paths.get(path);
		try {
			if (!Files.exists(folder)) {
				Files.createDirectories(folder);
				Files.setAttribute(folder, "dos:hidden", true);
				Files.setAttribute(folder, "dos:readonly", true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Comprueba si un cierto fichero existe. Tiene en cuenta la carpeta de
	 * usuario, por lo que no es necesario que venga en los parámetros
	 * 
	 * @param temporada
	 *            String en formato "XXYY" por ejemplo "1516"
	 * @param concepto
	 *            String con el nombre del
	 * @return true si existe, false en otro caso
	 */
	public static boolean folderHasSubfolder(String temporada) {
		File file = null;
		String filePath = "";
		if (temporada != null && !temporada.trim().isEmpty()) {
			filePath = temporada;
			file = new File(filePath);
			for (File fichero : file.listFiles()) {
				if (fichero.isDirectory())
					return true;
			}
		}
		return false;
	}

	/**
	 * Permite eliminar una carpeta y sus subdirectorios
	 * 
	 * @param path
	 */
	public static List<File> recorreFolder(String path) {
		Path folder = Paths.get(path);
		List<File> listaFicheros = new ArrayList<File>();
		try {
			Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					listaFicheros.add(file.toFile());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listaFicheros;
	}

	public static List<File> recorreWar(String path) {
		List<File> listaFicheros = new ArrayList<File>();
		try (ZipFile zipFile = new ZipFile(path)) {
		    Enumeration zipEntries = zipFile.entries();
		    while (zipEntries.hasMoreElements()) {
		        String fileName = ((ZipEntry) zipEntries.nextElement()).getName();
		        if(fileName.endsWith(".jar")){
//		        	listaFicheros = recorreFolder(fileName);
//		        	listaFicheros.addAll(recorreFolder(fileName));
//		        	listaFicheros.add(new File(fileName));
		        }
		        listaFicheros.add(new File(fileName));
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listaFicheros;
	}
	
	public static int getNumFilesFolder(String path) {
		int contador = 0;
		ZipInputStream zip;
		try {
			zip = new ZipInputStream(new FileInputStream(path));
			while (true) {
				ZipEntry e = zip.getNextEntry();
				if (e == null) {
					break;
				} else if (!e.isDirectory()) {
					contador++;
				}
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return contador;
	}

	/**
	 * Permite eliminar una carpeta y sus subdirectorios
	 * 
	 * @param path
	 */
	public static boolean deleteFolder(String path) {
		boolean retorno = true;
		Path folder = Paths.get(path);
		try {
			Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					UtilsFiles.setVisible(file);
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					UtilsFiles.setVisible(dir);
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			retorno = false;
			e.printStackTrace();
		}
		return retorno;
	}

	public static boolean deleteFile(File file) {
		boolean retorno = true;
		try {
			Files.delete(Paths.get(file.getPath()));
		} catch (IOException e) {
			retorno = false;
			e.printStackTrace();
		}
		return retorno;
	}

	/**
	 * Creación de un fichero no visible y no editable
	 * 
	 * @param path
	 */
	public static File createFile(String path) {
		Path file = Paths.get(path);
		try {
			if (!Files.exists(file.getParent())) {
				createFolder(file.getParent().toString());
				Files.setAttribute(file.getParent(), "dos:hidden", true);
				Files.setAttribute(file.getParent(), "dos:readonly", true);
			}

			Files.createFile(file);
			Files.setAttribute(file, "dos:hidden", true);
			Files.setAttribute(file, "dos:readonly", true);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return file.toFile();
	}

	/**
	 * Creación de un fichero no visible y no editable
	 * 
	 * @param path
	 */
	public static File createFile(File path) {
		return createFile(path.getPath());
	}

	/**
	 * Permite hacer un fichero o carpeta visible y editable
	 * 
	 * @param path
	 *            Path con la ruta del fichero o carpeta
	 */
	public static void setVisible(Path path) {
		try {
			Files.setAttribute(path, "dos:hidden", false);
			Files.setAttribute(path, "dos:readonly", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permite hacer un fichero o carpeta visible y editable
	 * 
	 * @param path
	 *            String con la ruta del fichero o carpeta
	 */
	public static void setVisible(String path) {
		Path ruta = Paths.get(path);
		setVisible(ruta);
	}

	/**
	 * Permite hacer un fichero o carpeta no visible y no editable
	 * 
	 * @param path
	 *            Path del fichero o carpeta
	 */
	public static void setHidden(Path path) {
		try {
			Files.setAttribute(path, "dos:hidden", true);
			Files.setAttribute(path, "dos:readonly", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

}
