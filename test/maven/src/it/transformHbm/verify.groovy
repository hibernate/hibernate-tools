import java.io.*;

File file = new File(basedir, "src/main/resources/Foo.mapping.xml");
if (!file.isFile()) {
    throw new FileNotFoundException("Could not find generated HBM file: " + file);
}
