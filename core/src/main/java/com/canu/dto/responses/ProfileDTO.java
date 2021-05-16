package com.canu.dto.responses;

import com.canu.model.CanIModel;
import com.canu.model.CanUModel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ProfileDTO {

    public ProfileDTO(CanUModel canu) {
        id = canu.getId();
        firstName = canu.getFirstName();
        lastName = canu.getLastName();
        email = canu.getEmail();
        avatar = canu.getAvatar();
        phone = canu.getPhone();
        nation = canu.getNation();
        city = canu.getCity();
        address = canu.getAddress();
        createdAt = canu.getCreatedAt();
        if (canu.isRegisterCanI()) {
            CanIModel caniData = canu.getCanIModel();
            phone = caniData.getPhone();
            cani = CanIDto.builder()
                          .id(caniData.getId())
                          .name(caniData.getName())
                          .avatar(caniData.getAvatar())
                          .price(caniData.getPrice())
                          .address(caniData.getAddress())
                          .nation(caniData.getNational())
                          .areaService(caniData.getAreaService())
                          .currency(caniData.getCurrency())
                          .jobType(caniData.getServiceType())
                          .rating(caniData.getRating())
                          .build();
        }
    }

    Long id;

    String firstName;

    String lastName;

    String email;

    String avatar;

    String phone;

    String nation;

    String city;

    String address;

    LocalDateTime createdAt;

    CanIDto cani;
}
