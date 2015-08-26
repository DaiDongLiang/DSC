package net.dsc.hazelcast.listener;


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
			long begin =System.currentTimeMillis();
			cluster.controllerRemoved(event);
			long end=System.currentTimeMillis();
			System.out.println("translation time:"+(end-begin));
	}

}
