package com.orange.porfolio.orange.portfolio.repositories;

import com.orange.porfolio.orange.portfolio.DTOs.ProjectDTO;
import com.orange.porfolio.orange.portfolio.entities.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectsRepository extends JpaRepository<Project, UUID> {
//  @Query("SELECT p FROM Project p WHERE p.author_id=:id")
  Page<Project> findAllByAuthorId(UUID id, Pageable pageable);
}
