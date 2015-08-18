package net.dsc.hazelcast;

import net.dsc.hazelcast.listener.ControllerMembershipListener;
import net.dsc.hazelcast.listener.FlowMessageListener;
import net.dsc.hazelcast.message.FlowMessage;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

public class HazelcastListenerManager {
	private static HazelcastInstance instance = HazelcastManager.getHazelcastInstance();
	private static HazelcastInstance client = HazelcastManager.getHazelcastClient();
	
	public static void addMemberListener(){//添加成员事件监听
		instance.getCluster().addMembershipListener(new ControllerMembershipListener());
	}
	
	public static void addFlowMessageListener(String TopicName){//添加流表事件监听
		ITopic<FlowMessage> topic = client.getTopic(TopicName);
		topic.addMessageListener(new FlowMessageListener());
	}
}
