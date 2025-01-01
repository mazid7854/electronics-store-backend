package com.mazid.electronic.store.services.implementation;

import com.mazid.electronic.store.exceptions.BadApiRequestException;
import com.mazid.electronic.store.exceptions.ResourceNotFoundException;
import com.mazid.electronic.store.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImplementation implements FileService {

    Logger logger= LoggerFactory.getLogger(FileServiceImplementation.class);
    @Override
    public String uploadFile(MultipartFile file, String path,String id) {
        //Get the original file name
        String originalFilename = file.getOriginalFilename();
        logger.info("Uploading file: {}", originalFilename);

        //Generate a unique file name
//        String fileName= UUID.randomUUID().toString();

        //Get the file extension
        assert originalFilename != null;
        String extension= originalFilename.substring(originalFilename.lastIndexOf("."));

        // get file name without extension
        String fileNameWithoutExtension= originalFilename.substring(0,originalFilename.lastIndexOf("."));

        //concatenate the file name and extension
        String fileNameWithExtension= id+fileNameWithoutExtension+extension;
        String fullPathWithExtension= path+ File.separator+fileNameWithExtension;

        if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".webp")) {

            //Upload the file to the specified path
            File folder=new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            try {
                // check if the file already exists
                Path path1 = Paths.get(fullPathWithExtension);
                if (Files.exists(path1)) {
                    throw new BadApiRequestException("File already exists");

                } else {
                    Files.copy(file.getInputStream(), path1);
                }
            } catch (Exception e) {
                throw new BadApiRequestException("File already exists");
            }

        }else {
               throw new BadApiRequestException("File : "+extension+" is not supported");
        }



        return fileNameWithExtension;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        String fullPath= null;
        try {
            fullPath = path+ File.separator+fileName;
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("File not found");
        }
        return new FileInputStream(fullPath);
    }
}
