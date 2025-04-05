package com.hixtrip.sample.infra;

import com.hixtrip.sample.domain.inventory.model.Inventory;
import com.hixtrip.sample.domain.inventory.repository.InventoryRepository;
import com.hixtrip.sample.infra.db.convertor.InventoryDOConvertor;
import com.hixtrip.sample.infra.db.dataobject.InventoryDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * infra层是domain定义的接口具体的实现
 */
@Component
public class InventoryRepositoryImpl implements InventoryRepository {
    public static final String INVENTORY_PREFIX = "inventory:";

//    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 首次启动准备一些库存用作测试
     * @param redisTemplate
     */
    @Autowired
    public InventoryRepositoryImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        if (redisTemplate.hasKey(INVENTORY_PREFIX + "1")) {
            Map<String, Object> inventoryForTest = new HashMap<>();
            inventoryForTest.put("skuId", "1");
            inventoryForTest.put("sellableQuantity", 100L);
            inventoryForTest.put("withholdingQuantity", 0L);
            inventoryForTest.put("occupiedQuantity", 0L);
            redisTemplate.opsForHash().putAll(INVENTORY_PREFIX + "1", inventoryForTest);
        }
    }

    @Override
    public Inventory getInventory(String skuId) {
        if (redisTemplate.hasKey(INVENTORY_PREFIX + skuId)) {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(INVENTORY_PREFIX + skuId);
            if (!entries.isEmpty()) {
                if (entries.containsKey("sellableQuantity") && entries.containsKey("withholdingQuantity") && entries.containsKey("occupiedQuantity")) {
                    InventoryDO inventoryDO = new InventoryDO();
                    inventoryDO.setSkuId((String) entries.get("skuId"));
                    inventoryDO.setSellableQuantity((Long) entries.get("sellableQuantity"));
                    inventoryDO.setWithholdingQuantity((Long) entries.get("withholdingQuantity"));
                    inventoryDO.setOccupiedQuantity((Long) entries.get("occupiedQuantity"));
                    return InventoryDOConvertor.INSTANCE.inventoryDO2Inventory(inventoryDO);
                }
            }
        }

        throw new RuntimeException("库存不存在");
    }

    @Override
    public void save(Inventory inventory) {
        InventoryDO inventoryDO = InventoryDOConvertor.INSTANCE.inventory2InventoryDO(inventory);

        Map<String, Object> map = new HashMap<>();
        map.put("skuId", inventoryDO.getSkuId());
        map.put("sellableQuantity", inventoryDO.getSellableQuantity());
        map.put("withholdingQuantity", inventoryDO.getWithholdingQuantity());
        map.put("occupiedQuantity", inventoryDO.getOccupiedQuantity());

        redisTemplate.opsForHash().putAll(INVENTORY_PREFIX + inventory.getSkuId(), map);
    }

    /**
     * 高并发无锁设计，使用lua脚本保证操作的原子性
     * @param skuId
     * @param quantity
     */
    @Override
    public void withholdInventory(String skuId, Integer quantity) {
        String script = "local key = KEYS[1] \n" +
                "local quantity = tonumber(ARGV[1]) \n" +
                "local sellableQuantity = tonumber(redis.call('hget', key, 'sellableQuantity')) \n" +
                "if (sellableQuantity >= quantity) then \n" +
                "  redis.call('HINCRBY', key, 'sellableQuantity', -quantity) \n" +
                "  redis.call('HINCRBY', key, 'withholdingQuantity', quantity) \n" +
                "  return true \n" +
                "else \n" +
                "  return false\n" +
                "end";

        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
        Boolean result = redisTemplate.execute(redisScript, Arrays.asList(INVENTORY_PREFIX + skuId), quantity);
        if (!result) {
            throw new RuntimeException("可售库存不足");
        }
    }

    @Override
    public void occupyFromWithholding(String skuId, Integer amount) {
        String script = "local key = KEYS[1] \n" +
                "local quantity = tonumber(ARGV[1]) \n" +
                "local withholdingQuantity = tonumber(redis.call('hget', key, 'withholdingQuantity')) \n" +
                "if (withholdingQuantity >= quantity) then \n" +
                "  redis.call('HINCRBY', key, 'withholdingQuantity', -quantity) \n" +
                "  redis.call('HINCRBY', key, 'occupiedQuantity', quantity) \n" +
                "  return true \n" +
                "else \n" +
                "  return false\n" +
                "end";

        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
        Boolean result = redisTemplate.execute(redisScript, Arrays.asList(INVENTORY_PREFIX + skuId), amount);
        if (!result) {
            throw new RuntimeException("预占库存不足");
        }
    }

    @Override
    public void sellFromWithholding(String skuId, Integer amount) {
        String script = "local key = KEYS[1] \n" +
                "local quantity = tonumber(ARGV[1]) \n" +
                "local withholdingQuantity = tonumber(redis.call('hget', key, 'withholdingQuantity')) \n" +
                "if (withholdingQuantity >= quantity) then \n" +
                "  redis.call('HINCRBY', key, 'withholdingQuantity', -quantity) \n" +
                "  redis.call('HINCRBY', key, 'sellableQuantity', quantity) \n" +
                "  return true \n" +
                "else \n" +
                "  return false\n" +
                "end";

        RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
        Boolean result = redisTemplate.execute(redisScript, Arrays.asList(INVENTORY_PREFIX + skuId), amount);
        if (!result) {
            throw new RuntimeException("预占库存不足");
        }
    }
}
