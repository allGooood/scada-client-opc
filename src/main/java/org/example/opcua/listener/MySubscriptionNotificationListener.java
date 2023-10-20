package org.example.opcua.listener;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredEventItem;
import com.prosysopc.ua.client.Subscription;
import com.prosysopc.ua.client.SubscriptionNotificationListener;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.Attributes;
import com.prosysopc.ua.stack.core.NotificationData;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.kafka.KafkaProducerFactory;
import org.example.kafka.avro.AvroSchemaFactory;
import org.example.opcua.SimpleClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class MySubscriptionNotificationListener implements SubscriptionNotificationListener {
    private final SimpleClient client;
    private final KafkaProducer<String, GenericRecord> producer = KafkaProducerFactory.getInstance();

    public MySubscriptionNotificationListener(SimpleClient client) {
        this.client = client;
    }

    @Override
    public void onBufferOverflow(Subscription subscription, UnsignedInteger unsignedInteger, ExtensionObject[] extensionObjects) {

    }

    public int setStatus(StatusCode statusCode){
        if(statusCode.equalsStatusCode(StatusCode.BAD)){
            return 3;
        }
        if(statusCode.isUncertain()){
            return 2;
        }
        return 1;
    }

    //TODO - Millisecond 이하 값 가져오기
    public LocalDateTime setTimeStamp(DateTime sourceTimeStamp){
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(sourceTimeStamp.getTimeInMillis())
                , TimeZone.getDefault().toZoneId());
    }

    @Override
    public void onDataChange(Subscription subscription, MonitoredDataItem monitoredDataItem, DataValue newValue) {
        // 1. Avro Schema 객체 생성
        Schema avroSchema = AvroSchemaFactory.generateSchema();

        // 2. Schema 에 맞게 값 세팅
        GenericRecord avroRecord = new GenericData.Record(avroSchema);

        NodeId nodeId = monitoredDataItem.getNodeId();
        avroRecord.put("node_id", nodeId.toString());

        Variant value = newValue.getValue();
        avroRecord.put("value", value != null ? value.toString() : null);

        StatusCode statusCodeObj = newValue.getStatusCode();
        avroRecord.put("quality", statusCodeObj != null ? setStatus(statusCodeObj) : 9);

        DateTime dateTIme = newValue.getSourceTimestamp();
        avroRecord.put("source_timestamp", dateTIme != null ? setTimeStamp(dateTIme).toString() : null);


        System.out.println("##Avro Record## " + avroRecord);


        // 3. 지정 된 Topic 으로 데이터 전달
        if(producer != null){
            ProducerRecord<String, GenericRecord> record = new ProducerRecord<>("tswind", avroRecord);
            producer.send(record);
            producer.flush();
        }else{
            System.out.println("There's no producer ready..");
        }
    }

    @Override
    public void onError(Subscription subscription, Object o, Exception e) {

    }

    @Override
    public void onEvent(Subscription subscription, MonitoredEventItem monitoredEventItem, Variant[] variants) {

    }

    @Override
    public long onMissingData(Subscription subscription, UnsignedInteger unsignedInteger, long l, long l1, StatusCode statusCode) {
        return 0;
    }

    @Override
    public void onNotificationData(Subscription subscription, NotificationData notificationData) {

    }

    @Override
    public void onStatusChange(Subscription subscription, StatusCode statusCode, StatusCode statusCode1, DiagnosticInfo diagnosticInfo) {

    }
}
