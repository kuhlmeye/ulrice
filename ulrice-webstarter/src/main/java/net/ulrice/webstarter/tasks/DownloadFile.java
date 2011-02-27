package net.ulrice.webstarter.tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import net.ulrice.webstarter.ProcessThread;
import net.ulrice.webstarter.TaskDescription;

public class DownloadFile extends AbstractTask {

	public static final String URL_PARAM = "url";

	public static final String CLASSPATH_PARAM = "classpath";

	@Override
	public boolean doTask(ProcessThread thread) {

		String urlStr = getParameterAsString(URL_PARAM);
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
		String localDirString = thread.getAppDescription().getLocalDir();
		try {

			URLConnection con = fileUrl.openConnection();
			String cookieString = thread.getContext().getCookieAsString();
			if (cookieString != null) {
				con.setRequestProperty("Cookie", cookieString);
			}
			con.connect();

			String lengthStr = con.getHeaderField("Content-Length");

			Long length = Long.valueOf(lengthStr);
			Long downloaded = 0l;

			File localDir = new File(localDirString);
			localDir.mkdirs();

			File localFile = new File(localDir, fileName);
			boolean skipDownload = false;
			if (localFile.exists()) {
				long localFileLen = localFile.length();
				long remoteFileLen = Long.valueOf(lengthStr);
				if (localFileLen == remoteFileLen) {
					// Skip file. It already exists.
					thread.fireTaskProgressed(this, 100, fileName, "Downloading " + fileName + "...(skipped)");
					skipDownload = true;
				}
			}

			if (!skipDownload) {
				thread.fireTaskProgressed(this, 0, fileName, "Downloading " + fileName + "...");

				if (localDir.isDirectory() && localDir.canWrite()) {
					FileOutputStream fos = new FileOutputStream(localFile);
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
				}
			}

			if (Boolean.valueOf(getParameterAsString(CLASSPATH_PARAM, "false"))) {
				Map<String, String> parameters = new HashMap<String, String>();
				parameters.put(AddToClasspath.FILTER_PARAM, ".*");
				parameters.put(AddToClasspath.URL_PARAM, urlStr);
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
}
