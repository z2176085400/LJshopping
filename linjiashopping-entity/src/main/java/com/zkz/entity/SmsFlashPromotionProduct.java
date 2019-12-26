package com.zkz.entity;


import com.zkz.model.PmsProduct;
import com.zkz.model.SmsFlashPromotionProductRelation;
import lombok.Getter;
import lombok.Setter;

/**
 * 限时购及商品信息封装
 */
public class SmsFlashPromotionProduct extends SmsFlashPromotionProductRelation {
    @Getter
    @Setter
    private PmsProduct product;
}
