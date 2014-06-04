package net.floodlightcontroller.test;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.util.HexString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.devicemanager.SwitchPort;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;
import net.floodlightcontroller.packet.Ethernet;



public class portmodifytesting implements IOFMessageListener, IFloodlightModule {
	
	protected IFloodlightProviderService floodlightProvider;
	protected Set macAddresses;
	protected static Logger logger;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return portmodifytesting.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
	    Collection<Class<? extends IFloodlightService>> l =
	        new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IFloodlightProviderService.class);
	    return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
	    floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
	    macAddresses = new ConcurrentSkipListSet<Long>();
	    logger = LoggerFactory.getLogger(portmodifytesting.class);
	}
	
	@Override
	public void startUp(FloodlightModuleContext context) {
	    floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		// TODO Auto-generated method stub
		
		
        switch (msg.getType()) {
        case PACKET_IN:
            return this.processPacketInMessage(sw,
                    (OFPacketIn) msg, cntx);
        	default:
        		break;
        }
        return Command.CONTINUE;
	

	}
    public Command processPacketInMessage(IOFSwitch sw, OFPacketIn pi,FloodlightContext cntx) {
    	
    		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
    				IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
    		
    		logger.info("haha module start!!");
    		
            SwitchPort swPort = new SwitchPort(sw.getId(), pi.getInPort());
            /*
            blockHost(floodlightProvider,
                    swPort, eth.getDestinationMAC().toLong(), (short) 5,
                    AppCookie.makeCookie(5, 0));
    		*/
			return Command.CONTINUE;
    		
    		
    }
    public static boolean
    blockHost(IFloodlightProviderService floodlightProvider,
              SwitchPort sw_tup, long host_mac,
              short hardTimeout, long cookie) {
    	
    	 OFFlowMod fm =
                 (OFFlowMod) floodlightProvider.getOFMessageFactory()
                                               .getMessage(OFType.FLOW_MOD);
         List<OFAction> actions = new ArrayList<OFAction>(); // Set no action to
         													// drop
         int inputPort = sw_tup.getPort();
         OFMatch match = new OFMatch();
         match.setInputPort((short)inputPort);
         
         IOFSwitch sw =
                 floodlightProvider.getSwitch(sw_tup.getSwitchDPID());
         
         fm.setCookie(cookie)
           .setHardTimeout((short) 0)
           .setIdleTimeout((short) 0)
           .setBufferId(OFPacketOut.BUFFER_ID_NONE)
           .setMatch(match)
           .setActions(actions)
           .setLengthU(OFFlowMod.MINIMUM_LENGTH); // +OFActionOutput.MINIMUM_LENGTH);

         try {
             if (logger.isDebugEnabled()) {
                 logger.debug("write drop flow-mod sw={} match={} flow-mod={}",
                           new Object[] { sw, match, fm });
             }
             sw.write(fm, null);
         } catch (IOException e) {
             logger.error("Failure writing drop flow mod", e);
         }
         
         return true;
    		
    }
    
}
