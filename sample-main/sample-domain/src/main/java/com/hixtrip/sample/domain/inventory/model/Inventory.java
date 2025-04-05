package com.hixtrip.sample.domain.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 库存
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder(toBuilder = true)
public class Inventory {
    /**
     * SkuId
     */
    private String skuId;

    /**
     * 可售库存
     */
    private Long sellableQuantity;

    /**
     * 预占库存
     */
    private Long withholdingQuantity;

    /**
     * 占用库存
     */
    private Long occupiedQuantity;

    /**
     * 充血模型
     * 预占库存
     * 无高并发时调用
     *
     * @param quantity
     */
    /*public void withhold(Integer quantity) {
        if (this.sellableQuantity < quantity) {
            throw new RuntimeException("库存不足");
        }

        this.sellableQuantity -= quantity;
        this.withholdingQuantity += quantity;
    }*/

    /**
     * 充血模型
     * 修改库存
     *
     * @param skuId
     * @param sellableQuantity
     * @param withholdingQuantity
     * @param occupiedQuantity
     */
    public void change(String skuId, Long sellableQuantity, Long withholdingQuantity, Long occupiedQuantity) {
        this.skuId = skuId;
        this.sellableQuantity = sellableQuantity;
        this.withholdingQuantity = withholdingQuantity;
        this.occupiedQuantity = occupiedQuantity;
    }

    /**
     * 商品数量从预占库存移到占用库存
     * 无高并发时调用
     * @param amount
     */
    /*public void occupyFromWithholding(Integer amount) {
        if (this.withholdingQuantity < amount) {
            throw new RuntimeException("预占库存不足");
        }
        this.withholdingQuantity -= amount;
        this.occupiedQuantity += amount;
    }*/

    /**
     * 商品数量从预占库存移到可售库存
     * 无高并发时调用
     * @param amount
     */
    /*public void sellFromWithholding(Integer amount) {
        if (this.withholdingQuantity < amount) {
            throw new RuntimeException("预占库存不足");
        }

        this.withholdingQuantity -= amount;
        this.sellableQuantity += amount;
    }*/
}
