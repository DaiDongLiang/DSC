package net.dsc.cluster;

import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_LOAD_MAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_MAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.CONTROLLER_SWITCH_MULITMAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.MASTER_MAP;
import static net.dsc.cluster.HazelcastTableNameConstant.SWITCHS_LINKS_MULITMAP_NAME;
import static net.dsc.cluster.HazelcastTableNameConstant.SWITCHS_MAP_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.dsc.cluster.model.ControllerModel;
import net.dsc.cluster.model.LinkModel;
import net.dsc.cluster.model.SwitchConnectModel;
import net.dsc.cluster.model.SwitchModel;
import net.dsc.cluster.web.ClusterWebRoutable;
import net.dsc.hazelcast.IHazelcastService;
import net.dsc.hazelcast.listener.ControllerMembershipListener;
import net.dsc.hazelcast.listener.IControllerListener;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;

import org.projectfloodlight.openflow.protocol.OFControllerRole;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.U64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MultiMap;

public class ClusterManager implements IFloodlightModule, IClusterService,
		IControllerListener {

	private static final Logger log = LoggerFactory
			.getLogger(ClusterManager.class);

	protected IFloodlightProviderService floodlightProvider;
	protected IOFSwitchService switchService;
	protected IHazelcastService hazelcast;
	protected IRestApiService restApiService;

	private IMap<String, ControllerModel> controllers;
	private IMap<String, SwitchModel> switchs;
	private MultiMap<ControllerModel, SwitchConnectModel> controllerMappingSwitch;
	private IMap<String, Integer> controllerLoad;
	private MultiMap<String, UUID> masterMap;
	private MultiMap<String, LinkModel> switchlinks;
	public ClusterManager() {
	}
	//链路集合
	@Override
	public void addLink(String dpid, LinkModel link) {
		switchlinks.put(dpid, link);
		
	}
	@Override
	public void deleteLink(String dpid, LinkModel link) {
		switchlinks.remove(dpid, link);
	}
	@Override
	public MultiMap<String, LinkModel> getLinks() {
		return switchlinks;
	}
	
	// 交换机集合
	@Override
	public void putSwitch(SwitchModel s) {
		switchs.put(s.getDpid(), s);
	}

	@Override
	public IMap<String, SwitchModel> getSwithcs() {
		return switchs;
	}

	@Override
	public void removeSwitch(String dpid) {
		switchs.remove(dpid);
	}

	// 控制器负载
	@Override
	public List<String> getSortedControllerLoad() {
		List<String> list = new ArrayList<String>();
		if (controllerLoad != null && !controllerLoad.isEmpty()) {
			List<Map.Entry<String, Integer>> entryList = new ArrayList<Map.Entry<String, Integer>>(
					controllerLoad.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Integer>>() {
						public int compare(Entry<String, Integer> entry1,
								Entry<String, Integer> entry2) {
							int value1 = 0, value2 = 0;
							value1 = entry1.getValue();
							value2 = entry2.getValue();
							return value1 - value2;
						}
					});
			for (Iterator<Map.Entry<String, Integer>> i = entryList.iterator(); i
					.hasNext();) {
				Map.Entry<String, Integer> entry = i.next();
				list.add(entry.getKey());
			}
		}
		return list;
	}

	@Override
	public synchronized void ControllerLoadIncrease(String controllerId, int num) {
		log.info("controller {} increase {}", controllerId, num);
		if (num < 0)
			throw new IllegalArgumentException("num < 0");
		Integer i = controllerLoad.get(controllerId);
		if (null == i)
			controllerLoad.put(controllerId, num);
		else
			controllerLoad.put(controllerId, i + num);

	}

	@Override
	public synchronized void ControllerLoadReduce(String controllerId, int num) {
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
		controllerLoad.put(controllerId, 0);
	}
	
	@Override
	public IMap<String, Integer> getControllerLoad() {
		return controllerLoad;
	}

	// 添加控制器集合
	@Override
	public void addController(ControllerModel c) {
		controllers.put(c.getControllerId(), c);
	}

	@Override
	public void removeController(ControllerModel c) {
		controllers.remove(c.getControllerId());

	}
	@Override
	public IMap<String, ControllerModel> getControllers() {
		return controllers;
	}
	
	//masterMap
	@Override
	public void putMasterMap(String dpid) {
		if(masterMap.keySet().contains(dpid)){
			masterMap.remove(dpid);
		}
		masterMap.put(dpid, UUID.fromString(floodlightProvider.getControllerModel()
				.getControllerId()));
	}

	@Override
	public void removeMasterMap(String dpid) {
		if (masterMap.containsKey(dpid))
			masterMap.remove(dpid);
	}
	@Override
	public MultiMap<String, UUID> getMasterMap() {
		return masterMap;
	}
	
	@Override
	public Map<String, UUID> getMasterMapFromCS() {
		Map<String, UUID> master=new HashMap<String, UUID>();
		for(ControllerModel c:controllerMappingSwitch.keySet()){
			for(SwitchConnectModel s:controllerMappingSwitch.get(c)){
				if(s.getRole().equals(OFControllerRole.ROLE_MASTER)){
					master.put(s.getDpid(), UUID.fromString(c.getControllerId()));
				}
			}
		}
		return master;
	}
	
	// 控制器映射

	@Override
	public boolean isConnected(String dpid, String uuid) {
		ControllerModel c = controllers.get(uuid);
		Collection<SwitchConnectModel> switchs = controllerMappingSwitch.get(c);
		for (SwitchConnectModel s : switchs) {
			if (s.getDpid().equals(dpid))
				return true;
		}
		return false;
	}

	@Override
	public void removeControllerMappingSwitch(ControllerModel c, String dpid,String role) {
		controllerMappingSwitch.remove(c, new SwitchConnectModel(c.getControllerId(), dpid, role));
	}

	@Override
	public synchronized void putControllerMappingSwitch(ControllerModel c,
			String dpid, String role) {
		putControllerMappingSwitch(c, new SwitchConnectModel(c.getControllerId(), dpid, role));
	}
	
	private void putControllerMappingSwitch(ControllerModel c,
			SwitchConnectModel s) {
		for (Iterator<SwitchConnectModel> i = controllerMappingSwitch.get(c)
				.iterator(); i.hasNext();) {
			SwitchConnectModel scm = i.next();
			if (scm.getDpid().equals(s.getDpid())) {
				controllerMappingSwitch.remove(c, scm);
				controllerMappingSwitch.put(c, s);
				return;
			}
		}
		controllerMappingSwitch.put(c, s);
	}
	@Override
	public MultiMap<ControllerModel, SwitchConnectModel> getControllerMappingSwitch() {
		return controllerMappingSwitch;
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
		l.add(IRestApiService.class);
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
		restApiService = context.getServiceImpl(IRestApiService.class);
		controllers = hazelcast.getMap(CONTROLLER_MAP_NAME);
		controllerMappingSwitch = hazelcast.getMultiMap(CONTROLLER_SWITCH_MULITMAP_NAME);
		controllerLoad = hazelcast.getMap(CONTROLLER_LOAD_MAP_NAME);
		masterMap = hazelcast.getMultiMap(MASTER_MAP);
		switchs = hazelcast.getMap(SWITCHS_MAP_NAME);
		switchlinks=hazelcast.getMultiMap(SWITCHS_LINKS_MULITMAP_NAME);
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		hazelcast.addMemberListener(new ControllerMembershipListener(this));
		if (restApiService != null)
			restApiService.addRestletRoutable(new ClusterWebRoutable());

	}

	// IControllerListener implements
	@Override
	public void controllerRemoved(MembershipEvent event) {
		Member m = event.getMember();
		log.info("{} disconnected", m.getUuid());
		controllerLoad.remove(m.getUuid());// 移除故障控制器负载
		List<String> load = ImmutableList.copyOf(getSortedControllerLoad());// 取得控制器负载排序
		String uuid = floodlightProvider.getControllerModel().getControllerId();// 得到本机uuid
		ControllerModel c = controllers.get(m.getUuid());// 得到故障控制器模型
		Collection<SwitchConnectModel> switchs = controllerMappingSwitch.get(c);// 得到故障控制器控制的交换机
		if (uuid.equals(load.get(0))) {
			for (SwitchConnectModel s : switchs) {// 遍历交换机
				for (int i = 0; i < load.size(); i++) {
					if (s.getRole().equals(OFControllerRole.ROLE_MASTER.toString()) && isConnected(s.getDpid(), load.get(i))) {// 如果交换机角色MASTER并且负载最小的是自己.
						DatapathId dpid = DatapathId.of(s.getDpid());
						removeMasterMap(dpid.toString());
						IOFSwitch sw = switchService.getSwitch(dpid);
						log.info("change master {}<-->{}", uuid, dpid);
						sw.writeRequest(sw.getOFFactory().buildRoleRequest()
								.setGenerationId(U64.ZERO)
								.setRole(OFControllerRole.ROLE_MASTER).build());
						break;
					}
				}
			}
			controllerMappingSwitch.remove(c);
			controllers.remove(c.getControllerId());
		}
	}
}
