package net.ulrice.webstarter.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class DownloadFile extends AbstractTask {

	private static final String LOCAL_DIR = "localDir";

	@Override
	public void doTask(ProcessThread thread) {
		executeTask(thread);
	}

	@Override
	public void executeTask(ProcessThread thread) {
		URL fileUrl = (URL) thread.getContext().getValue(ProcessContext.CURRENT_FILE_URL);
		String fileName = thread.getContext().getValueAsString(ProcessContext.CURRENT_FILE_NAME);

		thread.fireTaskStarted(this, fileName );

		
		String localDirString = getParameter(LOCAL_DIR);
		try {
			
			URLConnection con = fileUrl.openConnection();
			String cookieString = thread.getContext().getValueAsString(ProcessContext.COOKIE);
			if (cookieString != null) {
				con.setRequestProperty("Cookie", cookieString);
			}
			con.connect();
			
			String length = con.getHeaderField("Content-Length");
			String md5 = con.getHeaderField("Content-MD5");
			String type = con.getHeaderField("Content-Type");
			
			File localDir = new File(localDirString);
			localDir.mkdirs();

			thread.fireTaskProgressed(this, "");
			
			if(localDir.isDirectory() && localDir.canWrite()) {
				FileOutputStream fos = new FileOutputStream(localDirString + File.separator + fileName);
				InputStream is = new BufferedInputStream(con.getInputStream(), 1024);
				byte[] responseBuffer = new byte[1024];
				while (is.read(responseBuffer, 0, 1024) > 0) {
					fos.write(responseBuffer);
				}
				fos.flush();
				fos.close();
			}


			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		
		thread.fireTaskFinished(this);
		}
	}
}
