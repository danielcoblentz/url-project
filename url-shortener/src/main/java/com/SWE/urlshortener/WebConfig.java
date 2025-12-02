// package com.SWE.urlshortener;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebConfig {

//     @Bean
//     public WebMvcConfigurer corsConfigurer() {
//         return new WebMvcConfigurer() {
//             @Override
//             public void addCorsMappings(CorsRegistry registry) {
//                 registry.addMapping("/**")
//                         .allowedOrigins("https://url-project-c40x.onrender.com")
//                         .allowedMethods("GET", "POST", "PUT", "DELETE")
//                         .allowedHeaders("*")
//                         .allowCredentials(true);
//             }
//         };
//     }
// }
