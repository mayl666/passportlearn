package com.sogou.upd.passport.common.parameter;

import java.util.List;

/**
 * 后台操作记录类型枚举
 * User: chengang
 * Date: 14-8-11
 * Time: 下午2:08
 */
public enum OperateLogEnum implements IndexedEnum {


    RESET_PWD(0, "重置密码"),

    UNBIND_EMAIL(1, "解绑邮箱"),

    UNBIND_PHONE(2, "解绑手机"),

    PATCH_DELETE_PHONE(3, "批量删除注册手机号");


    private static final List<OperateLogEnum> INDEXS = IndexedEnum.IndexedEnumUtil.toIndexes(OperateLogEnum.values());

    /**
     * 索引
     */
    private final int index;

    /**
     * 系统
     */
    private final String operateType;

    OperateLogEnum(int index, String operateType) {
        this.index = index;
        this.operateType = operateType;
    }


    public int getIndex() {
        return index;
    }

    public String getOperateType() {
        return operateType;
    }

    /**
     * 根据index取得相应的平台
     *
     * @param index
     * @return
     */
    public static OperateLogEnum indexOf(final int index) {
        return IndexedEnum.IndexedEnumUtil.valueOf(INDEXS, index);
    }

}
