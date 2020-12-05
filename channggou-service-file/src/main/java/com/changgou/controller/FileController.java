package com.changgou.controller;

import com.changgou.goods.entity.Result;
import com.changgou.goods.entity.StatusCode;
import com.changgou.utils.FastDFSClient;
import com.changgou.utils.FastDFSFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
/**
 * 文件增删查改操作
 */
public class FileController {
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        // 获取请求文件名称
        String originalFilename = file.getOriginalFilename();

        // 获取文件后缀
        int index = originalFilename.lastIndexOf(".");
        String extName = originalFilename.substring(index);

        String url;

        try {
            // 获取文件内容
            byte[] context = file.getBytes();

            // 执行上传
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, context, extName);
            String[] uploadResult = FastDFSClient.upload(fastDFSFile);

            // 获取远程文件组名
            String groupName = uploadResult[0];

            // 获取远程路径
            String remoteFilePath = uploadResult[1];

            url = FastDFSClient.getTrackerUrl() + "/" + groupName + "/" + remoteFilePath;

            // todo 添加存储文件到db
            return new Result<>(false, StatusCode.ERROR, "上传文件成功", url);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return new Result<>(false, StatusCode.ERROR, "上传文件失败");
    }


    @GetMapping("/download")
    public ResponseEntity<byte[]> download() {
        String groupName = "group1";
        String remoteFilePath = "M00/00/00/wKjfgl-gzImAWL_qAArwcjdHqTY37..jpg";
        // 或许下载文件字节流
        byte[] downloadFileContent = FastDFSClient.downFile2(groupName, remoteFilePath);
        HttpHeaders downloadHeaders = new HttpHeaders();

        // 设置下载请求头 setContentDispositionFormData的参数2为下载文件名称
        downloadHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        downloadHeaders.setContentDispositionFormData("attachment", "download.jpg");


        return new ResponseEntity<>(downloadFileContent, downloadHeaders, HttpStatus.CREATED);
    }

        @DeleteMapping("/remove")
    public Result<String> remove() {
        String groupName = "group1";
        String remoteFilePath = "M00/00/00/wKjfgl-gzImAWL_qAArwcjdHqTY37..jpg";

        try {
            // 文件删除操作
            FastDFSClient.deleteFile(groupName, remoteFilePath);
            return new Result<>(true, StatusCode.OK, "删除成功.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(false, StatusCode.ERROR, "删除失败.");
        }
    }
}
