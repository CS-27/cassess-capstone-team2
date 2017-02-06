package com.cassess;

import com.cassess.model.taiga.ConsumeAuthUser;
import com.cassess.model.taiga.ConsumeProjectList;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


import com.cassess.model.slack.ConsumeUsers;



@SpringBootApplication
@ImportResource({"classpath*:applicationContext.xml"})
public class CassessApplication {

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    @EnableWebSecurity
    protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter{
        @Override
        protected void configure(HttpSecurity http) throws Exception{
            http
                .httpBasic().and()
                .authorizeRequests()
                .antMatchers("/index.html", "/partials/home.html", "/partials/dashboard.html",
                        "/partials/login.html", "/")
                .permitAll().anyRequest().authenticated()
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        }
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            System.out.println("Let's inspect the beans provided by Spring Boot:");
            //ConsumeUsers consumeUsers = (ConsumeUsers) ctx.getBean("consumeUsers");
    		//consumeUsers.getUserInfo("U2G79FELT");

            ConsumeAuthUser consumeAuthUser = (ConsumeAuthUser) ctx.getBean("consumeAuthUser");
            consumeAuthUser.getUserInfo();
            String token = consumeAuthUser.getToken("TaigaTestUser@gmail.com");
            System.out.println("Taiga Token: " + token);
            Long id = consumeAuthUser.getID("TaigaTestUser@gmail.com");
            System.out.println("Taiga Member ID: " + id);

            ConsumeProjectList consumeProjectList = (ConsumeProjectList) ctx.getBean("consumeProjectList");
            consumeProjectList.getProjectInfo(token, id);
            System.out.println("Taiga Project Name: " + consumeProjectList.getName("tjjohn1"));

        };
    }
    
	public static void main(String[] args) {
		//ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		SpringApplication.run(CassessApplication.class, args);
	}
}
