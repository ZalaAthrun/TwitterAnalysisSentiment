package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;

public class LocalFileWriter implements Writer {
    public void Write(String pathname, String data) throws IOException {
        File file = new File(pathname);
        if(file.exists()){
            file.delete();
        }
        if(!file.exists()){
            file.createNewFile();
        }
        file.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        bufferedWriter.write(data);
        bufferedWriter.close();
    }
}
