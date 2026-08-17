package com.retail.backend.repository;

import com.retail.backend.model.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {

    @Query(value = "CALL GetMonthlySalesForEachStore(:year, :month)", nativeQuery = true)
    List<Map<String, Object>> callGetMonthlySalesForEachStore(@Param("year") int year, @Param("month") int month);

    @Query(value = "CALL GetAggregateSalesForCompany(:year, :month)", nativeQuery = true)
    List<Map<String, Object>> callGetAggregateSalesForCompany(@Param("year") int year, @Param("month") int month);

    @Query(value = "CALL GetTopSellingProductsByCategory(:limitNum, :year)", nativeQuery = true)
    List<Map<String, Object>> callGetTopSellingProductsByCategory(@Param("limitNum") int limitNum, @Param("year") int year);
}
