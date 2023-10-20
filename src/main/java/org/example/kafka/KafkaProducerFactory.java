package org.example.kafka;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaProducerFactory {
    public static final String TOPIC = "tswind";
    private final static String BOOTSTRAP_SERVERS = "10.16.2.198:9092,10.16.2.199:9092,10.16.2.200:9092";
    private final static String REGISTRY_SERVERS = "http://10.16.2.202:8081";


    private static final KafkaProducer<String, GenericRecord> instance = createInstance();


    public static KafkaProducer<String, GenericRecord> getInstance(){
        return instance;
    }

    private static Properties createProperties(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", REGISTRY_SERVERS);
        return properties;
    }

    private static KafkaProducer<String, GenericRecord> createInstance(){
        Properties properties = createProperties();
        return new KafkaProducer<>(properties);
    }
}
