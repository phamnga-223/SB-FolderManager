package com.folderManager.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.folderManager.entity.Directory;
import com.folderManager.repository.DirectoryRepository;
import com.folderManager.utils.FileUtils;

@Service
public class DirectoryService {

	private static final String DIR = "D:/Dowload/test/QL-Folder";

	@Autowired
	private DirectoryRepository directoryRepository;

	public List<Directory> findByIdParent(int idParent) {
		List<Directory> listDirectory = new ArrayList<>();
		directoryRepository.findByIdParent(idParent).forEach(directory -> listDirectory.add(directory));

		return listDirectory;
	}

	public String findFullPath(int id) {
		String result = "";

		if (!directoryRepository.existsById(id)) {
			return result;
		}
		
		Directory file = directoryRepository.findById(id).get();
		int idParent = file.getIdParent();
		while (idParent != 0) {
			Directory parent = directoryRepository.findById(idParent).get();

			result = parent.getName() + "/" + result;
			idParent = parent.getIdParent();
		}

		if (!result.equals("")) {
			result += "/";
		}
		result += file.getName();

		return result;
	}

	public Directory findById(int id) {
		Directory directory = null;

		if (directoryRepository.existsById(id)) {
			directory = directoryRepository.findById(id).get();
		}

		return directory;
	}

	public Directory updateDirectory(int id, String name) {
		Directory directory = null;

		if (directoryRepository.existsById(id)) {
			directory = directoryRepository.findById(id).get();
			directory.setName(name);
			Timestamp time = new Timestamp(System.currentTimeMillis());
			directory.setUpdated(time);

			directory = directoryRepository.save(directory);
		}

		return directory;
	}

	public int deleteDirectory(int id) {
		int idParent = -1;

		if (directoryRepository.existsById(id)) {
			Directory directory = directoryRepository.findById(id).get();
			idParent = directory.getIdParent();
			directoryRepository.deleteById(id);
		}

		return idParent;
	}

	public String[] getFile(int id) {
		String[] result = { null, null };

		if (directoryRepository.existsById(id)) {
			String path = DIR + "/" + findFullPath(id);
			Directory directory = directoryRepository.findById(id).get();

			try {
				switch (directory.getType()) {
				case Directory.TYPE_FILE:
					File file = new File(path);
					InputStream inputStream = new FileInputStream(file);
					byte[] bytes = IOUtils.toByteArray(inputStream);
					result[0] = directory.getName();
					result[1] = Base64.getEncoder().encodeToString(bytes);

					break;
				case Directory.TYPE_DIR:
					byte[] bytesDir = FileUtils.getFolder(path, directory.getName());
					result[0] = directory.getName() + ".zip";
					result[1] = Base64.getEncoder().encodeToString(bytesDir);

					break;
				default:
					break;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public boolean saveFile(int idDir, MultipartFile file) {
		boolean result = false;

		try {
			String fileName = file.getOriginalFilename();
			String pathDir = findFullPath(idDir);

			// Save file
			if (fileName.contains("/")) {
				String[] names = fileName.split("/");
				fileName = names[names.length - 1];
			}
			String destinationPath = DIR + "/" + pathDir + "/" + fileName;
			FileUtils.saveFile(file, destinationPath);

			// Save into DB
			Directory directory = new Directory();
			directory.setName(fileName);
			directory.setIdParent(idDir);
			directory.setType(Directory.TYPE_FILE);
			Timestamp time = new Timestamp(System.currentTimeMillis());
			directory.setCreated(time);
			directory.setUpdated(time);
			directory = directoryRepository.save(directory);

			result = true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			result = false;
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}
	
	public boolean saveFolder(int idDir, MultipartFile[] folder) {
		boolean result = false;
		String folderUpload = null;
		
		for (int i = 0; i < folder.length; i++) {
			MultipartFile file = folder[i];
			String path = file.getOriginalFilename();
			String[] folderNames = path.split("/");
			int idParent = idDir;
			String testPath = DIR + "/" + findFullPath(idParent);
			for (int j = 0; j < folderNames.length -1; j++) {
				testPath += "/" + folderNames[j];
				File testFile = new File(testPath);
				if (!testFile.exists() && j < folderNames.length - 1) {
					// Create folder
					testFile.mkdir();
					
					// Save into DB
					Directory directory = new Directory();
					directory.setName(testFile.getName());
					directory.setIdParent(idParent);
					directory.setType(Directory.TYPE_DIR);
					Timestamp time = new Timestamp(System.currentTimeMillis());
					directory.setCreated(time);
					directory.setUpdated(time);
					directory = directoryRepository.save(directory);
					idParent = directory.getId();
				} else if (testFile.exists() && j < folderNames.length - 1) {
					List<Directory> diretories = directoryRepository.findByIdParentAndName(idParent, folderNames[j]);
					if (diretories != null) {
						Directory directory = diretories.get(0);
						idParent = directory.getId();
					}							
				}
			}
			
			result = saveFile(idParent, file);
		}
		
		return result;
	}
}
