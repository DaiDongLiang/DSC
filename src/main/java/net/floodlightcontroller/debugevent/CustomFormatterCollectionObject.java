package net.floodlightcontroller.debugevent;

import java.util.Collection;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;

public class CustomFormatterCollectionObject implements
CustomFormatter<Collection<Object>> {

@Override
public EventResourceBuilder
        customFormat(@Nullable Collection<Object> obl2, String name,
                     EventResourceBuilder edb) {
    if (obl2 != null) {
        StringBuilder sbldr2 = new StringBuilder();
        if (obl2.size() == 0) {
            sbldr2.append("--");
        } else {
            for (Object o : obl2) {
                sbldr2.append(o.toString());
                sbldr2.append(" ");
            }
        }
        edb.dataFields.add(new Metadata(name, sbldr2.toString()));
    }
    return edb;
}
}
