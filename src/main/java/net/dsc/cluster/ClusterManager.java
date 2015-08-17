package net.dsc.cluster;

import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_LOAD_MAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_MAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_SWITCH_MULITMAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.MASTER_MAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dsc.hazelcast.IHazelcastService;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;


public class ClusterManager implements IFloodlightModule,IClusterService{
	private static final Logger log = LoggerFactory.getLogger(ClusterManager.class);
	protected IFloodlightProviderService floodlightProvider;
	private IOFSwitchService switchService;
	protected IHazelcastService hazelcast;

	private List<ControllerModel> controllers;
	private MultiMap<ControllerModel, SwitchConnectModel> controllerMappingSwitch;
	private IMap<String, Integer> controllerLoad;
	private IMap<String, String> masterMap;

	public ClusterManager() { }
	
	@Override
	public void addController(ControllerModel c) {
		controllers.add(c);
	}
	@Override
	public void removeController(ControllerModel c) {
		controllers.remove(c);
		
	}
	@Override
    public void putMasterMap(String dpid){
    	masterMap.put(dpid, floodlightProvider.getControllerModel().getControllerId());
    }
	@Override
    public void removeMasterMap(String dpid){
    	if(masterMap.containsKey(dpid)) masterMap.remove(dpid);
    }
	@Override
	public void removeControllerMappingSwitch(ControllerModel c, String dpid,String role) {
		SwitchConnectModel s=new SwitchConnectModel(c.getControllerId(), dpid, role);
		controllerMappingSwitch.remove(c, s);
	}
	@Override
	public void putControllerMappingSwitch(ControllerModel c,String dpid,String role){
		putControllerMappingSwitch(c, new SwitchConnectModel(c.getControllerId(), dpid, role));
	}
	
	private void putControllerMappingSwitch(ControllerModel c,SwitchConnectModel s){
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

		controllers = hazelcast.getList(CONTROLLER_MAP_NAME);
		controllerMappingSwitch = hazelcast
				.getMultiMap(CONTROLLER_SWITCH_MULITMAP_NAME);
		controllerLoad = hazelcast.getMap(CONTROLLER_LOAD_MAP_NAME);
		masterMap = hazelcast.getMap(MASTER_MAP);

	}
	
	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
	}

}
