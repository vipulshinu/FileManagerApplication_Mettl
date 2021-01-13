package com.fileStorage.fileManager.controller;

import com.fileStorage.fileManager.service.FileManagerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileManagerController {

    @Autowired
    FileManagerServiceImpl fileManagerService;

    private final Logger logger = LoggerFactory.getLogger(FileManagerController.class);
    private final static List<String> imageExt = Arrays.asList(".img",".png",".jpg",".gif");

    @GetMapping("/view/{fileName}")
    public ResponseEntity<?> viewFile(@PathVariable("fileName") String file) throws IOException {
        logger.info(file);
        byte[] data = fileManagerService.viewUploadedFile(file);
        if(data.length==0){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        String ext = file.substring(file.length() - 4);
        logger.info("File Extension: {}",ext);
        if(imageExt.contains(ext) || ext.equals(".jpeg"))
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(data);
        else if(ext.equals(".pdf") || ext.equals(".csv") || ext.equals(".xlsx")){
            Resource resource = new ClassPathResource("Documents" + "/" + file);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(resource.getURL().getPath());
        }
       else return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(data);
    }

    @GetMapping("/viewAllFiles")
    public ResponseEntity<List<Map<String,String>>> retrieveAllFiles(){
        return new ResponseEntity<>(fileManagerService.viewAllUploadedFiles(),HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile uploadfile) {
        logger.debug("Single file upload!");
        if (uploadfile.isEmpty()) {
            return new ResponseEntity("please select a file!", HttpStatus.BAD_REQUEST);
        }
        fileManagerService.saveUploadedFile(uploadfile);
        return new ResponseEntity("Successfully uploaded - " +
                uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.CREATED);

    }

    @DeleteMapping("remove/{fileName}")
    public ResponseEntity<?> deleteFile(@PathVariable("fileName") String file){
        fileManagerService.deleteUploadedFile(file);
        return new ResponseEntity("Successfully deleted - " +
                file, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping(value = "/updateFile/{fileName}", consumes = {MediaType.APPLICATION_JSON_VALUE},produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateFile(@PathVariable("fileName")String file, @RequestBody Map<String,String> textData){
            fileManagerService.updateUploadedFile(file,textData);
        return new ResponseEntity("Successfully Renamed - " +
                file + " TO " + textData.get("updateFileName"), new HttpHeaders(), HttpStatus.OK);
    }




}
