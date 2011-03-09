package org.hibernate.tool.hbm2x;

import java.io.File;

public abstract class FileVisitor {


    public void visit(File dir) {
        process(dir);
    
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                visit(new File(dir, children[i]));
            }
        }
    }

	abstract protected void process(File dir);
    
}
