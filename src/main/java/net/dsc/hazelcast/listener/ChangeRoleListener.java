package net.dsc.hazelcast.listener;

import net.dsc.hazelcast.message.FlowMessage;
import net.dsc.hazelcast.message.RoleMessage;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class ChangeRoleListener implements MessageListener<RoleMessage>{

	@Override
	public void onMessage(Message<RoleMessage> message) {
		// TODO Auto-generated method stub
		RoleMessage roleMessage = message.getMessageObject();
	}

}
