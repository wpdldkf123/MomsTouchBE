package com.momstouch.momstouchbe.domain.order.model;

import com.momstouch.momstouchbe.domain.shop.model.Menu;
import com.momstouch.momstouchbe.global.domain.Money;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="menu_id")
    private Menu menu;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name="order_menu_id")
    private List<OrderOptionGroup> orderOptionGroupList = new ArrayList<>();

    private Integer count;

    public void order(Order order) {
        if(this.order == null || !this.order.equals(order)) {
            this.order = order;
            order.addOrderMenu(this);
        }
    }

    public void addOrderOptionGroup(OrderOptionGroup orderOptionGroup) {
        orderOptionGroupList.add(orderOptionGroup);
    }

    public Money getTotalPrice() {
        Money menuPrice = menu.getPrice();
        for (OrderOptionGroup orderOptionGroup : orderOptionGroupList) {
            menuPrice = menuPrice.plus(orderOptionGroup.totalPrice());
        }

        return menu.applyDiscountPolicy(menuPrice.times(count));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderMenu orderMenu = (OrderMenu) o;
        return Objects.equals(getId(), orderMenu.getId()) && Objects.equals(getOrder(), orderMenu.getOrder()) && Objects.equals(getMenu(), orderMenu.getMenu()) && Objects.equals(getOrderOptionGroupList(), orderMenu.getOrderOptionGroupList()) && Objects.equals(getCount(), orderMenu.getCount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOrder(), getMenu(), getOrderOptionGroupList(), getCount());
    }
}
