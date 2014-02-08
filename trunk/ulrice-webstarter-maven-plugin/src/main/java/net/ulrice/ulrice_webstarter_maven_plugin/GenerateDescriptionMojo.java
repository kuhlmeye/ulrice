package net.ulrice.ulrice_webstarter_maven_plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal create-description
 * 
 * @phase package
 */
public class GenerateDescriptionMojo extends AbstractMojo {

	/**
	 * Location of the files.
	 * 
	 * @parameter
	 * @required
	 */
	private List<File> directories;

	/**
	 * Reg-Ex used for filter the files that should be added to the description
	 * file.
	 * 
	 * @parameter
	 */
	private String[] filenameFilters;

	/**
	 * Name of the description file.
	 * 
	 * @parameter
	 * @required
	 */
	private String outputFilename;

	/**
	 * Base url
	 * 
	 * @parameter
	 */
	private URL baseUrl;

	/**
	 * Location where the files matching the filename pattern within the
	 * directories should be copied to
	 * 
	 * @parameter 
	 */
	private File targetDirectory;
	
	/**
	 * True, if an md5 fingerprint should be generated.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean useMd5;
	
	/**
	 * RegExp for files that should be compressed by pack200
	 * 
	 * @parameter default-value=""
	 */
	private String usePack200;
	
	/**
	 * Regexp for the files that should be uncompressed by zip after downloading.
	 * 
	 * @parameter default-value=""
	 */
	private String unzipFiles;
	
	/**
	 * Key values for the provided java runtime envrionments. The key will be used as the os attribute..
	 * 
	 * @parameter default-value=""
	 */
	private Properties providedJRE;

	public void execute() throws MojoExecutionException {

		File targetDir = targetDirectory;
		getLog().info("Target-Dir: " + targetDir);

		boolean copyFiles = true;

		if (targetDir == null) {
			copyFiles = false;
			getLog().info("Files wont be copied as targetDirectory is null!");
		}
		else if (!targetDir.exists()) {
			targetDir.mkdirs();
			getLog().info("Folder " + targetDirectory + " has been created, files will be copied!");
		}

		List<File> dirs = directories;

		for (File f : dirs) {
			if (!f.exists()) {
				f.mkdirs();
			}
		}

		File xmlDescrFile = new File(outputFilename + ".ws.xml");
		getLog().info("XML-File: " + xmlDescrFile.toString());
		
		File jsonDescrFile = new File(outputFilename + ".ws.json");
		getLog().info("JSON-File: " + jsonDescrFile.toString());
		
		PrintWriter xmlWriter = null;
		PrintWriter jsonWriter = null;

		try {
			xmlWriter = new PrintWriter(xmlDescrFile);
			jsonWriter = new PrintWriter(jsonDescrFile);			
			getLog().info("Writing to " + outputFilename);
			

			jsonWriter.println("{");
			jsonWriter.append("\"providedJREs\" : [");
			if(providedJRE != null) {
				for(String key : providedJRE.stringPropertyNames()) {
					jsonWriter.print("{");
					jsonWriter.print("\"os\" : \"" + key + "\" ");
					jsonWriter.print("\"name\" : \"" + providedJRE.getProperty(key) + "\"");
					jsonWriter.println("}");
				}					
			}
			jsonWriter.append("]");
			
			boolean fileFound = false;

			for (File f : dirs) {
				getLog().info("Scanning : " + f.getAbsolutePath());



				
				File[] files = f.listFiles();
				if (files != null) {
					for (File file : files) {
						String filename = file.getName();

						boolean matches = false;
						if (filenameFilters != null) {
							for (String filter : filenameFilters) {
								matches |= filename.matches(filter);
								if (matches) {
									break;
								}
							}
						} else {
							matches = true;
						}

						if (matches) {
							getLog().info("Processing: " + filename);
							if (!fileFound) {
								jsonWriter.append("\"tasklist\" : [");
								xmlWriter.println("<tasklist>");
								fileFound = true;
							}
							
							String md5 = null; 
							if(useMd5) {
								md5 = calculateMd5(file);
								getLog().debug("-MD5 for file: " + file + " is " + md5);
							}
							
							boolean pack200Used = false;						
							if(usePack200 != null && !"".equals(usePack200.trim()) && filename.matches(usePack200)) {
								getLog().debug("-Using pack200");
								file = pack200(file.getParentFile(), file);
								pack200Used = true;
							}

							
							String urlStr = null;
							if(baseUrl != null) {								
								URL url = new URL(baseUrl, file.getName());
								urlStr = url.toString();
							} else {
								urlStr = file.getName();
							}
							getLog().debug("-Adding file '" + file + "' as '" + urlStr + "'.");

							jsonWriter.print("{");
							jsonWriter.print("\"type\" : \"DownloadFile\" ");
							jsonWriter.print("\"classpath\" : \"true\" ");
							jsonWriter.print("\"url\" : \"" + urlStr + "\" ");
							jsonWriter.print("{type=\"DownloadFile\" classpath=\"true\" ");							
							jsonWriter.print("url=\"" + urlStr + "\" ");
							
							xmlWriter.print("<task type=\"DownloadFile\" classpath=\"true\" ");							
							xmlWriter.print("url=\"" + urlStr + "\" ");
							if (md5 != null && useMd5) {
								jsonWriter.print("\"md5\" : \"" + md5 + "\" ");
								xmlWriter.print("md5=\"" + md5 + "\" ");
							}		
							xmlWriter.print("pack200=\"" + pack200Used + "\" ");
							xmlWriter.print("length=\"" + file.length() + "\"");
							xmlWriter.println("/>");

							jsonWriter.print("\"pack200\" : \"" + pack200Used + "\" ");
							jsonWriter.print("\"length\" : \"" + file.length() + "\" ");
							jsonWriter.print("}");
							
							
							if(unzipFiles != null && !"".equals(unzipFiles.trim()) && filename.matches(unzipFiles)) {
								getLog().debug("-Using unzip files");							
								xmlWriter.print("<task type=\"Unzip\" filter=\".*\" url=\"" + urlStr + "\"/>");
								xmlWriter.print("{\"type\" : \"Unzip\" \"filter\" : \".*\" \"url\" : \"" + urlStr + "\"}");
							}
							
							if (copyFiles) {
								try {
									copyFile(file, targetDir);
								} catch (IOException e) {
									throw new MojoExecutionException("Error closing FileChannels copying file " + file.getAbsolutePath());
								}
							}
						}
					}
				}
			}
			if (fileFound) {
				jsonWriter.append("}");
				xmlWriter.println("</tasklist>");
			}
			jsonWriter.println("}");
		} catch (IOException e) {
			getLog().error("Error creating file " + xmlDescrFile, e);
			throw new MojoExecutionException("Error creating file " + xmlDescrFile, e);
		} catch (NoSuchAlgorithmException e) {
			getLog().error("Error creating md5 for file " + xmlDescrFile, e);
			throw new MojoExecutionException("Error creating md5 for file " + xmlDescrFile, e);
		} catch (Throwable th) {
			getLog().error("Error generating description file", th);
			throw new MojoExecutionException("Error generating description file", th);
		} finally {
			if (xmlWriter != null) {
				xmlWriter.close();
			}
			if (jsonWriter != null) {
				jsonWriter.close();
			}
		}
	}

	private File pack200(File targetDir, File file) throws IOException {
		File packedFile = new File(targetDir, file.getName().concat(".pack200"));
		try {
			JarFile jarFile = new JarFile(file);
			Packer packer = Pack200.newPacker();
			FileOutputStream os = new FileOutputStream(packedFile);
			packer.pack(jarFile, os);		
			return packedFile;
		} catch(IOException e) {
			getLog().info("Ignore packing file: " + file.getName());
		}
		return file;
	}

	private String calculateMd5(File file) throws NoSuchAlgorithmException, FileNotFoundException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(file);
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			is.close();
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);

			return bigInt.toString(16);

		} catch (IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}
	}

	private void copyFile(File file, File targetDir) throws IOException, MojoExecutionException {
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		try {
			File f = new File(targetDir.getAbsolutePath(), file.getName());
			inChannel = new FileInputStream(file).getChannel();
			outChannel = new FileOutputStream(f).getChannel();
			inChannel.transferTo(0, inChannel.size(), outChannel);
			getLog().debug("-Copy " + file.getName() + " to " + f.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException("Error creating file " + targetDir, e);
		} catch (IOException e) {
			throw new MojoExecutionException("Error writing to file " + targetDir, e);
		} finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}
}
