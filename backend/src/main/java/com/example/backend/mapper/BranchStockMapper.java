package com.example.backend.mapper;

import com.example.backend.dto.BranchStockResponseDTO;
import com.example.backend.model.BranchStockEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BranchStockMapper {

    @Mapping(source = "branch.branchId", target = "branchId")
    @Mapping(source = "branch.name", target = "branchName")
    @Mapping(source = "ingredient.ingredientId", target = "ingredientId")
    @Mapping(source = "ingredient.name", target = "ingredientName")
    @Mapping(source = "ingredient.sku", target = "sku")
    @Mapping(source = "ingredient.unit", target = "unit")
    @Mapping(source = "ingredient.costPerUnit", target = "costPerUnit")
    BranchStockResponseDTO toResponseDTO(BranchStockEntity entity);
}
