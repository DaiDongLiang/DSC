package net.floodlightcontroller.debugevent;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;

public class CustomFormatterPrimitive implements CustomFormatter<Object> {
    @Override
    public EventResourceBuilder customFormat(@Nullable Object obj,
                                             String name,
                                             EventResourceBuilder edb) {
        if (obj != null) {
            edb.dataFields.add(new Metadata(name, obj.toString()));
        }
        return edb;
    }
}
