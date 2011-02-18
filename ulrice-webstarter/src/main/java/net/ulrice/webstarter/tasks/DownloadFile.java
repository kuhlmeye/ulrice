package net.ulrice.webstarter.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class DownloadFile extends AbstractTask {



	@Override
	public boolean doTask(ProcessThread thread) {
		URL fileUrl = (URL) getParameter(ReadDescription.CURRENT_FILE_URL);
		String fileName = getParameterAsString(ReadDescription.CURRENT_FILE_NAME);


		String localDirString = getParameterAsString(ReadDescription.LOCAL_PATH);
		try {
			
			URLConnection con = fileUrl.openConnection();
			String cookieString = thread.getContext().getCookieAsString();
			if (cookieString != null) {
				con.setRequestProperty("Cookie", cookieString);
			}
			con.connect();
			
			String lengthStr = con.getHeaderField("Content-Length");
			String md5 = con.getHeaderField("Content-MD5");
			String type = con.getHeaderField("Content-Type");
			
			Long length = Long.valueOf(lengthStr);
			Long downloaded = 0l;
			
			File localDir = new File(localDirString);
			localDir.mkdirs();

			thread.fireTaskProgressed(this, 0, fileName, "Downloading " + fileName + "...");
			
			if(localDir.isDirectory() && localDir.canWrite()) {
				File localFile = new File(localDirString + fileName);
				FileOutputStream fos = new FileOutputStream(localFile);
				InputStream is = new BufferedInputStream(con.getInputStream(), 1024);
				byte[] responseBuffer = new byte[1024];
				int read = 0;
				while ((read = is.read(responseBuffer, 0, 1024)) > 0) {
					
					downloaded += read;
					int progress = (int)(100.0/length * downloaded);
					thread.fireTaskProgressed(this, progress, fileName, null);
					fos.write(responseBuffer, 0, read);
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
		return true;
	}
}
