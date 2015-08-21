package net.dsc.hazelcast.listener;

import com.hazelcast.core.MembershipEvent;

public interface  IControllerListener {
	public void controllerRemoved(MembershipEvent event);
}
