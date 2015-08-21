package net.floodlightcontroller.forwarding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.util.MatchUtils;

import org.projectfloodlight.openflow.protocol.OFFlowMod;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.protocol.action.OFActionOutput;
import org.projectfloodlight.openflow.protocol.match.Match;
import org.projectfloodlight.openflow.protocol.match.MatchField;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFBufferId;
import org.projectfloodlight.openflow.types.OFPort;

public class Forward implements IFloodlightModule,IOFMessageListener{
	
	public static int FLOWMOD_DEFAULT_IDLE_TIMEOUT = 5; // in seconds
	public static int FLOWMOD_DEFAULT_HARD_TIMEOUT = 0; // infinite
	public static int FLOWMOD_DEFAULT_PRIORITY = 1; // 0 is the default table-miss flow in OF1.3+, so we need to use 1
	private Map<String, Map<String,Integer>> macPort;
	protected IOFSwitchService switchService;
	@Override
	public String getName() {
		return "Forward";
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return (type.equals(OFType.PACKET_IN) && (name.equals("topology") || name.equals("devicemanager")));
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;
	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		switch (msg.getType()) {
		case PACKET_IN:
			return this.processPacketInMessage(sw, (OFPacketIn) msg, cntx);
		default:
			break;
		}
		return Command.CONTINUE;
	}
	public Command processPacketInMessage(IOFSwitch sw, OFPacketIn pi, FloodlightContext cntx){
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		OFPort inPort=pi.getMatch().get(MatchField.IN_PORT);
		String dst=eth.getDestinationMACAddress().toString();
		String src=eth.getSourceMACAddress().toString();
		String dpid=sw.getId().toString();
		initMap(dpid);
		macPort.get(dpid).put(src, inPort.getPortNumber());//记录地址
		Integer outPort;
		if(macPort.get(dpid).keySet().contains(dst)){
			outPort=macPort.get(dpid).get(dst);
			//构造match
			Match.Builder mb = sw.getOFFactory().buildMatch();
			mb.setExact(MatchField.IN_PORT, inPort);
			mb.setExact(MatchField.ETH_DST, MacAddress.of(dst));
			mb.setExact(MatchField.ETH_SRC, MacAddress.of(src));
			//构造actions
			OFActionOutput.Builder aob = sw.getOFFactory().actions().buildOutput();
			List<OFAction> actions = new ArrayList<OFAction>();
			aob.setPort(OFPort.of(outPort));
			aob.setMaxLen(Integer.MAX_VALUE);
			actions.add(aob.build());
			
			addFlow(dpid, mb.build(), actions,true);
		}
		else{
			outPort=OFPort.FLOOD.getPortNumber();
		}
		//发送packetout
		OFPacketOut.Builder pob = sw.getOFFactory().buildPacketOut();
		List<OFAction> actions = new ArrayList<OFAction>();
		actions.add(sw.getOFFactory().actions().output(OFPort.FLOOD, Integer.MAX_VALUE)); 
		pob.setActions(actions);
		pob.setInPort(inPort);
		pob.setActions(actions);
		pob.setBufferId(OFBufferId.NO_BUFFER);
		sw.write(pob.build());
		return Command.CONTINUE;
		
	}
	public void addFlow(String dpid,Match match,List<OFAction> actions,boolean doflush){
		DatapathId swId=DatapathId.of(dpid);
		IOFSwitch sw=switchService.getSwitch(swId);
		OFFlowMod.Builder fmb = sw.getOFFactory().buildFlowModify();
		Match.Builder mb = MatchUtils.createRetentiveBuilder(match);
		fmb.setMatch(mb.build())
		.setActions(actions)
		.setIdleTimeout(FLOWMOD_DEFAULT_IDLE_TIMEOUT)
		.setHardTimeout(FLOWMOD_DEFAULT_HARD_TIMEOUT)
		.setBufferId(OFBufferId.NO_BUFFER)
		.setPriority(FLOWMOD_DEFAULT_PRIORITY);
		if(doflush){
			sw.flush();
			sw.write(fmb.build());
			sw.flush();
		}
		else{
			sw.write(fmb.build());
		}
	}
	
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IOFSwitchService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		macPort=new HashMap<String, Map<String,Integer>>();
		switchService = context.getServiceImpl(IOFSwitchService.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		
	}
	
	private Map<String, Map<String,Integer>> initMap(String dpid){
		if(macPort.get(dpid)!=null){
			return macPort;
		}
		else{
			macPort.put(dpid, new HashMap<String, Integer>());
			return macPort;
		}
	}
}
