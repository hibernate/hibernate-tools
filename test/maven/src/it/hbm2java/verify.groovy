import java.io.*;

File entity = new File(basedir, "target/generated-sources/Person.java");
if (!entity.isFile()) {
    throw new FileNotFoundException("Could not find generated JPA Entity: " + entity);
}
