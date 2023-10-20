package org.example.opcua;

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.ServiceException;
import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.client.*;
import com.prosysopc.ua.stack.builtintypes.LocalizedText;
import com.prosysopc.ua.stack.builtintypes.NodeId;
import com.prosysopc.ua.stack.core.ApplicationDescription;
import com.prosysopc.ua.stack.core.ApplicationType;
import com.prosysopc.ua.stack.transport.security.SecurityMode;
import org.example.config.parser.ConfigParser;
import org.example.config.parser.ParserFactory;
import org.example.config.parser.ConfigType;
import org.example.opcua.listener.MySubscriptionNotificationListener;

import java.util.List;

public class SimpleClient {
    private final ConfigParser parser = ParserFactory.createDataSource(ConfigType.XML);
    protected String APP_NAME = "scada-opc-client";
//    protected String serverAddress = "opc.tcp://LAPTOP-R0CQ7DUO:53530/OPCUA/SimulationServer";
    protected String serverAddress = "opc.tcp://vgen-HP-Z8-G4-Workstation:53530/OPCUA/SimulationServer";
    protected int sessionCount = 0;
    protected UaClient client = new UaClient(serverAddress);
//    public ConfigParser configParser = new ConfigParser();
    public UaClient getUaClient(){
        return client;
    }

    protected SecurityMode securityMode = SecurityMode.NONE;
//    protected MonitoredDataItemListener itemListener = new MyMonitoredDataItemListener(this);
    protected SubscriptionNotificationListener subscriptionNotificationListener = new MySubscriptionNotificationListener(this);


    public SimpleClient() {
        try {
            connect();
            subscribe();
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } catch (StatusException e) {
            throw new RuntimeException(e);
        }
    }

    public void subscribe() throws StatusException, ServiceException {
        Subscription subscription = new Subscription();
        List<Object> nodes = parser.readSubscriptionNodes();

        for(int i=0; i<nodes.size(); i++){
            MonitoredDataItem item = new MonitoredDataItem((NodeId) nodes.get(i));
            subscription.addItem(item);
            System.out.println("added to subscription - " + i);
        }

        subscription.addNotificationListener(subscriptionNotificationListener);
        client.addSubscription(subscription);
    }

//    public void subscribe() throws StatusException, ServiceException {
//        Subscription subscription = new Subscription();
//
////        String nameSpace = "http://www.prosysopc.com/OPCUA/SimulationNodes/";
//        int ns = 3;
//
////        NodeId[] nodes = {
////                new NodeId(ns, 1001),
////                new NodeId(ns, 1002),
////                new NodeId(ns, 1003),
////                new NodeId(ns, 1004),
////                new NodeId(ns, 1005),
////                new NodeId(ns, 1006)
////        };
//        NodeId[] nodes = configParser.readSubscriptionList();
//
//        for(int i=0; i<nodes.length; i++){
//            MonitoredDataItem item = new MonitoredDataItem(nodes[i]);
////            item.setDataChangeListener(itemListener);
//            subscription.addItem(item);
//            System.out.println("added to subscription - " + i);
//        }
//
////        subscription.addAliveListener(subscriptionAliveListener);
//        subscription.addNotificationListener(subscriptionNotificationListener);
//
//        client.addSubscription(subscription);
//    }

    //TODO - tsWind용
//    public void subscribe() throws StatusException, ServiceException {
//        Subscription subscription = new Subscription();
//
//        String nameSpace = "http://www.amsc.com/OPCUA";
//
//        Object[] nodes = {
//                new NodeId(2, "WTG2/WCNV.CabiIntlHum.actSt.stVal"),
//                new NodeId(2, "WTG2/WCNV.CmCnvClFan.stVal"),
//                new NodeId(2, "WTG2/WCNV.CmCnvClPmp.stVal"),
//                new NodeId(2, "WTG2/WCNV.CmCnvFanSpd.stVal"),
//                new NodeId(2, "WTG2/WCNV.CmCnvStDec.stVal"),
//                new NodeId(2, "WTG2/WCNV.CmCnvStInc.stVal"),
//                new NodeId(2, "WTG2/WCNV.CmCnvStop.stVal"),
//
//                new NodeId(2, "WTG2/WCNV.CnvErrHis1.actSt.stVal"),
//                new NodeId(2, "WTG2/WCNV.CnvErrHis2.actSt.stVal"),
//                new NodeId(2, "WTG2/WCNV.CnvErrHis3.actSt.stVal"),
//                new NodeId(2, "WTG2/WCNV.CnvErrHis4.actSt.stVal"),
//                new NodeId(2, "WTG2/WCNV.CnvErrHis5.actSt.stVal"),
//                new NodeId(2, "WTG2/WCNV.CnvErrHis6.actSt.stVal"),
//                new NodeId(2, "WTG2/WCNV.CnvErrHis7.actSt.stVal"),
//                new NodeId(2, "WTG2/WCNV.CnvErrHis8.actSt.stVal")
//        };
////        NodeId nodeId = new NodeId(2, "WTG2/WCNV.CabiIntlHum.actSt.stVal"); //TODO 채널명으로 인덱스 가져오기
//
//        for(int i=0; i<nodes.length; i++){
//            MonitoredDataItem item = new MonitoredDataItem((NodeId) nodes[i]);
//
//            //TODO - item에 Listener 달기
//            subscription.addItem(item);
//            System.out.println("added to subscription - " + i);
//        }
//
//        client.addSubscription(subscription);
//    }

    private void connect() throws ServiceException {
        System.out.println("===== trying to connect =====");
        client.setSessionName(String.format("%s@%s Session%d", APP_NAME,
                ApplicationIdentity.getActualHostNameWithoutDomain(), ++sessionCount));

        ApplicationDescription appDescription = new ApplicationDescription();
        appDescription.setApplicationName(new LocalizedText(APP_NAME + "@localhost"));
        appDescription.setApplicationUri("urn:localhost:OPCUA:" + APP_NAME);
        appDescription.setProductUri("urn:prosysopc.com:OPCUA:" + APP_NAME);
        appDescription.setApplicationType(ApplicationType.Client);

        ApplicationIdentity appIdentity = new ApplicationIdentity();
        appIdentity.setApplicationDescription(appDescription);
        client.setApplicationIdentity(appIdentity);

        client.setSecurityMode(SecurityMode.NONE);
        System.out.println("===== client setting done =====");

        client.connect();
        System.out.println("===== connected =====");
    }
}
