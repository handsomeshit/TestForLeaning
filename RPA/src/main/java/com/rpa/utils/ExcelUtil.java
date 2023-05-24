package com.rpa.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.rpa.entity.DemoData;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  excel工具
 * </p>
 *
 * @author LMX
 * @date 2023/05/22/11:27
 */
@Slf4j
public class ExcelUtil<T> {

    public static void readExcel(String readFilePath, Class clazz, List list) {
        try (FileInputStream file = new FileInputStream(readFilePath)){
            long startTime = System.currentTimeMillis();
            EasyExcel.read(file, clazz, getExcelListener(list)).sheet().doRead();
            long costTime = (System.currentTimeMillis() - startTime);
            log.info("读取excel完毕, 处理时间:{}毫秒", costTime);
        } catch (Exception e) {
            log.error("读取excel出错", e);
        }
    }

    public static AnalysisEventListener getExcelListener(List list) {
        return new AnalysisEventListener() {
            @Override
            public void invoke(Object data, AnalysisContext context) {
                // 处理可能出现空行问题
                if(null == data) {
                    return;
                }
                list.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }
        };
    }

    public static void writeExcel(String writeFilePath, Class clazz, List list) {

        File writeFile = new File(writeFilePath);
        File parentDir = new File(writeFile.getParent());
        if(!parentDir.exists()) {
            parentDir.mkdirs();
        }
        try {
            long startTime = System.currentTimeMillis();
            EasyExcel.write(writeFile, clazz).sheet().needHead(true).doWrite(list);
            long costTime = (System.currentTimeMillis() - startTime);
            log.info("写入excel完毕, 处理时间:{}毫秒", costTime);
        } catch (Exception e) {
            log.error("写入excel出错", e);
        }

    }


}
