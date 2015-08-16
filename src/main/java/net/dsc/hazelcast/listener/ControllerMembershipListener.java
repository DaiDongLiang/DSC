package net.dsc.hazelcast.listener;


import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;

public class ControllerMembershipListener implements MembershipListener {

	@Override
	public void memberAdded(MembershipEvent arg0) {
		// TODO Auto-generated method stub
		
		System.out.println("member add");
	}

	@Override
	public void memberAttributeChanged(MemberAttributeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void memberRemoved(MembershipEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
