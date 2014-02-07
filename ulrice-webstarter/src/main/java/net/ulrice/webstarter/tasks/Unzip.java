package net.ulrice.webstarter.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.util.WebstarterUtils;

/**
 * Task for unzipping downloaded files.
 * 
 * @author christof
 */
public class Unzip extends AbstractTask {

    /** The logger used by this class. */
    private static final Logger LOG = Logger.getLogger(Unzip.class.getName());

    /** The reg exp filter string used to identify files to unzip. */
    public static final String FILTER_PARAM = "filter";

    /** The url of the cadidate file. */
    public static final String URL_PARAM = "url";

    private static final String BASE_URL_PARAM_NAME = "baseUrl";

    // TODO Unzip Type => Everytime, Checkdir
    
    @Override
    public boolean doTask(ProcessThread thread) {
        String filter = getParameterAsString(FILTER_PARAM);
        String urlStr = getParameterAsString(URL_PARAM);
        String localDirStr = WebstarterUtils.resolvePlaceholders(thread.getAppDescription().getLocalDir());
        String baseUrlString = getParameterAsString(BASE_URL_PARAM_NAME);
        if (baseUrlString != null) {
            urlStr = baseUrlString + urlStr;
        }

        URL fileUrl = null;
        if (urlStr != null) {
            try {
                fileUrl = new URL(urlStr);
            }
            catch (MalformedURLException e) {
                thread.handleError(this, "Malformed url.", "Malformed url: " + urlStr);
                LOG.log(Level.SEVERE, "Malformed url: " + urlStr, e);
            }
        }

        String file = fileUrl.getFile();
        String[] split = file.split("\\/");

        String fileName = split[split.length - 1];

        if (fileName.matches(filter)) {
            try {
                ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(localDirStr + fileName)));
                try {
                    BufferedOutputStream dest = null;

                    ZipEntry entry;

                    while ((entry = zis.getNextEntry()) != null) {

                        if (entry.isDirectory()) {
                            thread.fireTaskProgressed(this, 0, entry.getName(), "Unzipping Directory " + entry.getName() + "...");
                            File directory = new File(localDirStr + entry.getName());
                            directory.mkdirs();
                        }
                        else {
                            int count;
                            byte data[] = new byte[1024];
                            // write the files to the disk
                            File localFile = new File(localDirStr, entry.getName());
                            if (localFile.exists()) {
                                long localLen = localFile.length();
                                long zipLen = entry.getSize();

                                if (localLen == zipLen) {
                                    thread.fireTaskProgressed(this, 100, entry.getName(), "Unzipping " + entry.getName() + "(" + entry.getSize() + "bytes) ... (Skipped)");
                                    continue;
                                }

                            }
                            thread.fireTaskProgressed(this, 0, entry.getName(), "Unzipping " + entry.getName() + " (" + entry.getSize() + "bytes) ...");

                            dest = new BufferedOutputStream(new FileOutputStream(localDirStr + entry.getName()), 1024);

                            try {
                                Long length = Long.valueOf(entry.getSize());
                                Long downloaded = 0l;

                                while ((count = zis.read(data, 0, 1024)) != -1) {

                                    downloaded += count;
                                    int progress = (int) ((100.0 / length) * downloaded);
                                    thread.fireTaskProgressed(this, progress, entry.getName(), null);

                                    dest.write(data, 0, count);
                                }

                                dest.flush();
                            }
                            finally {
                                dest.close();
                            }
                        }

                    }
                }
                finally {
                    zis.close();
                }
            }
            catch (FileNotFoundException e) {
                thread.handleError(this, "File not found.", "File not found:" + fileName);
                LOG.log(Level.SEVERE, "File not found:" + fileName, e);
            }
            catch (IOException e) {
                thread.handleError(this, "IO exception.", "IO exception during unzipping file from url: " + urlStr);
                LOG.log(Level.SEVERE, "IO exception during unzipping file from url: " + urlStr, e);
            }
        }
        return true;
    }

}
