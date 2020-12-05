package com.channggou;


import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class SctrpytTest {
    @Test
    void encrypt() {
        String hashpw = BCrypt.hashpw("365373011", BCrypt.gensalt());
        System.out.println("密码加密:" + hashpw);
        boolean checkpw = BCrypt.checkpw("365373011", hashpw);
        System.out.println(checkpw);
    }
}
