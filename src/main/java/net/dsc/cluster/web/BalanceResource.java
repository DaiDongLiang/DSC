package net.dsc.cluster.web;

import java.util.List;
import java.util.Set;

import net.dsc.cluster.IClusterService;
import net.dsc.hazelcast.IHazelcastService;
import net.dsc.hazelcast.message.RoleMessage;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.internal.IOFSwitchService;

import org.projectfloodlight.openflow.protocol.OFControllerRole;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.U64;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class BalanceResource extends ServerResource{
	private static final Logger log = LoggerFactory
			.getLogger(BalanceResource.class);
    @Get("json")
    public void balance(){
        IClusterService clusterService = (IClusterService) getContext().getAttributes().get(IClusterService.class.getCanonicalName());
        IHazelcastService hazelcastService=(IHazelcastService) getContext().getAttributes().get(IHazelcastService.class.getCanonicalName());
        
        List<String> dpidList=ImmutableList.copyOf(clusterService.getMasterMap().keySet());
        List<String> uuidList=ImmutableList.copyOf(clusterService.getControllers().keySet());
        clusterService.getControllerLoad().clear();
        for(int i=0;i<dpidList.size();i++){
        	int index=i % (uuidList.size());
    		hazelcastService.publishRoleMessage(new RoleMessage("MASTER", dpidList.get(i)), uuidList.get(index));
        }
    }
}
