package com.springboot.file_service.repository;

import com.springboot.file_service.entity.FileRelationship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
@Repository
public interface IFileRelationshipRepository extends MongoRepository<FileRelationship,String> {
    List<FileRelationship> findAllByParentIdInAndParentType(Collection<String> parentIds, String parentType);
    FileRelationship findByPathFile(String pathFile);
    void deleteByPathFile(String pathFile);
    FileRelationship findAllByParentIdAndParentType(String parentId, String parentType);

}
