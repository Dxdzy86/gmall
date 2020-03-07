package com.dxd.gmall.manage.util;

import org.csource.fastdfs.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

//@PropertySource(value={"classpath:application.properties"})
@Component
public class PmsUploadUtil {

    public static String imageUpload(MultipartFile multipartFile) {
        //文件服务器的url地址
//        String url = "http://192.168.30.128";
        String url = null;
        try {
//            ClientGlobal.initByProperties("/application.properties");
            String path = PmsUploadUtil.class.getResource("/tracker.conf").getPath();
            ClientGlobal.init(path);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();

            url = trackerServer.getSocket().getInetAddress().getHostAddress();
            StorageClient storageClient = new StorageClient(trackerServer, null);

            String localFileName = multipartFile.getOriginalFilename();
            int indexOfdot = localFileName.lastIndexOf(".");
            String fileExtName = localFileName.substring(indexOfdot + 1);//拿到文件的后缀名
            String[] uploadFileInfos = storageClient.upload_file(multipartFile.getBytes(), fileExtName, null);

            for (String uploadedFileInfo : uploadFileInfos) {
                url += "/" + uploadedFileInfo;
                System.out.println("imageUrl=" + url);
            }
            url = "http://" + url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
