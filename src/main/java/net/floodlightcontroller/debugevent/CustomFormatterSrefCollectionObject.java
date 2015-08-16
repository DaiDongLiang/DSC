package net.floodlightcontroller.debugevent;

import java.lang.ref.SoftReference;
import java.util.Collection;

import javax.annotation.Nullable;

import net.floodlightcontroller.debugevent.EventResource.EventResourceBuilder;
import net.floodlightcontroller.debugevent.EventResource.Metadata;

public class CustomFormatterSrefCollectionObject implements
CustomFormatter<SoftReference<Collection<Object>>> {

@Override
public EventResourceBuilder customFormat(@Nullable
                                     SoftReference<Collection<Object>> srefCollectionObj2,
                                     String name, EventResourceBuilder edb) {
    if (srefCollectionObj2 != null) {
        Collection<Object> ol2 = srefCollectionObj2.get();
        if (ol2 != null) {
            StringBuilder sb = new StringBuilder();
            if (ol2.size() == 0) {
                sb.append("--");
            } else {
                for (Object o : ol2) {
                    sb.append(o.toString());
                    sb.append(" ");
                }
            }
            edb.dataFields.add(new Metadata(name, sb.toString()));
        } else {
            edb.dataFields.add(new Metadata(name,
                                            "-- reference not available --"));
        }
    }
    return edb;
}

}
