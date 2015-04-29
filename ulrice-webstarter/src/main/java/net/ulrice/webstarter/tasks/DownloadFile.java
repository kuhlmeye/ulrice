package net.ulrice.webstarter.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.ProvidedJRE;
import net.ulrice.webstarter.TaskDescription;
import net.ulrice.webstarter.net.NetworkUtilities;
import net.ulrice.webstarter.util.WebstarterUtils;

public class DownloadFile extends AbstractTask {

    private static final Logger LOG = Logger.getLogger(DownloadFile.class.getName());
    public static final String URL_PARAM = "url";
    public static final String MD5_PARAM = "md5";
    public static final String PACK200_PARAM = "pack200";
    private static final String BASE_URL_PARAM_NAME = "baseUrl";
    private static final String LENGTH_PARAM_NAME = "length";

    public static final String CLASSPATH_PARAM = "classpath";

    @Override
    public boolean doTask(ProcessThread thread) {
        Set<ProvidedJRE> providedJRESet = thread.getAppDescription().getProvidedJRESet();
        if(providedJRESet != null) {
        	for(ProvidedJRE providedJRE : providedJRESet) {
        		if(providedJRE.getFilename().equals(getParameterAsString(URL_PARAM))) {
        			providedJRE.setDownloadTask(this);
        			return true;
        		}        		
        	}
        }

        return downloadFileInternal(thread, 3);
    }


	private boolean downloadFileInternal(ProcessThread thread, int retry) {
		try {			
        	downloadFile(thread);
        	return true;
        }
		catch (SocketException e) {
			if(retry >= 0) {
				return downloadFileInternal(thread, retry - 1);
			} else {
	            LOG.log(Level.SEVERE, "IO exception during file download.", e);
	            thread.handleError(this, "File not found", "Could not find the file " + getUrl());
			}
		}
        catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, "IO exception during file download.", e);
            thread.handleError(this, "File not found", "Could not find the file " + getUrl());
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, "IO exception during file download.", e);
            thread.handleError(this, "Exception during file download", "Exception downloading file " + getUrl());
        }
        catch (InstantiationException e) {
            LOG.log(Level.SEVERE, "Instanciation exception during file download.", e);
            thread.handleError(this, "Exception during file download", "Exception downloading file " + getUrl());
        }
        catch (IllegalAccessException e) {
            LOG.log(Level.SEVERE, "Access exception during file download.", e);
            thread.handleError(this, "Exception during file download", "Exception downloading file " + getUrl());
        }
        catch (Throwable th) {
            LOG.log(Level.SEVERE, "Exception during file download.", th);
            thread.handleError(this, "Exception during file download", "Exception downloading file " + getUrl());
        }
        finally {
            thread.fireTaskFinished(this);
        }
		return false;
	}


	public boolean downloadFile(ProcessThread thread) throws MalformedURLException, IOException, InstantiationException, IllegalAccessException {

        String remoteMd5 = getParameterAsString(MD5_PARAM);
        boolean usePack200 = Boolean.valueOf(getParameterAsString(PACK200_PARAM));        
        
        long remoteFileLen = -1;
        try {
        	remoteFileLen = Long.valueOf(getParameterAsString(LENGTH_PARAM_NAME));
        } catch(NumberFormatException e) {
        }
        
		String urlStr = getUrl();

        URL fileUrl = new URL(urlStr);

        String file = fileUrl.getFile();
        String[] split = file.split("\\/");

        String fileName = split[split.length - 1];

        String localFileName = fileName;

        if (usePack200 && localFileName.endsWith(".pack200")) {
            localFileName = localFileName.substring(0, localFileName.length() - ".pack200".length());
        }

        String localDirString = WebstarterUtils.resolvePlaceholders(thread.getAppDescription().getLocalDir());

        File localDir = new File(localDirString);
            localDir.mkdirs();

            File localFile = new File(localDir, localFileName);

            String localMd5 = null;
            try {
                localMd5 = calculateMd5(localFile);
            }
            catch (NoSuchAlgorithmException e) {

            }
            catch (FileNotFoundException e) {

            }

            Properties properties = thread.getContext().getPersistentProperties();
            String remoteMd5Key = localFileName + "#remote";
            String localMd5Key = localFileName + "#local";
            String savedRemoteMd5 = properties.getProperty(remoteMd5Key, null);
            String savedLocalMd5 = properties.getProperty(localMd5Key, null);

            boolean skipDownload = false;
            if (localFile.exists()) {
                if ((remoteMd5 != null) && (savedRemoteMd5 != null) && (localMd5 != null) && (savedLocalMd5 != null)) {
                    skipDownload = remoteMd5.equals(savedRemoteMd5) && localMd5.equals(savedLocalMd5);
                }
                else {
                    long localFileLen = localFile.length();
                    if (localFileLen == remoteFileLen) {
                        // Skip file. It already exists.
                        thread.fireTaskProgressed(this, 100, fileName, "Downloading " + fileName + "... (skipped)");
                        skipDownload = true;
                    }
                }
            }

            if (!skipDownload) {
                thread.getContext().setFilesDownloaded(true);

                thread.fireTaskProgressed(this, 0, fileName, "Downloading " + fileName + "...");

                if (localDir.isDirectory() && localDir.canWrite()) {

                    File tempFile = null;
                    if (usePack200 && urlStr.endsWith(".pack200")) {
                        tempFile = File.createTempFile("downloader", ".pack200");
                    }
                    else {
                        tempFile = localFile;
                    }

					NetworkUtilities.downloadFile(this, thread, fileUrl, fileName, tempFile);

                    if (usePack200 && urlStr.endsWith(".pack200")) {
                        unpack200(tempFile, localFile);
                    }

                    if (remoteMd5 != null) {
                        properties.setProperty(remoteMd5Key, remoteMd5);
                    }
                    try {
                        localMd5 = calculateMd5(localFile);
                        properties.setProperty(localMd5Key, localMd5);
                    }
                    catch (NoSuchAlgorithmException e) {

                    }
                    catch (FileNotFoundException e) {

                    }

                }
            }
            else {
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

            return !skipDownload;
	}



    private String calculateMd5(File file) throws NoSuchAlgorithmException, FileNotFoundException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[8192];
        int read = 0;
        InputStream is = new FileInputStream(file);
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            is.close();
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);

            return bigInt.toString(16);

        }
        catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
            }
        }
    }

    private void unpack200(File inFile, File unpackedFile) throws IOException {
        try {
            JarOutputStream jostream = new JarOutputStream(new FileOutputStream(unpackedFile));

            try {
                Unpacker unpacker = Pack200.newUnpacker();
                unpacker.unpack(inFile, jostream);
            }
            finally {
                jostream.close();
            }
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, "IO Exception unpacking file.", e);
        }
    }



	public String getUrl() {
		String urlStr = getParameterAsString(URL_PARAM);
        String baseUrlString = getParameterAsString(BASE_URL_PARAM_NAME);
        if (baseUrlString != null) {
            urlStr = baseUrlString + urlStr;
        }
		return urlStr;
	}
}
