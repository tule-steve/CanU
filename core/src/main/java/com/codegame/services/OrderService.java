package com.codegame.services;

import com.codegame.dto.CreateOrderRequest;
import com.codegame.dto.LineItemDto;
import com.codegame.exception.GlobalValidationException;
import com.codegame.model.GiftCard;
import com.codegame.model.Item;
import com.codegame.model.Order;
import com.codegame.repositories.GiftCodeRepository;
import com.codegame.repositories.ItemRepository;
import com.codegame.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    final ItemRepository itemRepo;

    final GiftCodeRepository giftCodeRepo;

    final OrderRepository orderRepo;

    public void createOrder(CreateOrderRequest request) {
        Order newOrder = new Order();
        newOrder.setOrderId(request.getOrderId());
        newOrder.setOrderDetail(request.toString());
        newOrder.setTransactionTotal(request.getTransactionTotal());

        for (LineItemDto lineItm : request.getLineItems()) {
            Item currItm = itemRepo.findById(lineItm.getItemId())
                                   .orElseThrow(() -> new GlobalValidationException(
                                           "Cannot find item with id " + lineItm.getItemId()));

            List<GiftCard> availGiftCard = giftCodeRepo.getCodeByItemId(currItm.getId(),
                                                                        GiftCard.GiftCardStatus.AVAILABLE);
            if (availGiftCard.size() < lineItm.getAmount()) {
                throw new GlobalValidationException("Not enough gift code for item with id " + lineItm.getItemId());
            }
            newOrder.addGiftCards(availGiftCard.subList(0, lineItm.getAmount()));
        }

        orderRepo.save(newOrder);
    }
}
