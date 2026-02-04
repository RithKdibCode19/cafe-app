package com.example.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.dto.StockAdjustmentRequestDTO;
import com.example.backend.dto.StockAdjustmentResponseDTO;
import com.example.backend.mapper.StockAdjustmentMapper;
import com.example.backend.model.IngredientEntity;
import com.example.backend.model.StockAdjustmentEntity;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.StockAdjustmentRepository;

@ExtendWith(MockitoExtension.class)
public class StockAdjustmentServiceTest {

    @Mock
    private StockAdjustmentRepository stockAdjustmentRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private StockAdjustmentMapper stockAdjustmentMapper;

    @InjectMocks
    private StockAdjustmentService service;

    private IngredientEntity ingredient;
    private StockAdjustmentRequestDTO requestDTO;
    private StockAdjustmentEntity entity;
    private StockAdjustmentResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        ingredient = new IngredientEntity();
        ingredient.setIngredientId(1L);
        ingredient.setName("Milk");
        ingredient.setCurrentStock(10.0);

        requestDTO = new StockAdjustmentRequestDTO();
        requestDTO.setIngredientId(1L);
        requestDTO.setQtyChange(-2.0); // Wastage
        requestDTO.setReasonType("WASTAGE");

        entity = new StockAdjustmentEntity();
        entity.setAdjustmentId(100L);

        responseDTO = new StockAdjustmentResponseDTO();
        responseDTO.setAdjustmentId(100L);
    }

    @Test
    void createAdjustment_Success_Wastage() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(ingredient));
        when(stockAdjustmentMapper.toEntity(requestDTO)).thenReturn(entity);
        when(stockAdjustmentRepository.save(any(StockAdjustmentEntity.class))).thenReturn(entity);
        when(stockAdjustmentMapper.toResponseDTO(entity)).thenReturn(responseDTO);

        StockAdjustmentResponseDTO result = service.createAdjustment(requestDTO);

        assertNotNull(result);
        assertEquals(8.0, ingredient.getCurrentStock()); // 10 - 2

        verify(ingredientRepository).save(ingredient);
        verify(stockAdjustmentRepository).save(entity);
    }
}
