package com.alibaba.ageiport.common.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public final class IOUtils {

    private static final int COPY_BUF_SIZE = 8024;
    private static final int SKIP_BUF_SIZE = 4096;


    private static final byte[] SKIP_BUF = new byte[SKIP_BUF_SIZE];


    private IOUtils() {
    }


    public static long copy(final InputStream input, final OutputStream output) throws IOException {
        return copy(input, output, COPY_BUF_SIZE);
    }

    public static long copy(final InputStream input, final OutputStream output, final int buffersize) throws IOException {
        if (buffersize < 1) {
            throw new IllegalArgumentException("buffersize must be bigger than 0");
        }
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        long count = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }


    public static long skip(final InputStream input, long numToSkip) throws IOException {
        final long available = numToSkip;
        while (numToSkip > 0) {
            final long skipped = input.skip(numToSkip);
            if (skipped == 0) {
                break;
            }
            numToSkip -= skipped;
        }

        while (numToSkip > 0) {
            final int read = readFully(input, SKIP_BUF, 0,
                    (int) Math.min(numToSkip, SKIP_BUF_SIZE));
            if (read < 1) {
                break;
            }
            numToSkip -= read;
        }
        return available - numToSkip;
    }


    public static int readFully(final InputStream input, final byte[] b) throws IOException {
        return readFully(input, b, 0, b.length);
    }


    public static int readFully(final InputStream input, final byte[] b, final int offset, final int len)
            throws IOException {
        if (len < 0 || offset < 0 || len + offset > b.length) {
            throw new IndexOutOfBoundsException();
        }
        int count = 0, x = 0;
        while (count != len) {
            x = input.read(b, offset + count, len - count);
            if (x == -1) {
                break;
            }
            count += x;
        }
        return count;
    }


    public static void readFully(ReadableByteChannel channel, ByteBuffer b) throws IOException {
        final int expectedLength = b.remaining();
        int read = 0;
        while (read < expectedLength) {
            int readNow = channel.read(b);
            if (readNow <= 0) {
                break;
            }
            read += readNow;
        }
        if (read < expectedLength) {
            throw new EOFException();
        }
    }

    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }


    public static void closeQuietly(final Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (final IOException ignored) { // NOPMD NOSONAR
            }
        }
    }
}