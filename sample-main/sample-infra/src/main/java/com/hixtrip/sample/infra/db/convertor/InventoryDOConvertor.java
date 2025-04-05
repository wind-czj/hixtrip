package com.hixtrip.sample.infra.db.convertor;

import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.infra.db.dataobject.InventoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface InventoryDOConvertor {
    InventoryDOConvertor INSTANCE = Mappers.getMapper(InventoryDOConvertor.class);

    Inventory inventoryDO2Inventory(InventoryDO inventoryDO);

    InventoryDO inventory2InventoryDO(Inventory inventory);
}
