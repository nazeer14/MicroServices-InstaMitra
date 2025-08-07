package com.pack.repository;

import com.pack.common.enums.OrderStatus;
import com.pack.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Order> findByOrderNumber(String orderNumber);
    Optional<Order> findByTransactionId(String transactionId);
    Optional<Order> findByRefundTransactionId(String refundTransactionId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByProviderId(Long providerId);
    List<Order> findByUserId(Long userId);

    @Query(value = """
    SELECT o FROM Order o 
    WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(o.latitude)) 
    * cos(radians(o.longitude) - radians(:lng)) 
    + sin(radians(:lat)) * sin(radians(o.latitude)))) < :radius
""",nativeQuery = true)
    List<Order> findWithinRadius(@Param("lat") double lat,
                                 @Param("lng") double lng,
                                 @Param("radius") double radius);

    boolean existsByOrderNumber(String orderNumber);

    List<Order> findByProviderIdAndStatus(Long providerId, String status);

}
