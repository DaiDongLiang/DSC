package net.dsc.hazelcast.listener;


import net.dsc.cluster.IControllerListener;

import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

public class ControllerMembershipListener implements MembershipListener {
	
	private static IControllerListener cluster;
	
	public ControllerMembershipListener(IControllerListener c) {
		this.cluster=c;
	}
	public ControllerMembershipListener() { }
	@Override
	public void memberAdded(MembershipEvent arg0) {

	}

	@Override
	public void memberAttributeChanged(MemberAttributeEvent arg0) {
		
	}

	@Override
	public void memberRemoved(MembershipEvent event) {
			cluster.controllerRemoved(event);
	}

}
