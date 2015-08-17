package net.dsc.ha;

import static net.dsc.ha.HazelcastTableNameConstant.*;

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


public class HAManager implements IFloodlightModule {
	private static final Logger log = LoggerFactory.getLogger(HAManager.class);
	protected IFloodlightProviderService floodlightProvider;
	private IOFSwitchService switchService;
	protected IHazelcastService hazelcast;

	private List<ControllerModel> controllers;
	private MultiMap<ControllerModel, SwitchConnectModel> controllerMappingSwitch;
	private IMap<String, Integer> controllerLoad;
	private IMap<String, String> masterMap;

	public HAManager() {
	}

	// ===============================IFloodlightModule=======================
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
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
		Map<String, String> configParams = context.getConfigParams(this);
		floodlightProvider = context
				.getServiceImpl(IFloodlightProviderService.class);
		hazelcast = context.getServiceImpl(IHazelcastService.class);
		switchService = context.getServiceImpl(IOFSwitchService.class);

		controllers = hazelcast.getList(CONTROLLER_MAP_NAME);
		controllerMappingSwitch = hazelcast
				.getMultiMap(CONTROLLER_SWITCH_MULITMAP_NAME);
		controllerLoad = hazelcast.getMap(CONTROLLER_LOAD_MAP_NAME);
		masterMap = hazelcast.getMap(MASTER_MAP);

		controllers.add(floodlightProvider.getControllerModel());
	}

	private void initControllerLoadMap() {

	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {

	}
}
