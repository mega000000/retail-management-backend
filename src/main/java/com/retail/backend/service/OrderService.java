package com.retail.backend.service;

import com.retail.backend.dto.OrderItemRequest;
import com.retail.backend.dto.OrderRequest;
import com.retail.backend.exception.InsufficientStockException;
import com.retail.backend.exception.ResourceNotFoundException;
import com.retail.backend.model.*;
import com.retail.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public OrderService(OrderDetailsRepository orderDetailsRepository,
                        CustomerRepository customerRepository,
                        StoreRepository storeRepository,
                        ProductRepository productRepository,
                        InventoryRepository inventoryRepository) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.customerRepository = customerRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public OrderDetails placeOrder(OrderRequest request) {
        // 1. Get or create Customer
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setName(request.getCustomerName());
                    newCustomer.setEmail(request.getCustomerEmail());
                    newCustomer.setPhoneNumber(request.getCustomerPhone());
                    return customerRepository.save(newCustomer);
                });

        // Update phone number if it was blank and is now provided
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isBlank() && 
                (customer.getPhoneNumber() == null || customer.getPhoneNumber().isBlank())) {
            customer.setPhoneNumber(request.getCustomerPhone());
            customer = customerRepository.save(customer);
        }

        // 2. Find Store
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found with id: " + request.getStoreId()));

        // 3. Create OrderDetails
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setOrderDate(LocalDateTime.now());
        orderDetails.setStatus("COMPLETED");

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 4. Process items
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId()));

            // Check Inventory stock
            Inventory inventory = inventoryRepository.findByStoreIdAndProductId(store.getId(), product.getId())
                    .orElseThrow(() -> new InsufficientStockException("No inventory record for product: " + product.getName() + " in store: " + store.getName()));

            if (inventory.getQuantity() < itemReq.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName() + 
                        ". Requested: " + itemReq.getQuantity() + ", Available: " + inventory.getQuantity());
            }

            // Deduct inventory stock
            inventory.setQuantity(inventory.getQuantity() - itemReq.getQuantity());
            inventoryRepository.save(inventory);

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            
            // Add to parent
            orderDetails.addOrderItem(orderItem);

            // Add to total
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        orderDetails.setTotalAmount(totalAmount);

        // 5. Save and return
        return orderDetailsRepository.save(orderDetails);
    }

    public List<OrderDetails> getAllOrders() {
        return orderDetailsRepository.findAll();
    }

    public OrderDetails getOrderById(Long id) {
        return orderDetailsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    // Stored Procedure Wrappers
    public List<Map<String, Object>> getMonthlySalesForEachStore(int year, int month) {
        return orderDetailsRepository.callGetMonthlySalesForEachStore(year, month);
    }

    public List<Map<String, Object>> getAggregateSalesForCompany(int year, int month) {
        return orderDetailsRepository.callGetAggregateSalesForCompany(year, month);
    }

    public List<Map<String, Object>> getTopSellingProductsByCategory(int limitNum, int year) {
        return orderDetailsRepository.callGetTopSellingProductsByCategory(limitNum, year);
    }
}
