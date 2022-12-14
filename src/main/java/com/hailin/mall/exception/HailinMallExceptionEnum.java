package com.hailin.mall.exception;
//异常枚举
public enum HailinMallExceptionEnum {
    NEED_USER_NAME(10001,"用户名不能为空"),
    NEED_PASSWORD(10002,"密码不能为空"),
    PASSWORD_TOO_SHORT(10003,"密码不能少于8位"),
    NAME_EXISTED(10004,"重复的名称"),
    INSERT_FAILED(10005,"发生异常状况，注册失败，请重试"),
    WRONG_PASSWORD(10006,"输入有误，请重试"),
    NEED_LOGIN(10007,"用户未登录"),
    UPDATE_FAILED(10008,"更新失败"),
    NEED_ADMIN(10009,"无管理员权限"),
    PARAM_NOT_NULL(10010,"参数不能为空"),
    CREATE_FAILED(10011,"新增失败"),
    REQUEST_PARAM_ERROR(10012,"参数错误"),
    DELETE_FAILED(10013,"删除失败"),
    MKDIR_FAILED(10014,"文件夹创建失败"),
    UPLOAD_FAILED(10015,"图片上传失败"),
    NOT_SALE(10016,"商品不可售"),
    NOT_ENOUGH(10017,"库存不足"),
    CART_EMPTY(10018,"购物车已勾选商品为空"),
    NO_ENUM(10019,"未找到对应枚举"),
    NO_ORDER(10020,"订单不存在"),
    NOT_YOUR_ORDER(10021,"未在您的订单中找到"),
    WRONG_ORDER_STATUS(10022,"订单状态不符"),
    SYSTEM_ERROR(20000,"系统异常");
    Integer code;//异常码
    String msg;//异常信息

    HailinMallExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
