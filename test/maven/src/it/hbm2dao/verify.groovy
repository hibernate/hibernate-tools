import java.io.*;

File dao = new File(basedir, "target/generated-sources/PersonHome.java");
if (!dao.isFile()) {
    throw new FileNotFoundException("Could not find generated JPA DAO: " + dao);
}
