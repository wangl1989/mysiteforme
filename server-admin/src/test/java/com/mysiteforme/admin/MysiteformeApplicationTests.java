package com.mysiteforme.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MysiteformeApplicationTests {
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void contextLoads() {
        // Verify that the context loads successfully
    }

    @Test
    public void testPasswordEncoder() {
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + encodedPassword);

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        System.out.println("Password verification result: " + matches);
        
        // Add assertion to make it a proper test
        assert matches : "Password verification should return true";
    }

}
