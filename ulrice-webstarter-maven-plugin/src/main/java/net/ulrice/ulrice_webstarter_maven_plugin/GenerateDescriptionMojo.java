package net.ulrice.ulrice_webstarter_maven_plugin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

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
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File directory;

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

	public void execute() throws MojoExecutionException {

		File f = directory;

		if (!f.exists()) {
			f.mkdirs();
		}

		File descrFile = outputFilename;
		PrintWriter pw = null;
		
		try {
			pw = new PrintWriter(descrFile);
			getLog().info("Writing to " + descrFile);

			File[] files = f.listFiles();
			if (files != null) {
				pw.println("<tasklist>");
				for (File file : files) {
					String filename = file.getName();
					
					boolean matches = false;
					if(filenameFilters != null) {
						for(String filter : filenameFilters) {
							matches |= filename.matches(filter);
							if(matches) {
								break;
							}
						}
					} else {
						matches = true;
					}
					
					if(matches) {
						URL url = new URL(baseUrl, filename);					
						getLog().info("-Adding file '" + file + "' as '" + url + "'.");					
						pw.println("<task type=\"DownloadFile\" classpath=\"true\" url=\"" + url + "\"/>");
					}
				}
				pw.println("</tasklist>");
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Error creating file " + descrFile, e);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}
}
