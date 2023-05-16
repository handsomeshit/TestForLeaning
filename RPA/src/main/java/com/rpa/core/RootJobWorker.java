package com.rpa.core;

import com.rpa.config.RPAConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

/**
 * <p>
 *  核心功能
 * </p>
 *
 * @author LMX
 * @date 2023/05/08/17:56
 */
@Slf4j
public class RootJobWorker {

    @Resource
    protected RPAConfig rpaConfig = new RPAConfig();

    protected ChromeDriver driver;

    protected Actions actions;

    /**
     * 初始化环境
     */
    public ChromeDriver initChrome() {
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
        actions = new Actions(driver);
        return driver;
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

    /**
     * 执行js代码
     * @return 返沪结果
     */
    public Object runJSCode(String jscode) {
        try {
            JavascriptExecutor js = driver;
            log.info("runJScode:" + jscode);
            return js.executeScript(jscode);
        } catch (Exception e) {
            log.info("js执行异常", e);
            return null;
        }
    }

    /**
     * 指定元素执行js代码
     * @return 返沪结果
     */
    public Object runJSCode(String jscode, WebElement w) {
        try {
            JavascriptExecutor js = driver;
            log.info("runJScode:" + jscode);
            return js.executeScript(jscode);
        } catch (Exception e) {
            log.info("js执行异常", e);
            return null;
        }
    }

    /**
     * 程序休眠, 等待一段时间
     */
    @SneakyThrows
    public void sleep(long ms) {
        Thread.sleep(ms);
    }

    /**
     * 尝试寻找元素, 如果有多个返回第一个
     */
    public WebElement tryFindFirstElement(By by) {
        WebElement element = null;
        try {
            element = driver.findElement(by);
        } catch (NoSuchElementException e) {
            //do nothing
        }
        try {
            element = driver.findElements(by).get(0);
        } catch (Exception e) {
            //do nothing
        }
        return element;
    }

    /**
     * 等待元素加载
     */
    public WebElement waitLoadElement(By by, long scanTime) {
        long endTime = System.currentTimeMillis() + scanTime;
        WebElement element = null;
        do {
            sleep(200);
            element = tryFindFirstElement(by);
            if(System.currentTimeMillis() > endTime && element == null) {
                log.info("未找到元素:" + by.toString());
                break;
            }
        } while (element == null);
        return element;
    }

    /**
     * 等待元素加载(需要有指定内容), 有些内容是异步出现的
     */
    public WebElement waitLoadElementWithContent(By by, Function<WebElement, Boolean> condition, long scanTime) {
        long endTime = System.currentTimeMillis() + scanTime;
        WebElement element = null;
        do {
            sleep(200);
            element = tryFindFirstElement(by);
            if(System.currentTimeMillis() > endTime && !condition.apply(element)) {
                log.info("未找到元素:" + by.toString());
                return null;
            }
        } while (!condition.apply(element));
        return element;
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        try {
            driver.navigate().refresh();
            log.info("-------刷新页面成功-------");
        } catch (Exception e) {
            log.info("-------刷新页面失败-------");
        }
    }

    /**
     * 切换到含有指定元素的窗口
     */
    public boolean switchToTab(By by) {
        try {
            if (null != waitLoadElement(by, 2000)) {
                return true;
            }
            //获取当前浏览器的所有窗口句柄
            Set<String> handles = driver.getWindowHandles();//获取所有窗口句柄
            Iterator<String> it = handles.iterator();
            while (it.hasNext()) {
                String next = it.next();
                driver.switchTo().window(next);//切换到新窗口
                if (null != waitLoadElement(by, 2000)) {
                    log.info("切换到标签页(title):"+driver.getTitle());
                    return true;
                }
            }
        } catch (Exception e) {
            log.info("切换窗体失败");
            throw e;
        }
        log.info("找不到指定的标签页");
        return false;
    }

    /**
     * 切换到指定标题的窗口
     */
    public boolean switchToTab(String tabTitle) {
        try {
            if (StringUtils.isEmpty(tabTitle)) {
                return false;
            }
            if (driver.getTitle().equals(tabTitle)) {
                return true;
            }
            //获取当前浏览器的所有窗口句柄
            Set<String> handles = driver.getWindowHandles();//获取所有窗口句柄
            Iterator<String> it = handles.iterator();
            while (it.hasNext()) {
                String next = it.next();
                driver.switchTo().window(next);//切换到新窗口
                if (driver.getTitle().equals(tabTitle)) {
                    log.info("切换到标签页(title):"+driver.getTitle());
                    return true;
                }
            }
        } catch (Exception e) {
            log.info("切换窗体失败");
            throw e;
        }
        log.info("找不到指定的标签页");
        return false;
    }

    /**
     * 切换到新标题的窗口
     */
    public boolean switchToNewTab() {
        try {
            String windowHandle = driver.getWindowHandle(); //获取当前浏览器窗口句柄
            Set<String> handles = driver.getWindowHandles();//获取所有窗口句柄
            Iterator<String> it = handles.iterator();
            while (it.hasNext()) {
                String next = it.next();
                if (windowHandle.equals(next)) {
                    driver.switchTo().window(next);//切换到新窗口
                    return true;
                }
            }
        } catch (Exception e) {
            log.info("切换窗体失败");
            throw e;
        }
        log.info("找不到指定的标签页");
        return false;
    }

    /**
     * 在新标签打开链接
     * @param url
     */
    public void openUrlWithNewTab(String url) {
        runJSCode("window.open('" + url + "');");
    }

    /**
     * js滚动屏幕
     * 顶部   scroll(0, 0);
     * 指定位置   scroll(0, height);
     */
    public void scroll(int x, int y) {
        runJSCode("window.scrollTo(" + x + "," + y + ")");
    }

    /**
     * js 滚动条拉倒最底部
     */
    public void scrollToBottom() {
        runJSCode("window.scrollTo(0,document.body.scrollHeight)");
    }

    /**
     * 元素必须是存在的 将元素移动到显示器可见范围内
     *
     * @param element
     */
    public void letElementIntoView(WebElement element) {
        if (null == element) {
            return;
        }
        try {
            Object o = runJSCode("const viewPortHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight ;\n" +
                    "    const offsetTop = arguments[0].offsetTop;\n" +
                    "    const offsetHeight = arguments[0].offsetHeight;\n" +
                    "    const scrollTop = document.documentElement.scrollTop;\n" +
                    "    const top = (offsetTop+offsetHeight )- scrollTop\n" +
                    "    if(top>0&&top<viewPortHeight){\n" +
                    "        return true\n" +
                    "    }\n" +
                    "    else{\n" +
                    "        arguments[0].scrollIntoView();" +
                    "        return false;\n" +
                    "    }\n" +
                    "\n", element);
            if ("true".equals(String.valueOf(o))) {
                log.info("元素在窗体中");
            } else {
                log.info("元素移入窗体中");
            }
            log.info("letElementIntoView(元素移入视图中) -> true:" + element.getTagName());
        } catch (Exception e) {
            log.info("letElementIntoView(元素移入视图中) -> false:" + e.getMessage());
            new Actions(driver).moveToElement(element).perform();
        }
    }

    /**
     * 截图
     */
    public void screenshotPNG(File file) {
        byte[] screenshotAs = driver.getScreenshotAs(OutputType.BYTES);
        try (
                FileOutputStream fos1 = new FileOutputStream(file);
                BufferedOutputStream fos = new BufferedOutputStream(fos1);
        ) {
            fos.write(screenshotAs, 0, screenshotAs.length); // 写入数据
        } catch (Exception e) {
            log.info("截图发生错误", e);
        }
    }


}
