package com.leanderli.dev.cardlauncherpro.common;

/**
 * @author leanderli
 * @description
 * @date 2018/3/17 00172347
 */

public enum AppEnum {

    /**
     * 新增应用
     */
    ADD_APPS(0x000),
    /**
     * 删除应用
     */
    REMOVE_APPS(0x001),
    /**
     * 应用初始化成功
     */
    MSG_APP_INITIALIZATION_SUCCESS(0x002),
    /**
     * 应用初始化失败
     */
    MSG_APP_INITIALIZATION_FAILED(0x003),
    /**
     * 新建分组
     */
    MSG_PUT_EXTRA_ADD_GROUP(0x1000),
    /**
     * 组已存在
     */
    MSG_GROUP_EXIST(0x1001),
    /**
     * 分组信息保存成功
     */
    MSG_GROUP_SAVE(0x1002),
    /**
     * 分组名称为空
     */
    MSG_GROUP_NAME_IS_NULL(0x1003),
    /**
     * 传递数据成功
     */
    MSG_PUT_EXTRA_SUCCESS(0x200),
    /**
     * 传递数据失败
     */
    MSG_PUT_EXTRA_FAIL(0x202),
    /**
     * 应用不存在
     */
    MSG_APP_NOT_FOUND_ERROR(0x000404),
    /**
     * 无默认应用
     */
    MSG_NO_FAV_APPS(0x000200);

    private int value;

    private AppEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
