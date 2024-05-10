package com.august.gulimall.thirdparty;

import com.aliyun.oss.*;
import com.aliyun.oss.model.PutObjectRequest;
import com.august.gulimall.thirdparty.config.AliOssConfigProperties;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallThirdPartyApplicationTests {
    @Autowired
    OSS ossClient;


    @Autowired
    AliOssConfigProperties aliOssConfigProperties;

    @Test
    void contextLoads() {
        // 创建OSSClient实例。
        try {
            String content = "Hello OSS";
            PutObjectRequest putObjectRequest = new PutObjectRequest("augustgulimall","1.txt" ,new ByteArrayInputStream(content.getBytes()));
            ossClient.putObject(putObjectRequest);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    @Test
    public void text() {
        System.out.println(aliOssConfigProperties);
    }

    @Test
    public void testEmail() {

        System.out.println("测试邮件发送");
    }
}
