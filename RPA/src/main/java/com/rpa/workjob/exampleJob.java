package com.rpa.workjob;

import com.rpa.core.RootJobWorker;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;

/**
 * <p>
 *  自动化例子
 * </p>
 *
 * @author LMX
 * @date 2023/05/08/17:54
 */
public class exampleJob extends RootJobWorker {

    /**
     * 测试使用selenium
     */
    @Test
    public void test1() {
        initChrome();
        driver.get("https://www.baidu.com/");
        WebElement kw = driver.findElementById("kw");
        kw.click();
        kw.sendKeys("OpenAI");
        WebElement su = driver.findElementById("su");
        su.click();
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试加载包含指定内容的元素
     */
    @Test
    public void test2() {
        initChrome();
        driver.get("https://www.baidu.com/");
        WebElement su = driver.findElementById("su");
        WebElement element = waitLoadElementWithContent(By.id("su"), webElement -> "百度一下".equals(webElement.getAttribute("value")), 10000);
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试截图
     */
    @Test
    public void test3() {
        initChrome();
        driver.get("https://www.baidu.com/");
        screenshotPNG(new File("C:\\Users\\Administrator\\Desktop\\截图.png"));
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试切换iframe
     */
    @Test
    public void test4() {
        initChrome();
        driver.get("https://www.baidu.com/");
        //有时候元素加载不到, 可能是因为嵌套在iframe中, 先要切进里面
        WebElement element = waitLoadElement(By.id("iframeId"), 1000);
        driver.switchTo().frame(element);   //切换到指定的iframe
        driver.switchTo().parentFrame();    //切换到父级iframe
        driver.switchTo().defaultContent(); //切换到最外层
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试刷新页面
     */
    @Test
    public void test5() {
        initChrome();
        driver.get("https://www.baidu.com/");
        refresh();
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试打开新标签页
     */
    @Test
    public void test6() {
        initChrome();
        driver.get("https://www.baidu.com/");
        //在当前页打开一个网址, 只需要再调用一次
        //driver.get("https://juejin.cn/backend/Java/");
        openUrlWithNewTab("https://juejin.cn/backend/Java/");
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试窗口方法
     */
    @Test
    public void test7() {
        initChrome();
        driver.get("https://www.baidu.com/");
        driver.manage().window().maximize();    //最大化
        driver.manage().window().fullscreen();  //全屏
        driver.manage().window().getSize();     //获取浏览器的长宽
        driver.manage().window().getPosition(); //获取浏览器的位置, 计算位置为浏览器左上的边角位置
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试键盘事件
     */
    @Test
    public void test8() {
        initChrome();
        driver.get("https://www.baidu.com/");
        WebElement kw = driver.findElementById("kw");
        kw.sendKeys(Keys.CONTROL, "a");
        kw.sendKeys(Keys.CONTROL, "x");
        kw.sendKeys(Keys.CONTROL, "c");
        kw.sendKeys(Keys.CONTROL, "v");
        kw.sendKeys(Keys.F5);
        kw.sendKeys(Keys.TAB);
        kw.sendKeys(Keys.ENTER);
        kw.sendKeys(Keys.SPACE);
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试鼠标事件
     */
    @Test
    public void test9() {
        initChrome();
        driver.get("https://www.baidu.com/");
        WebElement su = driver.findElementById("su");
        Actions actions = new Actions(driver);
        actions.contextClick(su).perform(); //右键点击
        actions.clickAndHold(su).perform(); //左键单击
        actions.doubleClick(su).perform();  //左键双击
        actions.moveToElement(su).perform(); //移动到该元素中间
        actions.contextClick(su).perform(); //右键点击
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试关闭当前页和关闭驱动程序
     */
    @Test
    public void test10() {
        initChrome();
        driver.get("https://www.baidu.com/");
        openUrlWithNewTab("https://juejin.cn/backend/Java/");
        driver.close();
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试表单操作
     */
    @Test
    public void test11() {
        initChrome();
        driver.get("https://www.baidu.com/");
        //下拉框操作
        Select select = new Select(driver.findElementById("select"));
        //通过索引选择
        select.selectByIndex(1);
        //通过value值获取
        select.selectByValue("zhangsan");
        //通过文本值获取
        select.selectByVisibleText("张三");

        driver.findElementById("radio").click(); //单选按钮

        WebElement form = driver.findElementById("form");
        //只能用于表单提交
        form.submit();

        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试切换窗口
     */
    @Test
    public void test12() {
        initChrome();
        driver.get("https://www.baidu.com/");
        //获取窗口的句柄
        String windowHandle = driver.getWindowHandle();
        //另外一个窗口执行...
        //另外一个窗口执行结束后，我们可以通过switchTo()去返回到原先窗口
        driver.switchTo().window(windowHandle);
        driver.quit();  //这个方法一定要调用, 否则驱动会长期存在于内存中无法释放
    }

    /**
     * 测试关闭其他窗口
     */
    @Test
    public void test13() {
        initChrome();
        driver.get("https://www.baidu.com/");
        closeOtherTabs();
        driver.quit();
    }
}
