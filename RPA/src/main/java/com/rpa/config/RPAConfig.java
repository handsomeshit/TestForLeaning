package com.rpa.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *
 * </p>
 *
 * @author LMX
 * @date 2023/05/08/19:21
 */
@Configuration
@Data
public class RPAConfig {

    //文件版本,防止多线程缓存文件和用户文件共享,导致创建错误
    private AtomicInteger fileSerial = new AtomicInteger(0);

    @Value("${chromeConfig.driverPath:/lib/ChromeDriver/chromedriver.exe}")
    private String chromedriverPath;

    //获取临时路径
    private String tmpdir = System.getProperty("java.io.tmpdir");

    private String moduleRootPath;

    public static RPAConfig initRPAConfig() {
//        String path = new RPAConfig().getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//        String path = new RPAConfig().getClass().getResource("").getPath();
//        String path = new File("").getCanonicalPath();
//        String path = System.getProperty("user.dir");
        RPAConfig rpaConfig = new RPAConfig();
        String path = Thread.currentThread().getContextClassLoader().getResource("").getFile();
        rpaConfig.setModuleRootPath(new File(path).getParentFile().getParent());
        return rpaConfig;
    }
}
