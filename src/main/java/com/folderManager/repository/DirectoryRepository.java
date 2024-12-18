package com.folderManager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.folderManager.entity.Directory;

public interface DirectoryRepository extends CrudRepository<Directory, Integer>{
	
	public List<Directory> findByIdParent(int idParent);	
	
	public List<Directory> findByIdParentAndName(int idParent, String name);	
}
