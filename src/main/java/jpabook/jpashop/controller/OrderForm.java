package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class OrderForm {

    @NotEmpty
    private String memberId;

    @NotEmpty
    private String itemId;

    private int quantity;
}
