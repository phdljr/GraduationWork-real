package com.brother.graduationwork.repository;

import com.brother.graduationwork.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {

    @Transactional
    void deleteAllByUserId(Long UserId);

    List<Order> findAllByUserId(Long UserId);
}
