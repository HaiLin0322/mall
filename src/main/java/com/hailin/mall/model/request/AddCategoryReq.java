package com.hailin.mall.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//用于数据库增加的实体类，区别于pojo的实体类，使用的方法不需要的元素不进行设置，以避免不必要的风险
public class AddCategoryReq {
    @Size(min = 2,max = 5)//校验长度，需要在该范围内
    @NotNull//校验不能为空
    private String name;
    @NotNull
    @Max(3)//校验数据最大值
    private Integer type;
    @NotNull(message = "parentId不能为null")//message设置用于替换默认提示，当能够显示给用户时提示更明显
    private Integer parentId;
    @NotNull
    private Integer orderNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
