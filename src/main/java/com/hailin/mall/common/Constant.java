package com.hailin.mall.common;

import com.google.common.collect.Sets;
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
}
