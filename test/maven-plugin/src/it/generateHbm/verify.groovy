import java.io.*;

File file = new File(basedir, "target/generated-sources/Person.hbm.xml");
if (!file.isFile()) {
    throw new FileNotFoundException("Could not find generated HBM file: " + file);
}
