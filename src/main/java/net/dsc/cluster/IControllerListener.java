package net.dsc.cluster;

import com.hazelcast.core.MembershipEvent;

public interface  IControllerListener {
	public void controllerRemoved(MembershipEvent event);
}
