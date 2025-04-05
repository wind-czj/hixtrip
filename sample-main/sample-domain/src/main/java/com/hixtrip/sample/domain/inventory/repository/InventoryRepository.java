package com.hixtrip.sample.domain.inventory.repository;

import com.hixtrip.sample.domain.inventory.model.Inventory;

/**
 *
 */
public interface InventoryRepository {


    Inventory getInventory(String skuId);

    void save(Inventory inventory);

    void withholdInventory(String skuId, Integer quantity);

    void occupyFromWithholding(String skuId, Integer amount);

    void sellFromWithholding(String skuId, Integer amount);
}
