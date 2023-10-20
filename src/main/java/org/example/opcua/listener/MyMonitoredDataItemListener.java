package org.example.opcua.listener;

import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.MonitoredDataItem;
import com.prosysopc.ua.client.MonitoredDataItemListener;
import com.prosysopc.ua.stack.builtintypes.*;
import com.prosysopc.ua.stack.core.Attributes;
import com.prosysopc.ua.stack.core.Identifiers;
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

public class MyMonitoredDataItemListener implements MonitoredDataItemListener {
    private final SimpleClient client;
    private final KafkaProducer<String, GenericRecord> producer = KafkaProducerFactory.getInstance();

    public MyMonitoredDataItemListener(SimpleClient client) {
        this.client = client;
    }

    @Override
    public void onDataChange(MonitoredDataItem monitoredItem, DataValue prevValue, DataValue currentValue) {
        // 1. Avro Schema 객체 생성
        Schema avroSchema = AvroSchemaFactory.generateSchema();

        // 2. Schema 에 맞게 값 세팅
        GenericRecord avroRecord = new GenericData.Record(avroSchema);

        NodeId nodeId = monitoredItem.getNodeId();
        avroRecord.put("node_id", nodeId.toString());

        Object displayName = nodeId.getValue();

        avroRecord.put("display_name", displayName != null ? displayName.toString() : null);

        Variant value = currentValue.getValue();
        avroRecord.put("value", value != null ? value.toString() : null);

//        DataValue dataType;
//        try {
//            dataType = client.getUaClient().readAttribute(nodeId, UnsignedInteger.valueOf(Attributes.DataType.getValue()));
//        } catch (ServiceException e) {
//            throw new RuntimeException(e);
//        } catch (StatusException e) {
//            throw new RuntimeException(e);
//        }
//        avroRecord.put("data_type", dataType != null ? dataType.toString() : ""); //TODO - 여기에는 serverTimeStamp가 있고 sourceTimeStamp가 없음

        try {
            DataValue data = client.getUaClient().readAttribute(nodeId, UnsignedInteger.valueOf(Attributes.DataType.getValue()));
            NodeId identifier = (NodeId) data.getValue().getValue();
            avroRecord.put("data_type", identifier.toString()); //TODO
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } catch (StatusException e) {
            throw new RuntimeException(e);
        }

        StatusCode statusCodeObj = currentValue.getStatusCode();
        avroRecord.put("quality", statusCodeObj != null ? setStatus(statusCodeObj) : 9);

        DateTime dateTIme = currentValue.getSourceTimestamp();
        avroRecord.put("source_timestamp", dateTIme != null ? setTimeStamp(dateTIme).toString() : null);

        DateTime serverTimeStamp = currentValue.getServerTimestamp();
        avroRecord.put("server_timestamp", serverTimeStamp != null ? serverTimeStamp.toString() : null); //TODO - 왜 null로 들어오나?


        System.out.println("##Avro Record## " + avroRecord);


        // 3. 지정 된 Topic 으로 데이터 전달
        if(producer != null){
            ProducerRecord<String, GenericRecord> record = new ProducerRecord<>("tswind", avroRecord);
            producer.send(record);
            producer.flush();
//            producer.close();
        }else{
            System.out.println("There's no producer ready..");
        }
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
//        return LocalDateTime.ofInstant(
//                Instant.ofEpochMilli(sourceTimeStamp.getTimeInMillis())
//                , TimeZone.getDefault().toZoneId());
        return Instant.ofEpochMilli(sourceTimeStamp.getTimeInMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    //TODO - DataType 컬럼 값 (1. DataType 명, 2. OpcUa Identifier) 결정 하기
//    public String setDataType(NodeId dataTypeId){
//        if(dataTypeId.equals(Identifiers.Boolean)){
//            return "Boolean";
//        } else if(dataTypeId.equals(Identifiers.Decimal)){
//            return "Decimal";
//        } else if(dataTypeId.equals(Identifiers.Double)){
//            return "Double";
//        } else if(dataTypeId.equals(Identifiers.Integer)){
//            return "Integer";
//        } else if(dataTypeId.equals(Identifiers.Int32)){
//            return "Int32";
//        } else if(dataTypeId.equals(Identifiers.Int16)){
//            return "Int16";
//        } else if(dataTypeId.equals(Identifiers.Int64)){
//            return "Int64";
//        } else if(dataTypeId.equals(Identifiers.SByte)){
//            return "SByte";
//        }
//        return "";
//    }

}

