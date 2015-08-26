package net.dsc.cluster.web;

import net.dsc.hazelcast.IHazelcastService;
import net.floodlightcontroller.core.IShutdownService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class ShutDownResource extends ServerResource{
	@Get("json")
	public void shutdown(){
        IHazelcastService hazelcastService = (IHazelcastService) getContext().getAttributes().get(IHazelcastService.class.getCanonicalName());
        IShutdownService shutService = (IShutdownService) getContext().getAttributes().get(IShutdownService.class.getCanonicalName());
        hazelcastService.getInstance().shutdown();
        shutService.terminate("test", 0);
//        System.exit(0);
	}
}
