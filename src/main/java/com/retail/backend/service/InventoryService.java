package com.retail.backend.service;

import com.retail.backend.exception.ResourceNotFoundException;
import com.retail.backend.model.Inventory;
import com.retail.backend.model.Product;
import com.retail.backend.model.Store;
import com.retail.backend.repository.InventoryRepository;
import com.retail.backend.repository.ProductRepository;
import com.retail.backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, 
                            StoreRepository storeRepository, 
                            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    public List<Inventory> getInventoryByStoreId(Long storeId) {
        // Validate store exists first
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store not found with id: " + storeId);
        }
        return inventoryRepository.findByStoreId(storeId);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Inventory updateStockLevel(Long storeId, Long productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + storeId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Inventory inventory = inventoryRepository.findByStoreIdAndProductId(storeId, productId)
                .orElse(new Inventory(null, store, product, 0));

        inventory.setQuantity(quantity);
        return inventoryRepository.save(inventory);
    }
}
