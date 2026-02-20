package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;
    private String nameKh;
    private String description;
    private String descriptionKh;
    
    private Long parentId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNameKh() { return nameKh; }
    public void setNameKh(String nameKh) { this.nameKh = nameKh; }

    public String getDescriptionKh() { return descriptionKh; }
    public void setDescriptionKh(String descriptionKh) { this.descriptionKh = descriptionKh; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
