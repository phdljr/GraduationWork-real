package com.brother.graduationwork.domain;

import com.brother.graduationwork.dto.OrderDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Getter
@Table(name = "ORDERS")
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ORDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    private String menuName;
    private int price;
    private int quantity;

    public Order(OrderDTO orderDTO) {
        this.menuName = orderDTO.getMenuName();
        this.price = orderDTO.getPrice();
        this.quantity = orderDTO.getQuantity();
    }

    public void setUser(User user) {
        this.user = user;
        user.getOrderList().add(this);
        user.addTotalAmount(this.price * this.quantity);
    }
}
