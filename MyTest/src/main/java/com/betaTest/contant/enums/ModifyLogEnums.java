package com.betaTest.contant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author LMX
 * @date 2023/09/04/9:57
 */
public class ModifyLogEnums {

    @AllArgsConstructor
    @Getter
    public enum OperationObject {

//        wuziDetail("wuzi_detail", WuziDetail.class, Lists.newArrayList("物资编码", "当前使用人id")),
//
        inventoryWuziDetail("inventory_wuzi_detail", ModifyLogEnums.class, Lists.newArrayList());

        private final String tableName;

        private final Class clazz;

        private final List<String> unCheckedList;

        public static OperationObject getEnumByClass(Class clazz) {
            return Arrays.stream(values()).filter(o -> o.getClazz().equals(clazz)).findFirst().orElse(null);
        }
    }

    @AllArgsConstructor
    @Getter
    public enum OperationType {

        insert("新增"),

        update("更新"),

        addImport("新增导入"),

        coverImport("覆盖导入"),

        flowUpdate("流程引擎更新"),

        inventoryAdd("盘点新增"),

        inventoryUpdate("盘点更新");

        private final String operationType;
    }
}
