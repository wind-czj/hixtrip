package com.hixtrip.sample.domain.inventory;

import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 库存领域服务
 * 库存设计，忽略仓库、库存品、计量单位等业务
 */
@Component
public class InventoryDomainService {
    @Autowired
    private InventoryRepository inventoryRepository;


    /**
     * 获取sku当前库存
     *
     * @param skuId
     */
    public Long getInventory(String skuId) {
        //todo 需要你在infra实现，只需要实现缓存操作, 返回的领域对象自行定义
        Inventory inventory = inventoryRepository.getInventory(skuId);
        return inventory == null ? 0 : inventory.getSellableQuantity();
    }

    /**
     * 预占库存
     * @param skuId
     * @param quantity
     * @return
     */
    public void withholdInventory(String skuId, Integer quantity) {
        // 高并发无锁
        inventoryRepository.withholdInventory(skuId, quantity);
        // 未考虑高并发
//        Inventory inventory = inventoryRepository.getInventory(skuId);
//        inventory.withhold(quantity);
//        inventoryRepository.save(inventory);
    }

    /**
     * 修改库存
     *
     * @param skuId
     * @param sellableQuantity    可售库存
     * @param withholdingQuantity 预占库存
     * @param occupiedQuantity    占用库存
     * @return
     */
    public Boolean changeInventory(String skuId, Long sellableQuantity, Long withholdingQuantity, Long occupiedQuantity) {
        //todo 需要你在infra实现，只需要实现缓存操作。
        Inventory inventory = inventoryRepository.getInventory(skuId);
        inventory.change(skuId, sellableQuantity, withholdingQuantity, occupiedQuantity);
        inventoryRepository.save(inventory);
        return true;
    }

    public void occupyFromWithholding(String skuId, Integer amount) {
        inventoryRepository.occupyFromWithholding(skuId, amount);
        // 未考虑高并发
//        Inventory inventory = inventoryRepository.getInventory(skuId);
//        inventory.occupyFromWithholding(amount);
//        inventoryRepository.save(inventory);
    }

    public void sellFromWithholding(String skuId, Integer amount) {
        inventoryRepository.sellFromWithholding(skuId, amount);
        // 未考虑高并发
//        Inventory inventory = inventoryRepository.getInventory(skuId);
//        inventory.sellFromWithholding(amount);
//        inventoryRepository.save(inventory);
    }
}
