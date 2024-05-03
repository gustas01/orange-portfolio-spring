package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateTagDTO;
import com.orange.porfolio.orange.portfolio.DTOs.TagDTO;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.repositories.TagsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagsService {
  private TagsRepository tagsRepository;
  private ModelMapper mapper;

  public TagsService(TagsRepository tagsRepository, ModelMapper mapper) {
    this.tagsRepository = tagsRepository;
    this.mapper = mapper;
  }

  public List<Tag> findAll(){
    return this.tagsRepository.findAll();
  }

  public TagDTO create(CreateTagDTO createTagDTO){
    if (createTagDTO.getTagName() == null || createTagDTO.getTagName().isEmpty()) throw new BadRequestRuntimeException("Campos obrigatórios estão faltando");
    Tag newTag = mapper.map(createTagDTO, Tag.class);
    return mapper.map(this.tagsRepository.save(newTag), TagDTO.class);
  }

  public String delete(int id){
    Tag tag = this.tagsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tag inexistente"));
    this.tagsRepository.delete(tag);
    return "Tag deletada com sucesso";
  }

  public String update(int id, CreateTagDTO updateTagDTO){
    if (updateTagDTO.getTagName() == null || updateTagDTO.getTagName().isEmpty()) throw new BadRequestRuntimeException("Campos obrigatórios estão faltando");
    return this.tagsRepository.findById(id).map(t -> {
      t.setTagName(updateTagDTO.getTagName());
      this.tagsRepository.save(t);
      return "Tag atualizada com sucesso!";
    }).orElseThrow(() -> new EntityNotFoundException("Tag não encontrada!"));
  }
}
