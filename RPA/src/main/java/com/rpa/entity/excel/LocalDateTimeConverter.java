package com.rpa.entity.excel;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author QingBinGuo
 * @date 2022/10/9 17:42
 */
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    /**
     * 转换成的Java类型
     */
    @Override
    public Class<LocalDateTime> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    /**
     * Excel表中的类型
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 在读取时，将Excel的值转换为Java值的规则
     */
    @Override
    public LocalDateTime convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String value = cellData.getStringValue();
        if (StringUtils.hasText(value)) {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (CellDataTypeEnum.NUMBER == cellData.getType()) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(cellData.getNumberValue().longValue()), ZoneId.systemDefault());
        }
        return null;
    }

    /**
     * 在写入时，将Java的值转换为Excel值的规则
     */
    @Override
    public CellData convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String valueString = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new CellData<>(valueString);
    }

}