package com.albumx;

import com.albumx.infrastructure.config.AlbumProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AlbumProperties.class)
public class AlbumXApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlbumXApplication.class, args);
    }
}
