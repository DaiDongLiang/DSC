package net.dsc.cluster.web;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class ClusterWebRoutable implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
        router.attach("/controller/switches/json", ControllerSwitchesResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/cluster";
	}

}
