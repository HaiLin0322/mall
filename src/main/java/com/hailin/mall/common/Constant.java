package com.hailin.mall.common;

import com.google.common.collect.Sets;
import com.hailin.mall.exception.HailinMallException;
import com.hailin.mall.exception.HailinMallExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

//一些常量
@Component
public class Constant {
    public static final String HAILIN_MALL_USER="HAILIN_MALL_USER";
    //盐值
    public static final String SALT="f465asd4f23r3fv";
    public static String FILE_UPLOAD_DIR;
    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir){
        FILE_UPLOAD_DIR=fileUploadDir;
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC= Sets.newHashSet("price desc","price asc");
    }
    public interface SaleStatus{
        int SALE=1;//上架
        int NOT_SALE=0;//下架
    }
    public interface Cart{
        int UNCHECKED=0;//购物车未选中状态
        int CHECKED=1;//购物车选中状态
    }
    public enum OrderStatusEnum{
        CANCELED(0,"用户已取消"),
        NOT_PAY(10,"用户未付款"),
        PAID(20,"用户已付款"),
        DELIVERED(30,"已发货"),
        FINISHED(40,"交易完成");
        private String value;
        private int code;

        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum:values()){
                if (orderStatusEnum.getCode()==code){
                    return orderStatusEnum;
                }
            }
            throw new HailinMallException(HailinMallExceptionEnum.NO_ENUM);
        }

        OrderStatusEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
}
