package net.dsc.hazelcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.dsc.hazelcast.message.FlowMessage;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.MultiMap;

public class HazelcastService implements IHazelcastService,IFloodlightModule{
	public static final String FlowMessageTopic = "flowMessageTopic";
	
	private static final Logger log = LoggerFactory
			.getLogger(HazelcastService.class);

	private HazelcastInstance hazelcastInstance = null;
	private HazelcastInstance client = null;

	@Override
	public <K, V> IMap<K,V> getMap(String MapName) {
		IMap<K, V> map = client.getMap(MapName);
		return map;
	}
	@Override
	public <K, V> MultiMap<K,V> getMultiMap(String MapName) {
		MultiMap<K, V> map = client.getMultiMap(MapName);
		return map;
	}
	@Override
	public <T> ISet<T> getSet(String SetName) {
		ISet<T> set = client.getSet(SetName);
		return set;
	}

	@Override
	public <T> IQueue<T> getQueue(String QueueName) {
		IQueue<T> queue = client.getQueue(QueueName);
		return queue;
	}

	@Override
	public <T> IList<T> getList(String ListName) {
		IList<T> list = client.getList(ListName);
		return list;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		l.add(IHazelcastService.class);
		return l;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
		m.put(IHazelcastService.class, this);
		return m;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		return l;
	}
	//test
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		log.info("Hazelcast Init");
		hazelcastInstance = HazelcastManager.getHazelcastInstance();
		client = HazelcastManager.getHazelcastClient();
	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		HazelcastListenerManager.addFlowMessageListener(FlowMessageTopic);
		
		
	}
	
	@Override
	public void publishFlowMessage(FlowMessage flowMessage) {
		ITopic<FlowMessage> topic =  client.getTopic(FlowMessageTopic);
		topic.publish(flowMessage);
	}
	
	
	@Override
	public Member getLocalMember() {
		return hazelcastInstance.getCluster().getLocalMember();
		
	}
	@Override
	public void addMemberListener(MembershipListener mebershipListener) {
		HazelcastListenerManager.addMemberListener(mebershipListener);
		
	}	
}
