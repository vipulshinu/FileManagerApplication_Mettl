package com.fileStorage.fileManager.service;

import com.fileStorage.fileManager.controller.FileManagerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class FileManagerServiceImpl implements IFileManagerService{
    private final Logger logger = LoggerFactory.getLogger(FileManagerServiceImpl.class);
    String folderPath = "Documents";

    @Override
    public void saveUploadedFile(MultipartFile file) {
        try{
            byte[] bytes = file.getBytes();
            Path path = Paths.get("src/main/resources/" + folderPath +"/"+ file.getOriginalFilename());
            Files.write(path, bytes);

        }
        catch (IOException e){
            logger.error("IOException" + e);
        }

    }

    @Override
    public byte[] viewUploadedFile(String file) {
        Resource resource = new ClassPathResource(folderPath + "/" + file);
        byte[] bdata = new byte[0];
        try {
            logger.info(resource.toString());
            InputStream inputStream = resource.getInputStream();
            bdata = FileCopyUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
        return bdata;
    }

    @Override
    public void deleteUploadedFile(String file) {
        Path path = Paths.get("src/main/resources/" + folderPath +"/"+ file);
        try{
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Map<String,String>> viewAllUploadedFiles() {
        File dir = new File("src/main/resources/" + folderPath +"/");
        List<Map<String,String>> dirList = new ArrayList<>();
        List<String> list = Arrays.asList(dir.list());
        for(int i=0;i<list.size();i++){
            Map<String,String> map = new HashMap<>();
            map.put("Name",list.get(i));
            dirList.add(map);
        }
        return dirList;
    }

    @Override
    public void updateUploadedFile(String file, Map<String,String> textData) {
        Path path = Paths.get("src/main/resources/" + folderPath +"/"+ file);
            try{
                if(!textData.get("updateFileName").isEmpty()){
                    Files.move(path,path.resolveSibling(textData.get("updateFileName")));
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }


    }
}
