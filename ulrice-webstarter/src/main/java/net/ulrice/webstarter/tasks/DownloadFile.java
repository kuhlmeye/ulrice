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
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.TaskDescription;

public class DownloadFile extends AbstractTask {

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
		

		if(baseUrlString != null) {
			urlStr = baseUrlString + urlStr;
		}
		
		URL fileUrl = null;
		try {
			fileUrl = new URL(urlStr);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String file = fileUrl.getFile();
		String[] split = file.split("\\/");

		String fileName = split[split.length - 1];
		
		String localFileName = fileName;
		if(usePack200 && localFileName.endsWith(".pack200")) {
			localFileName = localFileName.substring(0, localFileName.length() - ".pack200".length());
		}
		
		String localDirString = thread.getAppDescription().getLocalDir();
		try {

			URLConnection con = fileUrl.openConnection();
			String reqCookieStr = thread.getContext().getCookieAsString();
			if (reqCookieStr != null) {
				con.setRequestProperty("Cookie", reqCookieStr);
			}
			con.connect();

			String lengthStr = con.getHeaderField("Content-Length");
			
			

			Long length = Long.valueOf(lengthStr);
			Long downloaded = 0l;

			File localDir = new File(localDirString);
			localDir.mkdirs();

			File localFile = new File(localDir, localFileName);
			String localMd5 = null;
			try {
				if(localFile.exists()) {
					localMd5 = calculateMd5(localFile);
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			boolean skipDownload = false;
			if (localFile.exists()) {
				if(remoteMd5 != null && localMd5 != null) {
					skipDownload = remoteMd5.equals(localMd5);
				} else {
					long localFileLen = localFile.length();
					long remoteFileLen = Long.valueOf(lengthStr);
					if (localFileLen == remoteFileLen) {
						// Skip file. It already exists.
						thread.fireTaskProgressed(this, 100, fileName, "Downloading " + fileName + "...(skipped)");
						skipDownload = true;
					}
				}
			}

			if (!skipDownload) {
				thread.fireTaskProgressed(this, 0, fileName, "Downloading " + fileName + "...");

				if (localDir.isDirectory() && localDir.canWrite()) {

					FileOutputStream fos = null;
					File tempFile = null;
					if(usePack200 && urlStr.endsWith(".pack200")) {
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
						int progress = (int) (100.0 / length * downloaded);
						thread.fireTaskProgressed(this, progress, fileName, null);
						fos.write(responseBuffer, 0, read);
					}
					fos.flush();
					fos.close();
					
					if(usePack200 && urlStr.endsWith(".pack200")) {
						unpack200(tempFile, localFile);
					}
				}
			}

			if (Boolean.valueOf(getParameterAsString(CLASSPATH_PARAM, "false"))) {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put(AddToClasspath.FILTER_PARAM, ".*");
				parameters.put(AddToClasspath.URL_PARAM, localFile.toURI().toURL().toString());
				TaskDescription classpathTask = new TaskDescription(AddToClasspath.class, parameters);
				thread.addSubTasks(this, classpathTask.instanciateTask());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			thread.fireTaskFinished(this);
		}
		return true;
	}
	

	private void unpack200(File inFile, File unpackedFile) throws IOException {
		try {
	        FileOutputStream fostream = new FileOutputStream(unpackedFile);
	        JarOutputStream jostream = new JarOutputStream(fostream);
	        Unpacker unpacker = Pack200.newUnpacker();
	        unpacker.unpack(inFile, jostream);
	        jostream.close();	
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private String calculateMd5(File file) throws NoSuchAlgorithmException, FileNotFoundException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(file);				
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			is.close();
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			return bigInt.toString(16);
			
		}
		catch(IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}
	}

}
