package com.retail.backend.controller;

import com.retail.backend.model.Inventory;
import com.retail.backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Inventory>> getInventoryByStoreId(@PathVariable Long storeId) {
        return ResponseEntity.ok(inventoryService.getInventoryByStoreId(storeId));
    }

    @PutMapping("/store/{storeId}/product/{productId}")
    public ResponseEntity<Inventory> updateStockLevel(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(inventoryService.updateStockLevel(storeId, productId, quantity));
    }
}
