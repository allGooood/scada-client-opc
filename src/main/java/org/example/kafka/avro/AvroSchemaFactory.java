package org.example.kafka.avro;

import org.apache.avro.Schema;

public class AvroSchemaFactory {

    //TODO - config 파일로 분리 -> 그냥 Specific Record + Avsc 파일 사용 하기
    public static String SCHEMA_ON_USE = "{"
            + " \"name\": \"tswind\","
            + " \"type\": \"record\","
            + " \"fields\": ["
            + "     {\"name\": \"node_id\", \"type\": \"string\"},"
//            + "     {\"name\": \"display_name\",  \"type\": [\"string\", \"null\"]},"
            + "     {\"name\": \"value\", \"type\": [\"string\", \"null\"]},"
//            + "     {\"name\": \"data_type\",  \"type\": [\"string\", \"null\"]},"
            + "     {\"name\": \"quality\",  \"type\": [\"int\", \"null\"]},"
            + "     {\"name\": \"source_timestamp\",  \"type\": [\"string\", \"null\"]}"
//            + "     {\"name\": \"server_timestamp\",  \"type\": [\"string\", \"null\"]}"
            + " ]"
            + "}";

    public static Schema generateSchema(){
        return new Schema.Parser()
                .parse(SCHEMA_ON_USE);
    }
}
