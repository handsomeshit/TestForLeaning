package com.rpa.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * </p>
 *
 * @author LMX
 * @date 2023/05/24/11:21
 */
@Slf4j
public class CmdUtil {

    /**
     * 执行cmd命令
     */
    public static boolean executeCmd(String cmd) {
        try {
            log.info("执行cmd命令[{}]", cmd);
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("cmd /c " + cmd);
            return true;
        } catch (IOException e) {
            log.error("命令执行失败");
            return false;
        }
    }

    /**
     * 执行带返回参数的cmd命令
     */
    @SneakyThrows
    public static List<String> executeCmdwithReturn(String cmd) {
        BufferedReader reader = null;
        try {
            log.info("执行cmd命令[{}]", cmd);
            List<String> result = new ArrayList<>();
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cmd /c " + cmd);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            String line;
            log.info("--------开始读取程序执行的返回信息--------");
            while ((line = reader.readLine()) != null) {
                log.info(line);
                result.add(line);
            }
            log.info("--------结束读取程序执行的返回信息--------");
            return result;
        } catch (Exception e) {
            log.error("命令执行失败");
            return null;
        } finally {
            reader.close();
        }
    }

    /**
     * 关闭指定端口的程序
     * 如果有多个, 就以逗号相隔
     */
    @SneakyThrows
    public static void killPorts(String portStr) {
        String[] split = portStr.split(",");
        Set<Integer> ports = new HashSet<>();
        for (String each : split) {
            try {
                ports.add(Integer.parseInt(each));
            } catch (Exception e) {
                log.info("无法识别的端口, 请以英文逗号隔开多个端口");
                Thread.sleep(2000);
            }
        }
        ports.forEach(port -> {
            kill(port);
        });
    }

    public static void kill(int port) {
        List<String> returnList = executeCmdwithReturn("netstat -ano | findstr \"" + port + "\"");
        List<String> validLineList = returnList.stream().filter(line -> containValidPort(line, port)).collect(Collectors.toList());
        if(validLineList.isEmpty()) {
            log.info("找不到端口号为:{}的进程", port);
        } else {
            Set<Integer> pids = new HashSet<>();
            for (String validLine : validLineList) {
                // 去除前后空格
                validLine = validLine.trim();
                // 获取最后一个空格下标
                int offset = validLine.lastIndexOf(" ");
                // 截取最后的内容 如 30700 或者 2319/python
                String spid = validLine.substring(offset);
                // 替换其中的空格
                spid = spid.replaceAll(" ", "");
                // 如果存在/
                int lastSlashIndex = spid.lastIndexOf("/");
                if (lastSlashIndex != -1) {
                    // 处理/
                    spid = spid.substring(0, lastSlashIndex);
                }
                try {
                    int pid = 0;
                    pid = Integer.parseInt(spid);
                    pids.add(pid);
                } catch (NumberFormatException e) {
                    log.info(e.getMessage(), e);
                }
            }
            log.info("需要关闭的进程的pid:{}", pids);
            if(CollectionUtils.isEmpty(pids)) {
                return;
            } else {
                pids.forEach(pid -> executeCmdwithReturn("taskkill /F /pid " + pid));
            }
        }
    }

    /**
     * 参考地址: https://www.zjh336.cn/?id=2055
     */
    public static boolean containValidPort(String line, int comparePort) {
        String find = "";
        String reg = "^ *[a-zA-Z]+ +\\S+";
        // 匹配正则
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(line);
        Boolean findFlag = matcher.find();
        log.info("读取数据：" + line);
        // 未匹配到则直接返回
        if (!findFlag) {
            return false;
        }
        // 获取匹配内容
        find = matcher.group();
        // 处理数据
        int spstart = find.lastIndexOf(":");
        // 截取掉冒号
        find = find.substring(spstart + 1);
        int port = 0;
        try {
            port = Integer.parseInt(find);
        } catch (NumberFormatException e) {
            return false;
        }
        // 端口等于需要对比的端口号, 则通过验证
        return comparePort == port;
    }
}
