package com.rpa.utils.jna;


import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.W32APIOptions;

/**
 * <p>
 *  JNA相关工具类 用于后期进行java窗体操作,实现文本的填写,按钮点击,元素加载等功能
 * </p>
 *
 * @author LMX
 * @date 2023/05/08/17:56
 */
public class JNAUtils {

    private static User32Ext USER32EXT = (User32Ext) Native.loadLibrary("user32", User32Ext.class, W32APIOptions.DEFAULT_OPTIONS);

    /**
     * 通过组件窗体类名和标题实现获取窗体
     * (顶层元素获取,非元素封装句柄,win原生或者进程顶层句柄(比如浏览器另存为确认框,程序住窗体等))
     *
     * @param className 句柄类名
     * @param title     句柄标题
     * @return
     */
    public static HWND findHandleByClassNameAndTitle(String className, String title) {
        return User32.INSTANCE.FindWindow(className, title); // 第一个参数是Windows窗体的窗体类，第二个参数是窗体的标题。不熟悉windows编程的需要先找一些Windows窗体数据结构的知识来看看，还有windows消息循环处理，其他的东西不用看太多。
    }

//    public boolean setWinEditValue(WinDef.HWND handle, String className, String caption, String text) {
//        if(handle != null) {
//            try {
//                WinDef.HWND edit = fin
//            } catch (Exception e) {
//
//            }
//        }
//    }

    public static boolean showWindow(HWND hwnd) {
        return User32.INSTANCE.ShowWindow(hwnd, WinUser.SW_RESTORE);
    }
}
