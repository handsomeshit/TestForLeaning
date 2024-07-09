package com.betaTest.test;

import java.util.Base64;
import java.io.File;
import java.io.FileOutputStream;

/**
 * <p>
 *
 * </p>
 *
 * @author LMX
 * @date 2023/03/06/10:52
 */
public class TestBase64Decode {

/*    public static void main(String[] args) throws Exception {
        File file = new File("C:\\Users\\Administrator\\Desktop\\新建文本文档.txt");
        InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
        StringBuilder base64 = new StringBuilder();
        int i = 0;
        while((i = reader.read())!=-1) {
            base64.append((char)i);
        }
        decodeBase64File(base64.toString(), "C:\\Users\\Administrator\\Desktop\\1.pdf");
    }*/



    public static void decodeBase64File(String base64Code, String targetPath) throws Exception {
        File file = new File(targetPath);
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] buffer = decoder.decode(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }
}
