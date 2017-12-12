package io;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;

public class HdfsFileReader implements Reader {
    public String load(String path) throws IOException {
        Configuration configuration = new Configuration();
        configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        configuration.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

        Path pathhadoop = new Path(path);
        FileSystem fs = pathhadoop.getFileSystem(configuration);
        FSDataInputStream inputStream = fs.open(pathhadoop);
        System.out.println(inputStream.available());
        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String result = writer.toString();

        return result;
    }
}
