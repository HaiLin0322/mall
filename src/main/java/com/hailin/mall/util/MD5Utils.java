package com.hailin.mall.util;

import com.hailin.mall.common.Constant;
import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//MD5工具
public class MD5Utils {
    public static String getMD5String(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return Base64.encodeBase64String(md5.digest((strValue+ Constant.SALT).getBytes()));
    }

    //测试生成的MD5的值
    public static void main(String[] args) {
        String md5=null;
        try {
            md5=getMD5String("1234");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        System.out.println(md5);
    }
}
