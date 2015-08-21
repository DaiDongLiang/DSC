/**
*    Copyright 2011, Big Switch Networks, Inc.
*    Originally created by David Erickson, Stanford University
*
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package net.dsc.cluster.web;

import java.util.HashSet;
import java.util.Set;

import net.dsc.cluster.IClusterService;
import net.dsc.cluster.SwitchModel;
import net.floodlightcontroller.core.web.serializers.DPIDSerializer;

import org.projectfloodlight.openflow.types.DatapathId;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Get a list of switches connected to the controller
 * 得到一个已连接控制器的交换机列表
 * @author readams
 */
public class SwitchesResource extends ServerResource {
    
    public static final String DPID_ERROR = "Invalid switch DPID string. Must be a 64-bit value in the form 00:11:22:33:44:55:66:77.";
    public static class DatapathIDJsonSerializerWrapper {
        private final DatapathId dpid;
        private final String version;
        private final String inetAddress; 
        private final String switch_add_time;
        public DatapathIDJsonSerializerWrapper(DatapathId dpid, String inetAddress, String switch_add_time,String version) {
            this.dpid = dpid;
            this.inetAddress = inetAddress;
            this.switch_add_time = switch_add_time;
            this.version=version;
        }
        
        @JsonSerialize(using=DPIDSerializer.class)
        public DatapathId getSwitchDPID() {
            return dpid;
        }
        public String getInetAddress() {
            return inetAddress;
        }
        public String getConnectedSince() {
            return switch_add_time;
        }

		public String getVersion() {
			return version;
		}
        
    }

    @Get("json")
    public Set<DatapathIDJsonSerializerWrapper> retrieve(){
        IClusterService clusterService = (IClusterService) getContext().getAttributes().get(IClusterService.class.getCanonicalName());
        Set<DatapathIDJsonSerializerWrapper> dpidSets = new HashSet<DatapathIDJsonSerializerWrapper>();
        for(SwitchModel s:clusterService.getSwithcs().values()){
            dpidSets.add(new DatapathIDJsonSerializerWrapper(DatapathId.of(s.getDpid()),s.getIp(),s.getDate().toString(),s.getVersoin()));	
        }
        return dpidSets;
    }
}
