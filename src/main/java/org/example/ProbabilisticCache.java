package org.example;

import java.util.Random;
import redis.clients.jedis.Jedis;

public class ProbabilisticCache {

    private Jedis jedis;
    private Random random;

    public ProbabilisticCache(String redisHost, int redisPort) {
        this.jedis = new Jedis(redisHost, redisPort);
        this.random = new Random();
    }

    public String getWithStampedeProtection(String key, DataGenerator generator, int expireTime) {
        String cachedValue = jedis.get(key);
        if (cachedValue != null) {
            return cachedValue;
        }

        if (random.nextDouble() > 0.01) {
            try {
                Thread.sleep((long) (random.nextDouble() * 500));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return getWithStampedeProtection(key, generator, expireTime);
        }

        String newValue = generator.generate();
        jedis.setex(key, expireTime, newValue);
        return newValue;
    }

    public static void main(String[] args) {
        ProbabilisticCache cache = new ProbabilisticCache("localhost", 6379);

        DataGenerator generator = () -> "Expensive Data";

        String result = cache.getWithStampedeProtection("mykey", generator, 60);
        System.out.println(result);
    }
}

@FunctionalInterface
interface DataGenerator {

    String generate();
}