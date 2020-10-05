package com.hins.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author:Wyman
 * @Date: 2020-10-03
 */
@Component
@PropertySource(value = "classpath:file-upload-prod.properties")
@ConfigurationProperties(prefix = "file")
public class FileUploadResource {

    /**
     * 文件服务器地址
     */
    private String imageUserFaceLocation;

    /**
     * 服务地址
     */
    private String imageServerUrl;

    public String getImageUserFaceLocation() {
        return imageUserFaceLocation;
    }

    public void setImageUserFaceLocation(String imageUserFaceLocation) {
        this.imageUserFaceLocation = imageUserFaceLocation;
    }

    public String getImageServerUrl() {
        return imageServerUrl;
    }

    public void setImageServerUrl(String imageServerUrl) {
        this.imageServerUrl = imageServerUrl;
    }
}
