package net.ulrice.webstarter.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.tasks.IFTask;

public class NetworkUtilities {


	public static void downloadFile(IFTask task, ProcessThread thread, URL fileUrl, String fileName, File localFile) throws IOException, FileNotFoundException {

        int downloaded = 0;
		long remoteFileLen;
		URLConnection con = fileUrl.openConnection();
		String reqCookieStr = thread.getContext().getCookieAsString();
		if (reqCookieStr != null) {
			con.setRequestProperty("Cookie", reqCookieStr);
		}
		con.connect();
		remoteFileLen = con.getContentLength();
		
		FileOutputStream fos = new FileOutputStream(localFile);
		try {
		    InputStream is = new BufferedInputStream(con.getInputStream(), 1024);

		    try {
		        byte[] responseBuffer = new byte[1024];
		        int read = 0;
		        while ((read = is.read(responseBuffer, 0, 1024)) > 0) {

		            downloaded += read;
		            int progress = (int) ((100.0 / remoteFileLen) * downloaded);
		            thread.fireTaskProgressed(task, progress, fileName, null);
		            fos.write(responseBuffer, 0, read);
		        }
		    }
		    finally {
		        is.close();
		    }
		    fos.flush();
		}
		finally {
		    fos.close();
		}
	}
}
