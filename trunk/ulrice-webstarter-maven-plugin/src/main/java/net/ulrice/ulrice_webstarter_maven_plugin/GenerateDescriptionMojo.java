package net.ulrice.ulrice_webstarter_maven_plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.List;

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
	private File outputFilename;

	/**
	 * Base url
	 * 
	 * @parameter
	 * @required
	 */
	private URL baseUrl;

	/**
	 * Location where the files matching the filename
	 * pattern within the directories should be copied to
	 * 
	 * @parameter
	 */
	private File targetDirectory;

	public void execute() throws MojoExecutionException {
		
		File targetDir = targetDirectory;
		
		boolean copyFiles = true;
		
		if (targetDir != null && !targetDir.exists()) {
			targetDir.mkdirs();
		}
		else {
			copyFiles = false;
		}

		List<File> dirs = directories;

		for (File f : dirs) {
			if (!f.exists()) {
				f.mkdirs();
			}
		}

		File descrFile = outputFilename;
		PrintWriter pw = null;

		try {
			pw = new PrintWriter(descrFile);
			getLog().info("Writing to " + descrFile);

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
							if (!fileFound) {
								pw.println("<tasklist>");
								fileFound = true;
							}
							URL url = new URL(baseUrl, filename);
							getLog().info(
									"-Adding file '" + file + "' as '" + url
											+ "'.");
							pw
									.println("<task type=\"DownloadFile\" classpath=\"true\" url=\""
											+ url + "\"/>");
							if (copyFiles) {
								try {
									copyFile(file, targetDir);
								}
								catch (IOException e) {
									throw new MojoExecutionException("Error closing FileChannels copying file " + file.getAbsolutePath());
								}
							}
						}
					}
				}
			}
			if (fileFound) {
				pw.println("</tasklist>");
			}
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Error creating file " + descrFile, e);
		} finally {
			if (pw != null) {
				pw.close();
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
			getLog().info("Copy " + file.getName() + " to " + f.getAbsolutePath());
		} catch (FileNotFoundException e) {
			throw new MojoExecutionException(
					"Error creating file " + targetDir, e);
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Error writing to file " + targetDir, e);
		}
		finally {
			if (inChannel != null) {
				inChannel.close();
			}
			if (outChannel != null) {
				outChannel.close();
			}
		}
	}
}
