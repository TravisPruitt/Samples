package com.disney.xband.common.lib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtils {
    /*
      * Generates a compressed file with a new file name.
      * Returns the new file path. The old file is not deleted.
      */
    public static String gzip(String path) throws IOException {
        String newPath = path + ".gz";
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new FileInputStream(path));
            out = new BufferedOutputStream(new FileOutputStream(newPath));

            gzip(in, out);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception ignore) {
                }
            }

            if (out != null) {
                try {
                    out.close();
                }
                catch (Exception ignore) {
                }
            }
        }

        return newPath;
    }

    public static void gzip(InputStream in, OutputStream out)
            throws IOException {
        int BUF_SIZE = 4096;
        GZIPOutputStream zipout = null;

        try {
            zipout = new GZIPOutputStream(out, BUF_SIZE);
            byte[] buffer = new byte[BUF_SIZE];
            int readBytes;
            do {
                readBytes = in.read(buffer, 0, BUF_SIZE);
                if (readBytes > 0) {
                    zipout.write(buffer, 0, readBytes);
                }
            }
            while (readBytes != -1);

            zipout.finish();
        }
        finally {
            if (zipout != null) {
                try {
                    zipout.close();
                }
                catch (Exception ignore) {
                }
            }
        }
    }
}
