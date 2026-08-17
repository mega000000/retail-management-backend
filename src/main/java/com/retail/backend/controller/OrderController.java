package com.retail.backend.controller;

import com.retail.backend.dto.OrderRequest;
import com.retail.backend.model.OrderDetails;
import com.retail.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDetails> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderDetails>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetails> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // Report Endpoints wrapping stored procedures
    @GetMapping("/reports/monthly-store-sales")
    public ResponseEntity<List<Map<String, Object>>> getMonthlySalesForEachStore(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(orderService.getMonthlySalesForEachStore(year, month));
    }

    @GetMapping("/reports/aggregate-company-sales")
    public ResponseEntity<List<Map<String, Object>>> getAggregateSalesForCompany(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(orderService.getAggregateSalesForCompany(year, month));
    }

    @GetMapping("/reports/top-selling-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProductsByCategory(
            @RequestParam(defaultValue = "3") int limit,
            @RequestParam int year) {
        return ResponseEntity.ok(orderService.getTopSellingProductsByCategory(limit, year));
    }
}
