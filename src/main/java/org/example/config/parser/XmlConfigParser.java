package org.example.config.parser;

import com.prosysopc.ua.stack.builtintypes.NodeId;
import org.example.config.parser.dto.OpcChannelDetail;
import org.example.config.parser.dto.Subscription;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlConfigParser implements ConfigParser {
    private final String CONFIG_PATH = "C:\\workspace\\ems-core\\maven-test\\src\\main\\java\\org\\example\\config\\config.xml";
    public static Document configFile = null;

    public XmlConfigParser(){
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;

        try {
            documentBuilder = documentFactory.newDocumentBuilder();

            configFile = documentBuilder.parse(CONFIG_PATH);
            configFile.getDocumentElement().normalize();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Subscription> getSubscription(Element element){
        List<Subscription> subscription = new ArrayList<>();
        String[] type = {"DO","DI","AO","AI"};

        // 각 Type(DO,DI,AO,AI)의 Data Check.
        for(int i=0; i< type.length; i++){
            List<Subscription.Value> values = new ArrayList<>();
            Element dataList = (Element) element.getElementsByTagName(type[i]).item(0);

            if(dataList != null){
                NodeList data = dataList.getElementsByTagName("data");
                for(int j=0; j<data.getLength(); j++){
                    Element value = (Element) data.item(j);
                    values.add(Subscription.Value.builder()
                            .id(value.getElementsByTagName("id").item(0).getTextContent())
                            .nodeId(value.getElementsByTagName("nodeId").item(0).getTextContent())
                            .description(value.getElementsByTagName("description").item(0).getTextContent())
                            .build());
                }
            }
            subscription.add(Subscription.builder()
                    .type(type[i])
                    .value(values)
                    .build());
        }
        return subscription;
    }

    public List<OpcChannelDetail> getChannels(String type){ //type = "gather"
        List<OpcChannelDetail> channelDTOs = new ArrayList<>();

        Element element = (Element) configFile.getElementsByTagName(type).item(0);
        if(element != null){
            NodeList channels = element.getElementsByTagName("channel");

            for(int i=0; i<channels.getLength(); i++){
                Element channelDetail = (Element) channels.item(i);

                //TODO - 임시로 OUC Channel만 가져오는 중
                String protocol = channelDetail.getElementsByTagName("protocol").item(0).getTextContent();
                if(!protocol.equals("OUC")){
                    continue;
                }

//                ChannelTCPDetailResDto.Connection connection = ChannelTCPDetailResDto.Connection.builder()
//                        .ip(channelDetail.getElementsByTagName("ip").item(0).getTextContent())
//                        .port(Integer.parseInt(channelDetail.getElementsByTagName("port").item(0).getTextContent()))
//                        .bindAddress(null)
//                        .slaveAddress(channelDetail.getElementsByTagName("slaveAddress").item(0).getTextContent())
//                        .period(Integer.parseInt(channelDetail.getElementsByTagName("period").item(0).getTextContent()))
//                        .build();

                channelDTOs.add(OpcChannelDetail.builder()
                        .id(null) //TODO
                        .name(channelDetail.getElementsByTagName("name").item(0).getTextContent())
                        .protocol(protocol)
//                        .rtuConnection(connection)
                        .subscription(getSubscription(channelDetail))
                        .build());
            }
        }
        return channelDTOs;
    }

    @Override
    public List<Object> readSubscriptionNodes() {
        List<Object> nodeIds = new ArrayList<>();

        List<OpcChannelDetail> channels = getChannels("gather");

        for(OpcChannelDetail channel : channels){
            List<Subscription> subscriptions = channel.getSubscription();

            for(Subscription subscription : subscriptions){
                List<Subscription.Value> subscriptionDetails = subscription.getValue();

                for(Subscription.Value detail : subscriptionDetails){
                    nodeIds.add(NodeId.parseNodeId(detail.getNodeId()));
                }
            }
        }
        return nodeIds;
    }

//    @Override
//    public List<Object> readSubscriptionNodes() {
//        NodeList nodes = configFile.getElementsByTagName("data");
//        System.out.println("nodes length: " + nodes.getLength());
//
//        List<Object> nodeIds = new ArrayList<>();
//
//        for(int i=0; i< nodes.getLength(); i++){
//            Node node = nodes.item(i);
//
//            if(node.getNodeType() == Node.ELEMENT_NODE){
//                Element element = (Element) node;
//                System.out.println("[id] " + getValue("id", element));
//                System.out.println("[nodeId] " + getValue("nodeId", element));
//                nodeIds.add(NodeId.parseNodeId(getValue("nodeId", element)));
//            }
//        }
//        return nodeIds;
//    }

    public String getValue(String tag, Element element){
        NodeList nodes = element.getElementsByTagName(tag)
                .item(0)
                .getChildNodes();
        Node node = nodes.item(0);
        return node.getNodeValue();
    }
}
