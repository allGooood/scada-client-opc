package org.example.opcua.manager;

import com.prosysopc.ua.StatusException;
import com.prosysopc.ua.server.NodeManagerUaNode;
import com.prosysopc.ua.server.UaInstantiationException;
import com.prosysopc.ua.server.UaServer;
import com.prosysopc.ua.stack.builtintypes.DataValue;
import com.prosysopc.ua.types.opcua.server.ExclusiveLimitState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyNodeManager extends NodeManagerUaNode {
    public static final String NAMESPACE = "http://www.amsc.com/OPCUA";
    private static final Logger logger = LoggerFactory.getLogger(MyNodeManager.class);

    public MyNodeManager(UaServer server, String namespaceUri) throws StatusException, UaInstantiationException {
        super(server, namespaceUri);
    }

//    public void simulate() {
//        final DataValue v = myLevel.getValue();
//        Double nextValue = v.isNull() ? 0 : v.getValue().doubleValue() + dx;
//        if (nextValue <= 0) {
//            dx = 1;
//        } else if (nextValue >= 100) {
//            dx = -1;
//        }
//        try {
//            myLevel.updateValue(nextValue);
//            if (nextValue > myAlarm.getHighHighLimit()) {
////                activateAlarm(700, ExclusiveLimitState.HighHigh);
//            } else if (nextValue > myAlarm.getHighLimit()) {
////                activateAlarm(500, ExclusiveLimitState.High);
//            } else if (nextValue < myAlarm.getLowLowLimit()) {
////                activateAlarm(700, ExclusiveLimitState.Low);
//            } else if (nextValue < myAlarm.getLowLimit()) {
////                activateAlarm(500, ExclusiveLimitState.LowLow);
//            } else {
////                inactivateAlarm();
//            }
//        } catch (Exception e) {
//            logger.error("Error while simulating", e);
//            throw new RuntimeException(e); // End the task
//        }
//
//    }
}
