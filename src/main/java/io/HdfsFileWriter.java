package io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;

public class HdfsFileWriter implements Writer {
    public void Write(String pathname, String data) throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        configuration.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
        Path path= new Path(pathname);
        FileSystem fs = path.getFileSystem(configuration);
        if(fs.isFile(path)){
            fs.delete(path);
        }
        FSDataOutputStream outputStream=fs.create(path);
        outputStream.writeBytes(data);
        outputStream.close();
    }
}
