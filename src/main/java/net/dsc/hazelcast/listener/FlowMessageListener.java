package net.dsc.hazelcast.listener;

import net.dsc.hazelcast.message.FlowMessage;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class FlowMessageListener implements MessageListener<FlowMessage>{

	@Override
	public void onMessage(Message<FlowMessage> message) {
		// TODO Auto-generated method stub
		FlowMessage flowMessage = message.getMessageObject();
	}

}
