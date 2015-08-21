package net.dsc.cluster.web;

import net.floodlightcontroller.restserver.RestletRoutable;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class ClusterWebRoutable implements RestletRoutable{

	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
        router.attach("/switches/json", SwitchesResource.class);
        router.attach("/controllers/json", ControllersResource.class);
        router.attach("/balance", BalanceResource.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/cluster";
	}

}
