package net.ulrice.webstarter.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class Unzip extends AbstractTask {

	private static final String FILTER_NAME = "filter";

	@Override
	public void executeTask(ProcessThread thread) {
		String filter = getParameter(FILTER_NAME);
		String fileName = thread.getContext().getValueAsString(ProcessContext.CURRENT_FILE_NAME);

		if (fileName.matches(filter)) {
			try {
				String localPath = "D:/TEMP/";
				ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(localPath + fileName)));
				BufferedOutputStream dest = null;

				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					
					if(entry.isDirectory()) {
						File directory = new File(localPath + entry.getName());
						directory.mkdirs();
					} else {
						int count;
						byte data[] = new byte[1024];
						// write the files to the disk
						FileOutputStream fos = new FileOutputStream(localPath + entry.getName());
						dest = new BufferedOutputStream(fos, 1024);
						while ((count = zis.read(data, 0, 1024)) != -1) {
							dest.write(data, 0, count);
						}
						dest.flush();
						dest.close();
					}
					

				}
				zis.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
