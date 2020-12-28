package com.codegame.model;

import com.codegame.dto.ItemDto;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Table(name = "tu_test_order")
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "order_id")
    Long orderId;

    @Column(name = "total_value")
    Long transactionTotal;

    @Column(name = "order_detail")
    String orderDetail;

    @Column(name = "created_at")
    @CreationTimestamp
    protected LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    List<GiftCard> codes = new ArrayList<>();

    public void addGiftCards(List<GiftCard> giftCards){
        giftCards.forEach(gc -> {
            gc.setOrder(this);
            gc.setStatus(GiftCard.GiftCardStatus.USED);
        });

        codes.addAll(giftCards);
    }
}
