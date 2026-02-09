package com.revpay.util;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtils {

    private SecurityUtils(){

    }

    public static String hash(String plainText){
        return BCrypt.hashpw(plainText, BCrypt.gensalt(10));
    }

    public static  boolean verify(String plainText,String hash){
        return BCrypt.checkpw(plainText,hash);
    }
}
