package com.rpa.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * <p>
 *
 * </p>
 *
 * @author LMX
 * @date 2023/05/24/9:54
 */
@Slf4j
public class FileUtil {

    /**
     * 创建目录
     */
    public static void mkDirs(String dirPath) {
        File directory = new File(dirPath);
        try {
            if(!directory.exists()) {
                directory.mkdirs();
            }
        } catch (Exception e) {
            log.info("创建目录失败", e);
            throw e;
        }
    }
}
