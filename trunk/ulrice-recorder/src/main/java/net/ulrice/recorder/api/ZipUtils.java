package net.ulrice.recorder.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.ulrice.Ulrice;

class ZipUtils {

	static void decompressFile(File file, File outputDirectory, String... extensionsToDecompress) throws IOException {
		Set<String> extensions = new HashSet<String>(Arrays.asList(extensionsToDecompress));
		
		ZipFile zipFile = new ZipFile(file);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();			

			int dotIdx = entry.getName().lastIndexOf('.');
			if(dotIdx >= 0) {
				String extension = entry.getName().substring(dotIdx + 1);
				if(!extensions.contains(extension)) {
					continue;
				}
			}
			
			
			if (entry.isDirectory()) {
				(new File(entry.getName())).mkdirs();
				continue;
			}
			
		    byte[] buffer = new byte[1024];
		    InputStream in = zipFile.getInputStream(entry);
		    OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outputDirectory, entry.getName())));
		   
		    int len;
		    while((len = in.read(buffer)) >= 0)
		      out.write(buffer, 0, len);

		    in.close();
		    out.close();
		}

		zipFile.close();
	}

	static void compressOutput(File outputFile, List<File> files) {

		byte[] buf = new byte[1024];

		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));

			for (int i = 0; i < files.size(); i++) {
				FileInputStream in = new FileInputStream(files.get(i));
				out.putNextEntry(new ZipEntry(files.get(i).getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}

			out.close();

			for (File file : files) {
				file.delete();
			}
		} catch (IOException e) {
			Ulrice.getMessageHandler().handleException(e);
		}
	}
}
