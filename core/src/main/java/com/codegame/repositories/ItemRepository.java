package com.codegame.repositories;

import com.codegame.dto.ItemDto;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(nativeQuery = true)
    List<ItemDto> getItemDetails();
}
