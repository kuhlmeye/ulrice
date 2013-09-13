package net.ulrice.webstarter.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.SortedMap;
import java.util.jar.JarFile;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Packer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreateDescription {

    private static final Logger LOG = Logger.getLogger(CreateDescription.class.getName());

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
        SortedMap<String, String> sortedMap = packer.properties();
        sortedMap.put(Packer.EFFORT, "7"); // default is "5"
        sortedMap.put(Packer.SEGMENT_LIMIT, "-1");
        sortedMap.put(Packer.KEEP_FILE_ORDER, Packer.FALSE);
        sortedMap.put(Packer.MODIFICATION_TIME, Packer.LATEST);
        sortedMap.put(Packer.DEFLATE_HINT, Packer.FALSE);
        sortedMap.put(Packer.CODE_ATTRIBUTE_PFX + "LineNumberTable", Packer.STRIP);
        sortedMap.put(Packer.UNKNOWN_ATTRIBUTE, Packer.ERROR);

        for (File file : files) {

            try {
                JarFile jarFile = new JarFile(file);
                try {
                    FileOutputStream fos = new FileOutputStream(new File(jarFile.getName() + ".pack"));

                    try {
                        // Call the packer
                        packer.pack(jarFile, fos);
                    }
                    finally {
                        fos.close();
                    }
                }
                finally {
                    jarFile.close();
                }
            }
            catch (IOException ioe) {
                LOG.log(Level.SEVERE, "IO Exception.", ioe);
            }
        }
    }
}
