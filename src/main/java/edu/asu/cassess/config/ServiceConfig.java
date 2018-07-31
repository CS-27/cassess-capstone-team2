package edu.asu.cassess.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Properties;

@Configuration
@ComponentScan("edu.asu.cassess.service")
public class ServiceConfig {
    private static String AG_URL = "";

    public static String getAG_URL() {
        System.out.println("[LOG] AG_URL "+AG_URL);
        return AG_URL;
    }
    static {
        try {
            Properties dbProperties = new Properties();
            dbProperties.load(ServiceConfig.class.getClassLoader().getResourceAsStream("/autograder.properties"));
            AG_URL = dbProperties.getProperty("ag_url");

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
        }
    }
}
