package net.dsc.hazelcast;

import net.dsc.hazelcast.message.FlowMessage;
import net.dsc.hazelcast.message.RoleMessage;

import com.hazelcast.core.Message;

public interface IMessageListener  {
	public void progressRoleMessage(Message<RoleMessage> roleMessage);
	
	public void progressFlowMessage(Message<FlowMessage> flowMessage);
}
