package net.floodlightcontroller.debugevent;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;
public class CustomFormatterString implements CustomFormatter<String> {

    @Override
    public EventResourceBuilder customFormat(@Nullable String string,
                                             String name,
                                             EventResourceBuilder edb) {
        if (string != null) {
            edb.dataFields.add(new Metadata(name, string));
        }
        return edb;
    }

}
