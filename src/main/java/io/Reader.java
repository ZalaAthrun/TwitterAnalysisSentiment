package io;

import java.io.IOException;

public interface Reader {
    String load(String path) throws IOException;
}
