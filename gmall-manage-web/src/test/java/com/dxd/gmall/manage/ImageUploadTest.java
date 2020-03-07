package com.dxd.gmall.manage;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageUploadTest {

    @Test
    public void testImageUpload(){
        String path = ImageUploadTest.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(path);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer,null);

            String localFileName = "E:\\applications\\图片\\95ca7e1e39b50b48.jpg";
            String fileExtName = "jpg";
            //String[] metaList = null;
            String[] uploadedFileInfos = storageClient.upload_file(localFileName, fileExtName, null);
            for (String uploadedFileInfo : uploadedFileInfos) {
                System.out.println(uploadedFileInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
