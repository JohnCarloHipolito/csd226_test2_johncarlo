package com.johncarlo.coffeeshop.service;

import com.johncarlo.coffeeshop.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> getOrders(User user) {
        return orderRepository.findByUser(user);
    }
}
