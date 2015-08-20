package net.dsc.cluster.web;

import java.util.Set;

import net.dsc.cluster.ClusterManager;
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

import com.google.common.collect.ImmutableSet;

public class BalanceResource extends ServerResource{
	private static final Logger log = LoggerFactory
			.getLogger(BalanceResource.class);
    @Get("json")
    public void balance(){
        IClusterService clusterService = (IClusterService) getContext().getAttributes().get(IClusterService.class.getCanonicalName());
        IFloodlightProviderService floodlightprovider=(IFloodlightProviderService) getContext().getAttributes().get(IFloodlightProviderService.class.getCanonicalName());
        IOFSwitchService switchService=(IOFSwitchService) getContext().getAttributes().get(IOFSwitchService.class.getCanonicalName());
        IHazelcastService hazelcastService=(IHazelcastService) getContext().getAttributes().get(IHazelcastService.class.getCanonicalName());
        String uuid=floodlightprovider.getControllerModel().getControllerId();
        Set<String> dpidSet=ImmutableSet.copyOf(clusterService.getMasterMap().keySet());
        for(String dpid:dpidSet){
        	String cId=clusterService.getSortedControllerLoad().get(0);
        	if(cId.equals(uuid)){
				IOFSwitch sw = switchService.getSwitch(DatapathId.of(dpid));
				log.info("change master {}<-->{}",uuid,dpid);
				sw.writeRequest(sw.getOFFactory()
						.buildRoleRequest()
						.setGenerationId(U64.ZERO)
						.setRole(OFControllerRole.ROLE_MASTER).
						build());
        	}
        	else{
        		hazelcastService.publishRoleMessage(new RoleMessage("MASTER", dpid), cId);
        	}
        }
    }
}
