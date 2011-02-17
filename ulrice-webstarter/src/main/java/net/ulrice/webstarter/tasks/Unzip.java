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
	public boolean doTask(ProcessThread thread) {
		String filter = getParameterAsString(FILTER_NAME);
		String fileName = getParameterAsString(ReadDescription.CURRENT_FILE_NAME);

		if (fileName.matches(filter)) {
			try {
				String localPath = getParameterAsString(ReadDescription.LOCAL_PATH);
				ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(localPath + fileName)));
				BufferedOutputStream dest = null;

				ZipEntry entry;
				
				
				while ((entry = zis.getNextEntry()) != null) {
					
					thread.fireTaskProgressed(this, 0, entry.getName(), "Unzipping " + entry.getName() + "(" + entry.getSize() + "bytes) ...");
					
					if(entry.isDirectory()) {
						File directory = new File(localPath + entry.getName());
						directory.mkdirs();
					} else {
						int count;
						byte data[] = new byte[1024];
						// write the files to the disk
						FileOutputStream fos = new FileOutputStream(localPath + entry.getName());
						dest = new BufferedOutputStream(fos, 1024);
						

						Long length = Long.valueOf(entry.getSize());
						Long downloaded = 0l;
						
						while ((count = zis.read(data, 0, 1024)) != -1) {

							downloaded += count;
							int progress = (int)(100.0/length * downloaded);
							thread.fireTaskProgressed(this, progress,  entry.getName(), null);
							
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
		return true;
	}


}
