package net.floodlightcontroller.debugevent;

import java.lang.ref.SoftReference;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;

public class CustomFormatterSrefObject implements CustomFormatter<SoftReference<Object>> {

    @Override
    public EventResourceBuilder
            customFormat(@Nullable SoftReference<Object> srefObj,
                         String name, EventResourceBuilder edb) {
        if (srefObj != null) {
            Object o = srefObj.get();
            if (o != null) {
                edb.dataFields.add(new Metadata(name, o.toString()));
            } else {
                edb.dataFields.add(new Metadata(name,
                                                "-- reference not available --"));
            }
        }
        return edb;
    }

}
