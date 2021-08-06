package com.xunda.mo.staticdata;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaEncodeMethod {

    /**
     * @params str 要加密的字符串
     */
    public static String rsaEncode(String str) {
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlYIwxN8f77oZlXBOuhzj+qym8NckvFLJPhtihMLL0xEpwQqK8zvOv7DwUwcfW/JrKBG/oOZbPW6OAUjsWQTBp8imJlA43rTMXTPsAwTllM2aGZdWXw2sYsgaq9864b8KjTuXCSewUWZJoQ3aAgoJDLSOpP7SOd/fpcLc/AjXzCB2auYRR4G3+W+39akGwHOqvnQzz2dwzlekhVtkywfLzt0nll8W4CkGEI+pq9Gy5R5XViJsvj7tLCy3DtBWHcKT0/WmoEOojqESjsY09SWWOKG+KjJfrABWJK4r1OcVrGWUkr/VRhpDGzPXD9LiU05efayK6BHJsX5RMBDkIZDlHQIDAQAB";
        String outStr = "";
        try {
            // base64编码的公钥
            byte[] decoded = Base64.decode(publicKey, Base64.DEFAULT);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            // RSA加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            outStr = Base64.encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outStr;
    }

}
