package com.example.test.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {

    protected static Logger logger = LogManager.getLogger();

    private static final String ERROR_UNABLE_TO_CLOSE = "Unable to close properly the BufferedInputStream.";

    private InputStream stream;

    public InputStream get(File file) throws IOException {
        try {
            if (stream != null) {
                stream.close();
            }

            stream = new FileInputStream(file);

            return stream;
        } catch (Exception e) {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e1) {
                logger.error(ERROR_UNABLE_TO_CLOSE, e);
            }

            throw new IOException("Something wrong happened reading the file: " + file, e);
        }
    }

    public void close() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            logger.error(ERROR_UNABLE_TO_CLOSE, e);
        }
    }
}