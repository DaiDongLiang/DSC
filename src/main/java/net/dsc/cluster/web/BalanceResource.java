package net.dsc.cluster.web;

import java.util.List;
import java.util.Map;

import net.dsc.cluster.IClusterService;
import net.dsc.hazelcast.IHazelcastService;
import net.dsc.hazelcast.message.RoleMessage;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class BalanceResource extends ServerResource{
	private static final Logger log = LoggerFactory
			.getLogger(BalanceResource.class);
    @Get("json")
    public String balance(){
        IClusterService clusterService = (IClusterService) getContext().getAttributes().get(IClusterService.class.getCanonicalName());
        IHazelcastService hazelcastService=(IHazelcastService) getContext().getAttributes().get(IHazelcastService.class.getCanonicalName());
        
        List<String> dpidList=ImmutableList.copyOf(clusterService.getMasterMap().keySet());
        List<String> uuidList=ImmutableList.copyOf(clusterService.getControllers().keySet());
        Map<String,String> masterMap=ImmutableMap.copyOf(clusterService.getMasterMap());
        clusterService.getControllerLoad().clear();
        for(Map.Entry<String, String> m:masterMap.entrySet()){//将所有主控变为从控
       		hazelcastService.publishRoleMessage(new RoleMessage("SLAVE", m.getKey()),m.getValue());	
        }
        for(int i=0;i<dpidList.size();i++){
        	for(int index=0;index<uuidList.size();index++){
        		if(clusterService.isConnected(dpidList.get(i), uuidList.get(index))){
        			hazelcastService.publishRoleMessage(new RoleMessage("MASTER", dpidList.get(i)), uuidList.get(index));
					log.info("change master {}<-->{}", uuidList.get(index),  dpidList.get(i));
        			String temp=uuidList.get(index);
        			uuidList.remove(index);
        			uuidList.add(temp);
        			break;
        		}
        	}
        }
        return "balance begin";
    }
}
