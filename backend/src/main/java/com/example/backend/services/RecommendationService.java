package com.example.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.backend.dto.MenuItemResponseDTO;
import com.example.backend.mapper.MenuItemMapper;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;

@Service
public class RecommendationService {

    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    public RecommendationService(OrderItemRepository orderItemRepository,
                                MenuItemRepository menuItemRepository,
                                MenuItemMapper menuItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.menuItemMapper = menuItemMapper;
    }

    public List<MenuItemResponseDTO> getFrequentlyBoughtWith(Long menuItemId) {
        // Find top 4 items bought with this one
        List<Object[]> results = orderItemRepository.findFrequentlyBoughtTogether(menuItemId, PageRequest.of(0, 4));

        return results.stream()
                .map(row -> {
                    Long id = (Long) row[0];
                    return menuItemRepository.findById(id).orElse(null);
                })
                .filter(item -> item != null)
                .map(menuItemMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
