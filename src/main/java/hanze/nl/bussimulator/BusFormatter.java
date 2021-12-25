package hanze.nl.bussimulator;

public class BusFormatter implements FormatterInterface<Bus, Bericht> {
    private int tijd;

    public BusFormatter(int tijd) {
        this.tijd = tijd;
    }

    /**
     * @param element Bus Converts a bus into a MQTT message.
     *
     * @return Formatted message.
     */
    public Bericht format(Bus element) {
        Bericht bericht = new Bericht(
                element.getLijn().name(),
                element.getBedrijf().name(),
                element.getBusID(),
                this.tijd
        );

        bericht.ETAs = element.getETAs(this.tijd);

        return bericht;
    }

    public void setTijd(int tijd) {
        this.tijd = tijd;
    }
}
