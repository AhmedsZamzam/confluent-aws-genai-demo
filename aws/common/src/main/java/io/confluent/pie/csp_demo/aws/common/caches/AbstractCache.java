package io.confluent.pie.csp_demo.aws.common.caches;

import io.kcache.Cache;
import io.kcache.KafkaCache;
import io.kcache.KafkaCacheConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.kcache.KafkaCacheConfig.*;

/**
 * Abstract cache
 *
 * @param <T> Type
 */
public abstract class AbstractCache<T> {

    public interface CacheUpdateHandler<T> {
        void handleUpdate(String key, T value, T oldValue);
    }

    @Getter(AccessLevel.PROTECTED)
    private final KafkaCache<String, T> cache;

    @Setter
    private CacheUpdateHandler<T> cacheUpdateHandler;

    protected AbstractCache(@Autowired KafkaCacheConfig cfg,
                            Serializer<T> serializer,
                            Deserializer<T> deserializer,
                            String topic) {
        final Map<String, Object> properties = cfg.originals();
        properties.put(KAFKACACHE_TOPIC_CONFIG, topic);
        properties.put(KAFKACACHE_GROUP_ID_CONFIG, cfg.originals().get(KAFKACACHE_GROUP_ID_CONFIG) + "-" + topic);
        properties.put(KAFKACACHE_CLIENT_ID_CONFIG, cfg.originals().get(KAFKACACHE_CLIENT_ID_CONFIG) + "-" + topic);

        this.cache = new KafkaCache<>(
                new KafkaCacheConfig(properties),
                Serdes.String(),
                Serdes.serdeFrom(
                        serializer,
                        deserializer
                ),
                this::handleUpdate,
                null);

        this.cache.init();
    }

    /**
     * Handle update
     *
     * @param key            Key
     * @param value          Value
     * @param oldValue       Old value
     * @param topicPartition Topic partition
     * @param offset         Offset
     * @param ts             Timestamp
     */
    public void handleUpdate(String key, T value, T oldValue, TopicPartition topicPartition, long offset, long ts) {
        if (this.cacheUpdateHandler != null) {
            this.cacheUpdateHandler.handleUpdate(key, value, oldValue);
        }
    }

    /**
     * Compute if absent
     *
     * @param key             The key
     * @param mappingFunction The mapping function
     * @return The value
     */
    public T computeIfAbsent(String key, Function<String, T> mappingFunction) {
        return this.cache.computeIfAbsent(key, mappingFunction);
    }

    public void update(String key, T value) {
        this.cache.put(key, value);
    }

    /**
     * Put value by key
     *
     * @param key   Key
     * @param value Value
     */
    public void put(String key, T value) {
        this.cache.put(key, value);
    }

    /**
     * Check if cache contains key
     *
     * @param key Key
     * @return True if cache contains key
     */
    public boolean containsKey(String key) {
        return this.cache.containsKey(key);
    }

    /**
     * Get value by key
     *
     * @param key Key
     * @return Value
     */
    public T get(String key) {
        return this.cache.get(key);
    }

    /**
     * Get all values
     *
     * @return List of values
     */
    public List<Map.Entry<String, T>> getAll() {
        return new ArrayList<>(this.cache.entrySet());
    }

}
