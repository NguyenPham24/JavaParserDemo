package org.example;

import java.io.File;
import java.io.IOException;

public class DirIterator {

    public interface Filter {
        boolean isJavaFile(String path);
    }
    private Filter filter;
    private FileExplorer fileExplorer;

    /// Constructor.
    public DirIterator(Filter filter, FileExplorer fileExplorer) {
        this.filter = filter;
        this.fileExplorer = fileExplorer;
    }

    /// Public getter for explore
    public void explore(File root) throws IOException {
        explore("", root);
    }

    private void explore(String path, File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                explore(path + "/" + child.getName(), child);
            }
        } else {
            if (filter.isJavaFile(path)) {
                fileExplorer.explore(path, file);
            }
        }
    }
}