package com.codegame.model;

import com.codegame.dto.ItemDto;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;
import static javax.persistence.GenerationType.SEQUENCE;
@SqlResultSetMapping(
        name = "itemDetailsMapping",
        classes = {
                @ConstructorResult(
                        targetClass = ItemDto.class,
                        columns = {
                                @ColumnResult(name = "itemId"),
                                @ColumnResult(name = "description"),
                                @ColumnResult(name = "count"),
                                @ColumnResult(name = "price"),
                                @ColumnResult(name = "createdAt")
                        }
                )
        }
)
@NamedNativeQuery(name = "Item.getItemDetails", query =
        "select itm.id as itemId, itm.description as description, count(gc.id) as count, itm.price as price, itm.created_at as createdAt " +
        " from tu_test_itm itm " +
        " left outer join tu_test_gc gc on itm.id = gc.item_id " +
        "group by itm.id",
        resultSetMapping = "itemDetailsMapping")
@Entity
@Data
@Table(name = "tu_test_itm")
public class Item {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    Long id;

    @NotBlank(message = "description id is required.")
    @Column(name = "description")
    String description;

    @Min(1)
    @Column(name = "price")
    int price = 0;

    @Column(name = "created_at")
    @CreationTimestamp
    protected LocalDateTime createdAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    List<GiftCard> codes = new ArrayList<>();

    public void addGiftCard(GiftCard gc){
        codes.add(gc);
    }
}
