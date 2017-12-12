package io;

import java.io.IOException;

public interface Writer {
    void Write(String pathname, String data) throws IOException;
}
