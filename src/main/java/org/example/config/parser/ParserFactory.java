package org.example.config.parser;

public class ParserFactory {

    public static ConfigParser createDataSource(String type){
        if(type.equals(ConfigType.XML)){
            return new XmlConfigParser();
        }else if(type.equals(ConfigType.RDB)){
            return new RdbConfigParser();
        }else{
            throw new IllegalArgumentException("Invalid type of DataSource");
        }
    }
}
