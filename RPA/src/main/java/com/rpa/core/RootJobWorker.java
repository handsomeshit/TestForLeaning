package com.rpa.core;

import com.rpa.config.RPAConfig;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.File;

/**
 * <p>
 *  核心功能
 * </p>
 *
 * @author LMX
 * @date 2023/05/08/17:56
 */
public class RootJobWorker {

    @Resource
    private RPAConfig rpaConfig = new RPAConfig();

    private ChromeDriver driver;

    @Test
    public void test() {
        initChrome();
    }

    public void initChrome() {
        ClassPathResource resource = new ClassPathResource("\\src\\main\\resources/lib/ChromeDriver/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", resource.getPath());
        ChromeOptions options = new ChromeOptions();
        //无浏览器模式相关设置
        //options.addArguments("--headless");   //设置为无浏览器访问
        //无浏览器模式-最大化窗口  ,防止有些元素被隐藏
//        int screenWidth = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
//        int screenHeight = java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
//        options.addArguments("window-size=" + screenWidth + "," + screenHeight);

        options.addArguments("--disable-gpu");
        options.addArguments("--disable-software-rasterizer"); //禁用3D软件光栅化器
        options.addArguments("--no-sandbox");// 为了让linux root用户也能执行
        //优化参数
        options.addArguments("--disable-dev-shm-usage"); //解决在某些VM环境中，/dev/shm分区太小，导致Chrome失败或崩溃
        //如果要爬取图片, 则开启下列参数
        //options.addArguments("blink-settings=imagesEnabled=false"); //禁止加图片,如果爬取图片的话,这个不能禁用
        //options.addArguments("--disable-images");

        //创建缓存用的临时文件夹
        String tmpDir = rpaConfig.getTmpdir() + File.separator + "chromeFileDataCache" + File.separator + rpaConfig.getFileSerial();
        File dataDir = new File(tmpDir + File.separator + "data");
        if(dataDir.exists()){
            dataDir.mkdirs();
        }
        File cacheDir = new File(tmpDir + File.separator + "cache");
        if(cacheDir.exists()){
            cacheDir.mkdirs();
        }
        options.addArguments("--user-data-dir=" + dataDir.getAbsolutePath()); //解决打开页面出现data;空白页面情况,因为没有缓存目录
        options.addArguments("--disk-cache-dir=" + cacheDir.getAbsolutePath()); //指定Cache路径
        options.addArguments("--incognito") ; //无痕模式
        options.addArguments("--disable-plugins"); //禁用插件,加快速度
        options.addArguments("--disable-extensions"); //禁用扩展
        options.addArguments("--disable-popup-blocking"); //关闭弹窗拦截
        options.addArguments("--ignore-certificate-errors"); //  禁现窗口最大化
        options.addArguments("--allow-running-insecure-content");  //关闭https提示 32位
        options.addArguments("--disable-infobars");  //禁用浏览器正在被自动化程序控制的提示  ,但是高版本不生效
        //随机设置请求头
        options.addArguments("--user-agent=" + UserAgent.getUserAgentWindows());
        proxy(options, false); //设置代理 ,true 开启代理
        driver = new ChromeDriver(options);//实例化
        driver.manage().window().maximize(); //界面的方式, 最大化窗口, 防止有些元素被隐藏,无界面就不要使用了
        driver.get("https://www.baidu.com/");
    }

    //自行扩展, 从接口中读取,或者从文件中读取都行
    private void proxy(ChromeOptions options, boolean pd) {
        if (pd) {
            String prox = "101.200.127.149:" + 3129;
            Proxy p = new Proxy();
            p.setHttpProxy(prox);//http
//        p.setFtpProxy(prox); //ftp
//        p.setSslProxy(prox);//ssl
//        p.setSocksProxy(prox); //SOCKS
//        p.setSocksUsername("");
//        p.setSocksPassword("");
            options.setProxy(p);
        }
    }
}
