package net.dsc.cluster.web;

import java.util.HashSet;
import java.util.Set;

import net.dsc.cluster.ControllerModel;
import net.dsc.cluster.IClusterService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ControllersResource extends ServerResource{
    public static class ControllersJsonSerializerWrapper {
        private final String controllerId;
        private final String controllerIp;
        private final Integer controllerLoad; 
        
        public ControllersJsonSerializerWrapper(String controllerId,
				String controllerIp, Integer controllerLoad) {
			this.controllerId = controllerId;
			this.controllerIp = controllerIp;
			this.controllerLoad =controllerLoad;
		}

		public String getControllerId() {
			return controllerId;
		}

		public String getControllerIp() {
			return controllerIp;
		}

		public Integer getControllerLoad() {
			return controllerLoad;
		}
		
    }

    @Get("json")
    public Set<ControllersJsonSerializerWrapper> retrieve(){
        IClusterService clusterService = (IClusterService) getContext().getAttributes().get(IClusterService.class.getCanonicalName());
        
        Set<ControllersJsonSerializerWrapper> controllersSets = new HashSet<ControllersJsonSerializerWrapper>();
        for(ControllerModel  c:clusterService.getControllers().values()){
        	controllersSets.add(new ControllersJsonSerializerWrapper(c.getControllerId(),c.getControllerIp(),clusterService.getControllerLoad().get(c.getControllerId())));	
        }
        return controllersSets;
    }

}
