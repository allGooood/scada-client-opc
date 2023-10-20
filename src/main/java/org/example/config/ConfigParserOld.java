package org.example.config;

import com.prosysopc.ua.stack.builtintypes.NodeId;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ConfigParserOld {
    private final String PATH = "C:\\workspace\\ems-core\\maven-test\\src\\main\\java\\org\\example\\config\\config.xml";
    public static Document configFile = null;

    public ConfigParserOld(){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        try {
            configFile = builder.parse(PATH);
            configFile.getDocumentElement().normalize();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("configFileIsNull: " + (configFile == null));
    }

    public String getValue(String tag, Element element){
        NodeList nodes = element.getElementsByTagName(tag)
                .item(0)
                .getChildNodes();
        Node node = nodes.item(0);
        return node.getNodeValue();
    }

    public NodeId[] readSubscriptionList(){
        NodeList nodes = configFile.getElementsByTagName("data");
        System.out.println("nodes length: " + nodes.getLength());

        NodeId[] nodeIDs = new NodeId[nodes.getLength()];

        for(int i=0; i< nodes.getLength(); i++){
            Node node = nodes.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element) node;
                System.out.println("[id] " + getValue("id", element));
                System.out.println("[nodeId] " + getValue("nodeId", element));
                nodeIDs[i] = NodeId.parseNodeId(getValue("nodeId", element));
            }
        }
        return nodeIDs;
    }

    private NodeId generateNodeId(String id){
        String[] elements = id.split(";");
        return new NodeId(Integer.parseInt(elements[0]), elements[1]);
    }
}
