package hanze.nl.bussimulator;

import com.thoughtworks.xstream.XStream;

public class BerichtXMLFormatter implements FormatterInterface<Bericht, String> {
    public String format(Bericht element) {
        XStream xstream = new XStream();
        xstream.alias("Bericht", Bericht.class);
        xstream.alias("ETA", ETA.class);

        return xstream.toXML(element);
    }
}
