package com.ljf.idempotentsdk.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class JDBCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public  JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public static void main(String[] args) {
        //@Autowired
        //JdbcTemplate jdbcTemplate = null;

        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        System.out.println(ctx);
        //jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");
        JdbcTemplate jdbcTemplate = (new JDBCTest()).getJdbcTemplate();

        System.out.println(jdbcTemplate);
    }
}

