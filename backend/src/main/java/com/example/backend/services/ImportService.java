package com.example.backend.services;

import com.example.backend.model.CategoryEntity;
import com.example.backend.model.MenuItemEntity;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.model.BranchStockEntity;
import com.example.backend.model.IngredientEntity;
import com.example.backend.model.BranchEntity;
import com.example.backend.repository.BranchStockRepository;
import com.example.backend.repository.IngredientRepository;
import com.example.backend.repository.BranchRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Optional;

@Service
public class ImportService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final BranchStockRepository branchStockRepository;
    private final BranchRepository branchRepository;

    public ImportService(MenuItemRepository menuItemRepository, 
                         CategoryRepository categoryRepository,
                         IngredientRepository ingredientRepository,
                         BranchStockRepository branchStockRepository,
                         BranchRepository branchRepository) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.ingredientRepository = ingredientRepository;
        this.branchStockRepository = branchStockRepository;
        this.branchRepository = branchRepository;
    }

    @Transactional
    public void importMenuItems(MultipartFile file) throws IOException, CsvValidationException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.toLowerCase().endsWith(".xlsx")) {
            importMenuItemsFromExcel(file);
        } else {
            importMenuItemsFromCsv(file);
        }
    }

    private void importMenuItemsFromExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row row = rows.next();
                String name = getCellValueAsString(row.getCell(0));
                String categoryName = getCellValueAsString(row.getCell(1));
                Double basePrice = getCellValueAsDouble(row.getCell(2));

                if (name == null || name.isEmpty()) continue;

                saveOrUpdateMenuItem(name, categoryName, basePrice);
            }
        }
    }

    private void importMenuItemsFromCsv(MultipartFile file) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                if (line.length < 3) continue;
                String name = line[0];
                String categoryName = line[1];
                
                Double basePrice = null;
                try {
                    basePrice = Double.parseDouble(line[2]);
                } catch (NumberFormatException e) {
                    continue;
                }

                saveOrUpdateMenuItem(name, categoryName, basePrice);
            }
        }
    }

    private void saveOrUpdateMenuItem(String name, String categoryName, Double basePrice) {
        Optional<MenuItemEntity> existing = menuItemRepository.findByNameAndDeletedAtIsNull(name);
        MenuItemEntity item = existing.orElse(new MenuItemEntity());
        
        item.setName(name);
        if (basePrice != null) {
            item.setBasePrice(basePrice);
        } else if (item.getBasePrice() == null) {
            item.setBasePrice(0.0);
        }
        
        if (categoryName != null && !categoryName.isEmpty()) {
            CategoryEntity category = categoryRepository.findByNameAndDeletedAtIsNull(categoryName)
                    .orElseGet(() -> {
                        CategoryEntity newCat = new CategoryEntity();
                        newCat.setName(categoryName);
                        return categoryRepository.save(newCat);
                    });
            item.setCategory(category);
        }
        
        menuItemRepository.save(item);
    }

    @Transactional
    public void importIngredientStock(Long branchId, MultipartFile file) throws IOException, CsvValidationException {
        BranchEntity branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found with ID: " + branchId));

        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.toLowerCase().endsWith(".xlsx")) {
            importIngredientStockFromExcel(branch, file);
        } else {
            importIngredientStockFromCsv(branch, file);
        }
    }

    private void importIngredientStockFromExcel(BranchEntity branch, MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            if (rows.hasNext()) rows.next(); // skip header

            while (rows.hasNext()) {
                Row row = rows.next();
                String sku = getCellValueAsString(row.getCell(0));
                Double newStock = getCellValueAsDouble(row.getCell(1));

                if (sku == null || sku.isEmpty() || newStock == null) continue;
                updateStock(branch, sku, newStock);
            }
        }
    }

    private void importIngredientStockFromCsv(BranchEntity branch, MultipartFile file) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            reader.readNext(); // skip header
            while ((line = reader.readNext()) != null) {
                if (line.length < 2) continue;
                String sku = line[0];
                Double newStock = null;
                try {
                    newStock = Double.parseDouble(line[1]);
                } catch (NumberFormatException e) {
                    continue;
                }
                updateStock(branch, sku, newStock);
            }
        }
    }

    private void updateStock(BranchEntity branch, String sku, Double newStock) {
        ingredientRepository.findBySkuAndDeletedAtIsNull(sku).ifPresent(ingredient -> {
            Optional<BranchStockEntity> stockOpt = branchStockRepository.findByBranchBranchIdAndIngredientIngredientIdAndDeletedAtIsNull(branch.getBranchId(), ingredient.getIngredientId());
            BranchStockEntity stock = stockOpt.orElseGet(() -> {
                BranchStockEntity newStockEntity = new BranchStockEntity();
                newStockEntity.setBranch(branch);
                newStockEntity.setIngredient(ingredient);
                return newStockEntity;
            });
            stock.setCurrentStock(newStock);
            branchStockRepository.save(stock);
        });
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return null;
        }
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
