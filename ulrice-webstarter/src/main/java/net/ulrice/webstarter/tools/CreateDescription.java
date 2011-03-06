package net.ulrice.webstarter.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.jar.Pack200.Unpacker;

public class CreateDescription {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File[] files = new File("/home/christof/ulrice-webstarter/nadin4.10/").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});

		Packer packer = Pack200.newPacker();
		// Initialize the state by setting the desired properties
		Map p = packer.properties();
		p.put(Packer.EFFORT, "7"); // default is "5"
		p.put(Packer.SEGMENT_LIMIT, "-1");
		p.put(Packer.KEEP_FILE_ORDER, Packer.FALSE);
		p.put(Packer.MODIFICATION_TIME, Packer.LATEST);
		p.put(Packer.DEFLATE_HINT, Packer.FALSE);
		p.put(Packer.CODE_ATTRIBUTE_PFX + "LineNumberTable", Packer.STRIP);
		p.put(Packer.UNKNOWN_ATTRIBUTE, Packer.ERROR);

		for (File file : files) {

			try {
				JarFile jarFile = new JarFile(file);
				FileOutputStream fos = new FileOutputStream(new File(jarFile.getName() + ".pack"));
				// Call the packer
				packer.pack(jarFile, fos);
				jarFile.close();
				fos.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}
