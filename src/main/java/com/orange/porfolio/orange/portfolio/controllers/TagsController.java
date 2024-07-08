package com.orange.porfolio.orange.portfolio.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.porfolio.orange.portfolio.DTOs.CreateTagDTO;
import com.orange.porfolio.orange.portfolio.DTOs.TagDTO;
import com.orange.porfolio.orange.portfolio.entities.Tag;
import com.orange.porfolio.orange.portfolio.services.TagsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tags")
public class TagsController {
  private final TagsService tagsService;

  public TagsController(TagsService tagsService) {
    this.tagsService = tagsService;
  }

  @PostMapping
  @PreAuthorize("hasAuthority('admin')")
  public ResponseEntity<TagDTO> create(@RequestBody @Valid CreateTagDTO createTagDTO){
    return new ResponseEntity<>(this.tagsService.create(createTagDTO), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Tag>> findAll(){
    return ResponseEntity.ok(this.tagsService.findAll());
  }


  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('admin')")
  public ResponseEntity<String> delete(@PathVariable int id){
    return new ResponseEntity<>(this.tagsService.delete(id), HttpStatus.NO_CONTENT);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('admin')")
  public ResponseEntity<String> update(@PathVariable int id,@RequestBody @Valid CreateTagDTO updateTagDTO) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> res = new HashMap<>();

    res.put("message", this.tagsService.update(id, updateTagDTO));
    String json = mapper.writeValueAsString(res);
    return ResponseEntity.ok(json);
  }
}
