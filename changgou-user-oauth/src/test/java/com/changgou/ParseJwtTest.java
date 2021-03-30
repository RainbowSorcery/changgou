package com.changgou;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;


public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJpdGhlaW1hIiwiaWQiOiIxIn0.aOUSeDDjhpEXRoiPv0dP8jLdl_1kgH8mtJi5al9ntKiz5u4MHir9_IcxFYI0_ndMXEZcYohJVdNBXca8E6jVqalvfXtd2sopuaFUVSYVeYZ33KBgtoNaBYdA2JUMgm6duar9998Wu_i2SV8HwK58pCbF_Rwv2Qi6--m3IAKGNWy81qLfpOwZ1voyHVsjNXG8NtQ5yW9k2-eeXLxRX2D8uSD3bObujaRCyQzDU_EsRyaxpjfu5BqtMBWLDK_wWSDTW11jwHrldnwHycB4XUH7OE8GPDq0U4lgRRM4uX-hFPYVd6A6bm8eNRbyO_oglgNppgr1chN1d53vLWWFbGUIiw";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhIRqu5RUAnXJDwrH24pV\n" +
                "1WDwMT+eEx4760qEvGOnQBCWZoGWvL17ZmMSdOultDhvR0cPm8fk/letGNpYzP5l\n" +
                "kPdJc49s56LhaJ/cYsmG4RW+GieP4a9r5nlpv1RDfJogM6+17yk2XLNop2Oyv3L+\n" +
                "DroIYu2VCaiTuh2dm27vxl44WB3sH9RYJxnkX32fSDdwxmr84pa3K/nIMs7ne4RK\n" +
                "DLtXMgScunFvFhJHxzxWZu+gP42G3TVOo6jvISYv+qf2R69gF6CCYKPVZJTqQY+T\n" +
                "bFabgV/3+JdKFvy5+FOxH9+W3poV2RmgJfvOB7JMdUSbBCr4XlhPIG2PFkiKEjHL\n" +
                "SQIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);

        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
