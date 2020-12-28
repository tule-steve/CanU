package com.codegame.services;

import com.codegame.dto.AddGiftCardRequest;
import com.codegame.dto.ItemDto;
import com.codegame.dto.RefundRequest;
import com.codegame.exception.GlobalValidationException;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    final GiftCodeRepository giftRepo;

    final ItemRepository itemRepo;

    final EntityManager em;

    public void refund(RefundRequest request) {
        List<GiftCard> refundCodes = giftRepo.getRefundCodes(request.getCodes(), request.getOrderId());
        int refundCodeCount = request.getCodes().size();
        int existingCodeCount = refundCodes.size();

        if (refundCodeCount != existingCodeCount) {
            throw new GlobalValidationException("Code list not matching");
        }

        refundCodes.forEach(r -> r.setStatus(GiftCard.GiftCardStatus.REFUNDING));
        giftRepo.saveAll(refundCodes);
    }

    public List<GiftCard> getCodeByItem(Long itemId) {
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new GlobalValidationException("Item not exist"));
        return giftRepo.getCodeByItemId(item.getId());
    }

    public void createItem(List<Item> data){
        itemRepo.saveAll(data);
    }

    public void addGiftCard(AddGiftCardRequest data){
        Item item = itemRepo.findById(data.getItemId()).orElseThrow(() -> new GlobalValidationException("Item not exist"));

        List<GiftCard> newGiftCardList = new ArrayList<>();
        for(String code : data.getCodes()){
            GiftCard gc = new GiftCard();
            gc.setGiftCode(code);
            gc.setItem(item);
            gc.setStatus(GiftCard.GiftCardStatus.AVAILABLE);
            newGiftCardList.add(gc);
            item.addGiftCard(gc);
        }

        giftRepo.saveAll(newGiftCardList);
    }

    public List<ItemDto> getItemDetails(){

        return itemRepo.getItemDetails();
    }
}
