package net.ulrice.webstarter.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.TaskDescription;
import net.ulrice.webstarter.util.WebstarterUtils;

public class DownloadFile extends AbstractTask {

	private static final Logger LOG = Logger.getLogger(DownloadFile.class.getName());
	public static final String URL_PARAM = "url";
	public static final String MD5_PARAM = "md5";
	public static final String PACK200_PARAM = "pack200";
	private static final String BASE_URL_PARAM_NAME = "baseUrl";

	public static final String CLASSPATH_PARAM = "classpath";

	@Override
	public boolean doTask(ProcessThread thread) {

		String baseUrlString = getParameterAsString(BASE_URL_PARAM_NAME);
		String urlStr = getParameterAsString(URL_PARAM);
		String remoteMd5 = getParameterAsString(MD5_PARAM);
		boolean usePack200 = Boolean.valueOf(getParameterAsString(PACK200_PARAM));

		if (baseUrlString != null) {
			urlStr = baseUrlString + urlStr;
		}

		URL fileUrl = null;
		try {
			fileUrl = new URL(urlStr);
		} catch (MalformedURLException e) {
			LOG.log(Level.SEVERE, "Error creating url from file. ", e);
			thread.handleError(this, "Error creating url from file.", "Error creating url from file: " + e.getMessage());
		}

		String file = fileUrl.getFile();
		String[] split = file.split("\\/");

		String fileName = split[split.length - 1];

		String localFileName = fileName;

		if (usePack200 && localFileName.endsWith(".pack200")) {
			localFileName = localFileName.substring(0, localFileName.length() - ".pack200".length());
		}

		String localDirString = WebstarterUtils.resolvePlaceholders(thread.getAppDescription().getLocalDir());
		try {

			URLConnection con = fileUrl.openConnection();
			String reqCookieStr = thread.getContext().getCookieAsString();
			if (reqCookieStr != null) {
				con.setRequestProperty("Cookie", reqCookieStr);
			}
			con.connect();
			
			long remoteFileLen = con.getContentLength();
			int downloaded = 0;

			File localDir = new File(localDirString);
			localDir.mkdirs();

			File localFile = new File(localDir, localFileName);

			String localMd5 = null;
			try {
				localMd5 = calculateMd5(localFile);
			} catch (NoSuchAlgorithmException e) {

			} catch (FileNotFoundException e) {

			}

			
			Properties properties = thread.getContext().getPersistentProperties();
			String remoteMd5Key = localFileName + "#remote";
			String localMd5Key = localFileName + "#local";
			String savedRemoteMd5 = properties.getProperty(remoteMd5Key, null);
			String savedLocalMd5 = properties.getProperty(localMd5Key, null);

			boolean skipDownload = false;
			if (localFile.exists()) {
				if (remoteMd5 != null && savedRemoteMd5 != null && localMd5 != null && savedLocalMd5 != null) {
					skipDownload = remoteMd5.equals(savedRemoteMd5) && localMd5.equals(savedLocalMd5);
				} else {
					long localFileLen = localFile.length();
					if (localFileLen == remoteFileLen) {
						// Skip file. It already exists.
						thread.fireTaskProgressed(this, 100, fileName, "Downloading " + fileName + "... (skipped)");
						skipDownload = true;
					}
				}
			}

			if (!skipDownload) {
				thread.fireTaskProgressed(this, 0, fileName, "Downloading " + fileName + "...");

				if (localDir.isDirectory() && localDir.canWrite()) {

					FileOutputStream fos = null;
					File tempFile = null;
					if (usePack200 && urlStr.endsWith(".pack200")) {
						tempFile = File.createTempFile("downloader", ".pack200");
					} else {
						tempFile = localFile;
					}
					fos = new FileOutputStream(tempFile);

					InputStream is = new BufferedInputStream(con.getInputStream(), 1024);
					byte[] responseBuffer = new byte[1024];
					int read = 0;
					while ((read = is.read(responseBuffer, 0, 1024)) > 0) {

						downloaded += read;
						int progress = (int) (100.0 / remoteFileLen * downloaded);
						thread.fireTaskProgressed(this, progress, fileName, null);
						fos.write(responseBuffer, 0, read);
					}
					fos.flush();
					fos.close();

					if (usePack200 && urlStr.endsWith(".pack200")) {
						unpack200(tempFile, localFile);
					}

					if(remoteMd5 != null) {
						properties.setProperty(remoteMd5Key, remoteMd5);
					}
					try {
						localMd5 = calculateMd5(localFile);
						properties.setProperty(localMd5Key, localMd5);
					} catch (NoSuchAlgorithmException e) {

					} catch (FileNotFoundException e) {

					}
					
				}
			} else {
				thread.fireTaskProgressed(this, 100, fileName, "Downloading " + fileName + "... (skipped)");
			}

			if (Boolean.valueOf(getParameterAsString(CLASSPATH_PARAM, "false"))) {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put(AddToClasspath.FILTER_PARAM, ".*");
				parameters.put(AddToClasspath.URL_PARAM, localFile.toURI().toURL().toString());
				TaskDescription classpathTask = new TaskDescription(AddToClasspath.class, parameters);
				IFTask instanciateTask = classpathTask.instanciateTask();
				instanciateTask.doTask(thread);
			}

		} catch (IOException e) {
			LOG.log(Level.SEVERE, "IO exception during file download.", e);
		} catch (InstantiationException e) {
			LOG.log(Level.SEVERE, "Instanciation exception during file download.", e);
		} catch (IllegalAccessException e) {
			LOG.log(Level.SEVERE, "Access exception during file download.", e);
		} finally {

			thread.fireTaskFinished(this);
		}
		return true;
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

	private void unpack200(File inFile, File unpackedFile) throws IOException {
		try {
			FileOutputStream fostream = new FileOutputStream(unpackedFile);
			JarOutputStream jostream = new JarOutputStream(fostream);
			Unpacker unpacker = Pack200.newUnpacker();
			unpacker.unpack(inFile, jostream);
			jostream.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "IO Exception unpacking file.", e);
		}
	}
}
