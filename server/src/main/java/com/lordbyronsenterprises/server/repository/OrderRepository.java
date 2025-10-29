package com.lordbyronsenterprises.server.repository;

import com.lordbyronsenterprises.server.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
}
