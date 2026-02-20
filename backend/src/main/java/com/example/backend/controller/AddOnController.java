package com.example.backend.controller;

import com.example.backend.model.AddOnEntity;
import com.example.backend.repository.AddOnRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addons")
public class AddOnController {

    private final AddOnRepository addOnRepository;

    public AddOnController(AddOnRepository addOnRepository) {
        this.addOnRepository = addOnRepository;
    }

    @GetMapping
    public ResponseEntity<List<AddOnEntity>> getAllAddOns() {
        List<AddOnEntity> addOns = addOnRepository.findAll()
                .stream()
                .filter(a -> a.getDeletedAt() == null)
                .toList();
        return ResponseEntity.ok(addOns);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddOnEntity> getAddOnById(@PathVariable Long id) {
        return addOnRepository.findById(id)
                .filter(a -> a.getDeletedAt() == null)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AddOnEntity> createAddOn(@RequestBody AddOnEntity addOn) {
        AddOnEntity saved = addOnRepository.save(addOn);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddOnEntity> updateAddOn(@PathVariable Long id, @RequestBody AddOnEntity addOn) {
        return addOnRepository.findById(id)
                .map(existing -> {
                    existing.setName(addOn.getName());
                    existing.setNameKh(addOn.getNameKh());
                    existing.setPrice(addOn.getPrice());
                    return ResponseEntity.ok(addOnRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddOn(@PathVariable Long id) {
        return addOnRepository.findById(id)
                .map(existing -> {
                    existing.setDeletedAt(java.time.LocalDateTime.now());
                    addOnRepository.save(existing);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
