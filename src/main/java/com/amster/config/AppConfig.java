package com.amster.config;  
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
@Configuration 
@ComponentScan("com.amster") 
@EnableWebMvc   
public class AppConfig {  
} 