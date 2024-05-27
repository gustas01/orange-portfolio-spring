package com.orange.porfolio.orange.portfolio.services;

import com.orange.porfolio.orange.portfolio.DTOs.CreateTagDTO;
import com.orange.porfolio.orange.portfolio.DTOs.TagDTO;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.exceptions.BadRequestRuntimeException;
import com.orange.porfolio.orange.portfolio.repositories.TagsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagsService {
  private TagsRepository tagsRepository;
  private ModelMapper mapper;

  public TagsService(TagsRepository tagsRepository, ModelMapper mapper) {
    this.tagsRepository = tagsRepository;
    this.mapper = mapper;
  }

  public List<Tag> findAll(){
    return this.tagsRepository.findAllByActive(true);
  }

  public TagDTO create(CreateTagDTO createTagDTO){
    Optional<Tag> tag = this.tagsRepository.findOneByTagName(createTagDTO.getTagName());
    if (tag.isPresent()){
      if (tag.get().getActive())
        throw new BadRequestRuntimeException("Tag já existe!");
      tag.get().setActive(true);
      this.tagsRepository.save(tag.get());
      return mapper.map(tag, TagDTO.class);
    }
    Tag newTag = mapper.map(createTagDTO, Tag.class);
    return mapper.map(this.tagsRepository.save(newTag), TagDTO.class);
  }

  public String delete(int id){
    Tag tag = this.tagsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tag inexistente"));
    tag.setActive(false);
    this.tagsRepository.save(tag);
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
