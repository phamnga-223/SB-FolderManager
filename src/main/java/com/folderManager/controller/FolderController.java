package com.folderManager.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.folderManager.entity.BaseResponse;
import com.folderManager.entity.Directory;
import com.folderManager.service.DirectoryService;

@RequestMapping("/")
@Controller
public class FolderController {

	@Autowired
	private DirectoryService directoryService;
	
	@GetMapping
	public String folder(Model model) {
//		String path = "D:\\Dowload";
//		File directoryPath = new File(path);
//		File[] fileList = directoryPath.listFiles();
//		String[] files = directoryPath.list();
//		String[] types = new String[fileList.length];
//		int i = 0;
//		
//		for (File file : fileList) {
//			
//			if (file.isDirectory()) {
//				types[i++] = "DIR";
//			} else if (file.isFile()) {
//				types[i++] = "FIL";
//			}
//		}
//		
//		model.addAttribute("path", path);
//		model.addAttribute("files", files);
//		model.addAttribute("types", types);
		
		List<Directory> listDirectory = directoryService.findByIdParent(0);
		
		model.addAttribute("listDirectory", listDirectory);
	
		return "folders/folders";
	}

	@GetMapping(value = "/details")
	public String detail(Model model, @RequestParam int id) {
		
		String fullPath = directoryService.findFullPath(id);
		List<Directory> listFiles = directoryService.findByIdParent(id);
		Directory directory = directoryService.findById(id);
		
		model.addAttribute("idParent", directory.getIdParent());
		model.addAttribute("fullPath", fullPath);
		model.addAttribute("listFiles", listFiles);
		
		return "folders/details";
	}
	
	@PostMapping(value = "/update") 
	public ResponseEntity<?> update(@RequestParam("id") int id, @RequestParam("name") String name) {
		Directory directory = directoryService.updateDirectory(id, name);
		BaseResponse response = new BaseResponse();
		
		if (directory != null) {
			response.setCode(200);
		} else {
			response.setCode(500);
		}
		response.setData(directory);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping(value = "/delete")
	public ResponseEntity<?> delete(@RequestParam int id) {
		BaseResponse response = new BaseResponse();		
		int idParent = directoryService.deleteDirectory(id);
		
		if (idParent >= 0) {
			response.setCode(200);
			response.setData(idParent);
		} else {
			response.setCode(500);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping(value = "/download")
	public ResponseEntity<?> download(@RequestParam int id) {
		BaseResponse response = new BaseResponse();
		String[] result = directoryService.getFile(id);
		if (result[0] != null) {
			response.setCode(200);
			response.setData(result);
		} else {
			response.setCode(500);
		}

		return ResponseEntity.ok(response);
	}
	
	@PostMapping(value = "/upload/file")
	public ResponseEntity<?> uploadFile(@RequestParam("id") int idDir,
			@RequestParam("file") MultipartFile file) {
		BaseResponse response = new BaseResponse();
		if (directoryService.saveFile(idDir, file)) {
			response.setCode(200);
			response.setData(idDir);
		} else {
			response.setCode(500);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(value = "upload/folder")
	public ResponseEntity<?> uploadFolder(@RequestParam("id") int idDir,
			@RequestParam("folder") MultipartFile[] folder) {
		BaseResponse response = new BaseResponse();
		if (directoryService.saveFolder(idDir, folder)) {
			response.setCode(200);
			response.setData(idDir);
		} else {
			response.setCode(500);
		}
		
		return ResponseEntity.ok(response);
	}
}
