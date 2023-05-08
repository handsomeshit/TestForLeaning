package com.betaTest.test;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class Zipplus {

    public void empress() throws IOException {
        File[] files = new File("C:\\Users\\Administrator\\Desktop\\测试数据\\myTest").listFiles();
        File zipFile = new File("C:\\Users\\Administrator\\Desktop\\测试数据\\myTest\\123.zip");
        FileOutputStream outputStream = new FileOutputStream(zipFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(byteArrayOutputStream);

        for (File file : files) {
            System.out.println(file.getPath());
            write(zipOut, file.getName(), file.getPath());
            IOUtils.write(byteArrayOutputStream.toByteArray(), outputStream);
            byteArrayOutputStream.reset();
            outputStream.flush();
        }

        zipOut.finish();
        zipOut.close();

        IOUtils.write(byteArrayOutputStream.toByteArray(), outputStream);
        outputStream.close();
    }

    private static void write(ZipArchiveOutputStream zipOut, String fileName, String filePath) throws IOException {
        ZipArchiveEntry entry = new ZipArchiveEntry(fileName);
        zipOut.putArchiveEntry(entry);
        IOUtils.copy(new FileInputStream(filePath), zipOut);
        zipOut.closeArchiveEntry();
    }
}
