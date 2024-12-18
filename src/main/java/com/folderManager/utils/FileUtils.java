package com.folderManager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.io.Files;

public class FileUtils {

	public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
	    if (fileToZip.isHidden()) {
	        return;
	    }
	    if (fileToZip.isDirectory()) {
	        if (fileName.endsWith("/")) {
	            zipOut.putNextEntry(new ZipEntry(fileName));
	            zipOut.closeEntry();
	        } else {
	            zipOut.putNextEntry(new ZipEntry(fileName + "/"));
	            zipOut.closeEntry();
	        }
	        File[] children = fileToZip.listFiles();
	        for (File childFile : children) {
	            zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
	        }
	        return;
	    }
	    FileInputStream fis = new FileInputStream(fileToZip);
	    ZipEntry zipEntry = new ZipEntry(fileName);
	    zipOut.putNextEntry(zipEntry);
	    byte[] bytes = new byte[1024];
	    int length;
	    while ((length = fis.read(bytes)) >= 0) {
	        zipOut.write(bytes, 0, length);
	    }
	    fis.close();	    
	}

	public static byte[] getFolder(String path, String name) throws IOException {
        FileOutputStream fos = new FileOutputStream(name + ".zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        
        File fileToZip = new File(path);
        FileUtils.zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
        
        File fileZip = new File(name + ".zip");
        InputStream inputStream = new FileInputStream(fileZip);
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        
        if (fileZip.delete()) {
        	System.out.println("Delete temp file success!");
        } else {
        	System.out.println("Cannot delete temp file!");
        }
        
        return byteArray;
	}
	
	public static void saveFile(MultipartFile file, String path) throws IllegalStateException, IOException {
		File fileUpload = new File(path);
		file.transferTo(fileUpload);
	}
}
