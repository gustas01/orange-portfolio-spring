package com.orange.porfolio.orange.portfolio.repositories;

import com.orange.porfolio.orange.portfolio.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Integer> {
  Optional<Tag> findOneByTagName(String tagName);
  List<Tag> findAllByActive(Boolean status);
}
