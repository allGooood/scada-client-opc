package org.example.config.parser.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.config.parser.dto.Subscription;

import java.util.List;

@Builder
@Getter
public class OpcChannelDetail {
    private String id;
    private String name;
    private String protocol;
    private List<Subscription> subscription;

//    @Builder
//    @Getter
//    public static class Connection{
//        private String ip;
//        private Integer port;
//        private Bind bindAddress;
//        private String slaveAddress;
//        private Integer period;
//    }
//    @Builder
//    @Getter
//    public static class Bind{
//        private String name;
//        private String value;
//    }
}
