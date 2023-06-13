package com.driver;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class OrderService {


    OrderRepository orderRepository = new OrderRepository();

    public void addOrder(Order order) {
        orderRepository.addOrder(order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        orderRepository.addPartner(partner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) throws RuntimeException {
        Optional<Order> orderOptional = orderRepository.getOrderById(orderId);
        Optional<DeliveryPartner> partnerOptional = orderRepository.getPartnerById(partnerId);
        // assuming this is not for update
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order id not present: " + orderId);
        }
        if (partnerOptional.isEmpty()) {
            throw new RuntimeException("Partner id not present: " + partnerId);
        }

        DeliveryPartner partner = partnerOptional.get();
        partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
        orderRepository.addPartner(partner);
        orderRepository.addOrderPartnerPair(orderId, partnerId);
    }

    public Order getOrderById(String orderId) throws RuntimeException {
        Optional<Order> orderOptional = orderRepository.getOrderById(orderId);
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        }
        throw new RuntimeException("Order not found");
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        Optional<DeliveryPartner> partnerOptional = orderRepository.getPartnerById(partnerId);
        if (partnerOptional.isPresent()) {
            return partnerOptional.get();
        }
        throw new RuntimeException("Partner not found");
    }

    public Integer gerOrderCountForPartner(String partnerId) {
        List<String> orders = orderRepository.getOrdersForPartner(partnerId);
        return orders.size();
    }

    public List<String> getOrderByPartnerId(String partnerId) {
        return orderRepository.getAllOrderForPartner(partnerId);
    }

    public List<String> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Integer getUnassignedOrders() {
        return orderRepository.getAllOrders().size() - orderRepository.getAssignedOrders().size();
    }

    public Integer getOrdersLeftForPartnerAfterTime(String time, String partnerId) {
        List<String> orderIds = orderRepository.getOrdersForPartner(partnerId);
        List<Order> orders = new ArrayList<>();
        for(String id : orderIds) {
            Order order = orderRepository.getOrderById(id).get();
            if(order.getDeliveryTime() > TimeUtil.convertTime(time)) {
                orders.add(order);
            }
        }
        return orders.size();
    }

    public String getLastDeliveryTimeByPartner(String partnerId) {
        List<String> orderIds = orderRepository.getAllOrderForPartner(partnerId);
        int maxTime = 0;
        for(String orderId : orderIds) {
            int deliveryTime = orderRepository.getOrderById(orderId).get().getDeliveryTime();
            if (deliveryTime > maxTime) {
                maxTime = deliveryTime;
            }
        }
        return TimeUtil.convertTime(maxTime);
    }

    public void deletePartner(String partnerId) {
        List<String> orderIds = orderRepository.getAllOrderForPartner(partnerId);
        orderRepository.deletePartner(partnerId);
        for (String id : orderIds) {
            orderRepository.unAssignOrder(id);
        }
    }

    public void deleteOrder(String orderId) {
        Optional<String> partnerIdOpt = orderRepository.getPartnerForOrder(orderId);
        orderRepository.deleteOrder(orderId);

        if (partnerIdOpt.isPresent()) {
            List<String> orderIds = orderRepository.getAllOrderForPartner(partnerIdOpt.get());
            orderIds.remove(orderId);
        }
    }
}