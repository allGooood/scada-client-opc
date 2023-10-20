package org.example.config.parser.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
public class Subscription {
    private String type;
    private List<Value> value;

    @Getter
    @Setter
    @Builder
    public static class Value{
        private String id;
        private String nodeId;
        private String description;
        private String dataTyep;
    }
}
