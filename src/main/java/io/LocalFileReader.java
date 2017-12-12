package io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import sun.nio.cs.UTF_32;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LocalFileReader implements Reader {
    public String load(String path) throws IOException {
        File file = new File(path);
        String content = FileUtils.readFileToString(file, "UTF-8");
        return content;
    }
}
