package com.ywis.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;
 

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2

public class SwaggerConfig {
	 private String version;
 
    @Bean
    public Docket apiV1() {
    	version = "V3";
	  
    	
        return new Docket(DocumentationType.OAS_30) // open api spec 3.0
        		.consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
        		.useDefaultResponseMessages(false)
        		.apiInfo(getApiInfo(version))
        		.groupName(version)
        		.enableUrlTemplating(false)
        		.useDefaultResponseMessages(true)
        		.select()
        		
                //.apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/ktgateway/**"))
                
                .build() ;
 
    }
    
   
    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        return consumes;
    }
    
    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }
    
    private ApiInfo getApiInfo( String version) {
        return new ApiInfoBuilder()
                .title("[YWIS KT Proxy Gateway] REST API "+version)
                .description("[YWIS KT Proxy Gateway] API REST API Details")
//                .contact(new Contact("[Logout]", "/logout.do", ""))
                .version("3.0")
                .build();
    }
 
}
