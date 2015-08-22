package net.dsc.cluster.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.dsc.cluster.IClusterService;
import net.dsc.hazelcast.IHazelcastService;
import net.dsc.hazelcast.message.RoleMessage;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BalanceResource extends ServerResource{
	private static final Logger log = LoggerFactory
			.getLogger(BalanceResource.class);
    @Get("json")
    public String balance(){
        IClusterService clusterService = (IClusterService) getContext().getAttributes().get(IClusterService.class.getCanonicalName());
        IHazelcastService hazelcastService=(IHazelcastService) getContext().getAttributes().get(IHazelcastService.class.getCanonicalName());
        
        List<String> dpidList=Lists.newArrayList(clusterService.getMasterMap().keySet());
        List<String> uuidList= Lists.newArrayList(clusterService.getControllers().keySet());
        Map<String,UUID> masterMap=Maps.newHashMap(clusterService.getMasterMap());
        for(String u:uuidList){
        	clusterService.ControllerLoadReset(u);
        }
        System.out.println(clusterService.getMasterMap());
        for(Map.Entry<String, UUID> m:masterMap.entrySet()){//将所有主控变为从控
        	System.out.println(m.getKey()+"----"+m.getValue());
       		hazelcastService.publishRoleMessage(new RoleMessage("SLAVE", m.getKey()),m.getValue().toString());	
        }
//        for(int i=0;i<dpidList.size();i++){
//        	int length=uuidList.size();
//        	System.out.println(i);
//        	for(int index=0;index<length;index++){
//        		if(clusterService.isConnected(dpidList.get(i), uuidList.get(index))){
//        			hazelcastService.publishRoleMessage(new RoleMessage("MASTER", dpidList.get(i)), uuidList.get(index));
//					log.info("change master {}<-->{}", uuidList.get(index),  dpidList.get(i));
//        			String temp=uuidList.get(index);
//        			System.out.println(index);
//        			uuidList.add(temp);
//        			break;
//        		}
//        	}
//        }
        return "balance begin";
    }
}
