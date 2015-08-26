package net.dsc.cluster.web;

import net.dsc.hazelcast.IHazelcastService;
import net.dsc.hazelcast.message.ShutDownMessage;
import net.floodlightcontroller.core.IShutdownService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ShutDownResource extends ServerResource{
	@Get("json")
	public void shutdown(){
        IHazelcastService hazelcastService = (IHazelcastService) getContext().getAttributes().get(IHazelcastService.class.getCanonicalName());
        IShutdownService shutService = (IShutdownService) getContext().getAttributes().get(IShutdownService.class.getCanonicalName());
		String controllerId = (String) getRequestAttributes().get("controllerId");
		hazelcastService.publishShutMessage(new ShutDownMessage(), controllerId);
	}
}
