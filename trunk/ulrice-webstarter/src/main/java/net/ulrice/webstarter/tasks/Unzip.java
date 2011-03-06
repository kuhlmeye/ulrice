package net.ulrice.webstarter.tasks;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.ulrice.webstarter.ProcessContext;
import net.ulrice.webstarter.ProcessThread;

public class Unzip extends AbstractTask {

	public static final String FILTER_PARAM = "filter";
	public static final String URL_PARAM = "url";

	@Override
	public boolean doTask(ProcessThread thread) {
		String filter = getParameterAsString(FILTER_PARAM);
		String urlStr = getParameterAsString(URL_PARAM);
		String localDirStr = thread.getAppDescription().getLocalDir();
		
		URL fileUrl = null;
		if(urlStr != null) {
			try {
				fileUrl = new URL(urlStr);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		String file = fileUrl.getFile();
		String[] split = file.split("\\/");
		
		
		String fileName = split[split.length - 1];

		if (fileName.matches(filter)) {
			try {
				ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(localDirStr + fileName)));
				BufferedOutputStream dest = null;

				ZipEntry entry;
				
				
				while ((entry = zis.getNextEntry()) != null) {
					
					
					if(entry.isDirectory()) {
						thread.fireTaskProgressed(this, 0, entry.getName(), "Unzipping Directory " + entry.getName() + "...");
						File directory = new File(localDirStr + entry.getName());
						directory.mkdirs();
					} else {
						int count;
						byte data[] = new byte[1024];
						// write the files to the disk
						File localFile = new File(localDirStr, entry.getName());
						if(localFile.exists()) {
							long localLen = localFile.length();
							long zipLen = entry.getSize();
							
							if(localLen == zipLen) {
								thread.fireTaskProgressed(this, 100, entry.getName(), "Unzipping " + entry.getName() + "(" + entry.getSize() + "bytes) ... (Skipped)");
								continue;
							}
							
						}
						thread.fireTaskProgressed(this, 0, entry.getName(), "Unzipping " + entry.getName() + "(" + entry.getSize() + "bytes) ...");
						
						FileOutputStream fos = new FileOutputStream(localDirStr + entry.getName());
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
