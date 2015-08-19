package net.dsc.cluster;

import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_LOAD_MAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_LIST_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_SWITCH_MULITMAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.MASTER_MAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dsc.hazelcast.IHazelcastService;
import net.dsc.hazelcast.listener.ControllerMembershipListener;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import org.projectfloodlight.openflow.protocol.OFControllerRole;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.U64;
import org.python.modules.synchronize;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;

import com.hazelcast.core.MembershipEvent;

import com.hazelcast.core.MultiMap;


public class ClusterManager implements IFloodlightModule, IClusterService,
 
	
	


		IControllerListener{

	private static final Logger log = LoggerFactory
			.getLogger(ClusterManager.class);

	protected IFloodlightProviderService floodlightProvider;
	protected IOFSwitchService switchService;
	protected IHazelcastService hazelcast;


	private List<ControllerModel> controllers;
	private MultiMap<ControllerModel, SwitchConnectModel> controllerMappingSwitch;
	private IMap<String, Integer> controllerLoad;
	private IMap<String, String> masterMap;

	public ClusterManager() {
	}

	@Override
	public String getMinControllerLoad() {
		String result = null;
		Integer min = 0;
		for (String uuid : controllerLoad.keySet()) {
			if (min == 0) {
				min = controllerLoad.get(uuid);
				result = uuid;
			} else {
				if (min >= controllerLoad.get(uuid)) {
					min = controllerLoad.get(uuid);
					result = uuid;
				}
			}
		}
		return result;
	}

	@Override
	public synchronized void ControllerLoadIncrease(String controllerId, int num) {
		log.info("controller {} increase {}",controllerId,num);
		if (num < 0)
			throw new IllegalArgumentException("num < 0");
		Integer i = controllerLoad.get(controllerId);
		System.out.println("I---"+i);
		if (null == i)
			controllerLoad.put(controllerId, num);
		else
			controllerLoad.put(controllerId, i + num);

		
		log.info(controllerLoad.get(controllerId).toString());

	}

	@Override
	public void ControllerLoadReduce(String controllerId, int num) {
		if (num < 0)
			throw new IllegalArgumentException("num < 0");
		Integer i = controllerLoad.get(controllerId);
		if (null == i)
			ControllerLoadReset(controllerId);
		else
			controllerLoad.set(controllerId, i - num < 0 ? 0 : i - num);
	}

	@Override
	public void ControllerLoadReset(String controllerId) {
		if (controllerLoad.containsKey(controllerId))
			controllerLoad.clear();
		else
			controllerLoad.put(controllerId, 0);
	}

	// 添加控制器集合
	@Override
	public void addController(ControllerModel c) {
		controllers.add(c);
	}

	@Override
	public void removeController(ControllerModel c) {
		controllers.remove(c);

	}

	@Override
	public void putMasterMap(String dpid) {
		masterMap.put(dpid, floodlightProvider.getControllerModel()
				.getControllerId());
	}

	@Override
	public void removeMasterMap(String dpid) {
		if (masterMap.containsKey(dpid))
			masterMap.remove(dpid);
	}

	@Override
	public void removeControllerMappingSwitch(ControllerModel c, String dpid,
			String role) {

		SwitchConnectModel s = new SwitchConnectModel(c.getControllerId(),
				dpid, role);
		controllerMappingSwitch.remove(c, s);
	}

	@Override
	public void putControllerMappingSwitch(ControllerModel c, String dpid,
			String role) {
		putControllerMappingSwitch(c,
				new SwitchConnectModel(c.getControllerId(), dpid, role));
	}

	private void putControllerMappingSwitch(ControllerModel c,
			SwitchConnectModel s) {
		controllerMappingSwitch.put(c, s);
	}

	public List<ControllerModel> getControllers() {
		return controllers;
	}

	public MultiMap<ControllerModel, SwitchConnectModel> getControllerMappingSwitch() {
		return controllerMappingSwitch;
	}

	public IMap<String, String> getMasterMap() {
		return masterMap;
	}

	// ===============================IFloodlightModule=======================
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IClusterService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IClusterService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IFloodlightProviderService.class);
		l.add(IHazelcastService.class);
		l.add(IOFSwitchService.class);
		return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		log.info("HAManager init");
		floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		hazelcast = context.getServiceImpl(IHazelcastService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);
		controllers = hazelcast.getList(CONTROLLER_LIST_NAME);
		controllerMappingSwitch = hazelcast
				.getMultiMap(CONTROLLER_SWITCH_MULITMAP_NAME);
		controllerLoad = hazelcast.getMap(CONTROLLER_LOAD_MAP_NAME);
		masterMap = hazelcast.getMap(MASTER_MAP);

	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {


	}

	

	// IControllerListener implements
	@Override
	public void controllerRemoved(MembershipEvent event) {	
		Member m = event.getMember();
		log.info("{} disconnected",m.getUuid());
		ControllerModel c = new ControllerModel(m.getUuid(), m.getSocketAddress().getAddress().toString());
		String uuid = getMinControllerLoad();
		controllerLoad.remove(uuid);
		if (uuid.equals(floodlightProvider.getControllerModel().getControllerId())) {
			log.info("change master to {}",uuid);
			Collection<SwitchConnectModel> switchs = controllerMappingSwitch.get(c);
			for (SwitchConnectModel s : switchs) {
				if (s.getRole().equals(OFControllerRole.ROLE_MASTER.toString())) {
					DatapathId dpid = DatapathId.of(s.getDpid());
					removeMasterMap(dpid.toString());
					IOFSwitch sw = switchService.getSwitch(dpid);
					log.info("change master {}<-->{}",uuid,dpid);
					sw.writeRequest(sw.getOFFactory()
							.buildRoleRequest()
							.setGenerationId(U64.ZERO)
							.setRole(OFControllerRole.ROLE_MASTER).
							build());
				}
			}
			controllerMappingSwitch.remove(c);
		}
		
		
		
		
	}

}


