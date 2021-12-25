package hanze.nl.bussimulator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import hanze.nl.tijdtools.TijdFuncties;

public class Runner {
	private static HashMap<Integer,ArrayList<Bus>> busStart = new HashMap<Integer,ArrayList<Bus>>();
	private static ArrayList<Bus> actieveBussen = new ArrayList<Bus>();
	private static int interval=1000;
	private static int syncInterval=5;
	
	private static void addBus(int starttijd, Bus bus){
		ArrayList<Bus> bussen = new ArrayList<Bus>();
		if (busStart.containsKey(starttijd)) {
			bussen = busStart.get(starttijd);
		}
		bussen.add(bus);
		busStart.put(starttijd,bussen);
		bus.setbusID(starttijd);
	}
	
	private static int startBussen(int tijd){
		for (Bus bus : busStart.get(tijd)){
			actieveBussen.add(bus);
		}
		busStart.remove(tijd);
		return (!busStart.isEmpty()) ? Collections.min(busStart.keySet()) : -1;
	}
	
	public static void moveBussen(int nu){
		BusFormatter busFormatter = new BusFormatter(nu);
		Producer     busProducer  = new Producer();

		for (Bus bus : actieveBussen) {
			if (!bus.move()) {
				Bericht        busBericht  = busFormatter.format(bus);
				ArrayList<ETA> lastETAList = new ArrayList<ETA>();
				ETA            lastEta     = null;

				for (ETA eta : busBericht.ETAs)
					lastEta = eta;

				if (lastEta == null)
					throw new RuntimeException("Ran out of ETAs.");

				lastETAList.add(lastEta);

				busBericht.ETAs     = lastETAList;
				busBericht.eindpunt = lastEta.halteNaam;

				busProducer.sendBericht(busBericht);

				actieveBussen.remove(bus);
			}
		}
	}

	public static void sendETAs(int nu){
		BusFormatter busFormatter = new BusFormatter(nu);
		Producer     busProducer  = new Producer();

		for (Bus bus : actieveBussen) {
			busProducer.sendBericht(busFormatter.format(bus));
		}
	}

	public static int initBussen(){
		addBusToCollection(3, Lijnen.LIJN1, Bedrijven.ARRIVA);
		addBusToCollection(5, Lijnen.LIJN2, Bedrijven.ARRIVA);
		addBusToCollection(4, Lijnen.LIJN3, Bedrijven.ARRIVA);
		addBusToCollection(6, Lijnen.LIJN4, Bedrijven.ARRIVA);
		addBusToCollection(3, Lijnen.LIJN5, Bedrijven.FLIXBUS);
		addBusToCollection(5, Lijnen.LIJN6, Bedrijven.QBUZZ);
		addBusToCollection(4, Lijnen.LIJN7, Bedrijven.QBUZZ);
		addBusToCollection(6, Lijnen.LIJN1, Bedrijven.ARRIVA);
		addBusToCollection(12, Lijnen.LIJN4, Bedrijven.ARRIVA);
		addBusToCollection(10, Lijnen.LIJN5, Bedrijven.FLIXBUS);

		return Collections.min(busStart.keySet());
	}

	public static void main(String[] args) throws InterruptedException {
		int tijd=0;
		int counter=0;
		TijdFuncties tijdFuncties = new TijdFuncties();
		tijdFuncties.initSimulatorTijden(interval,syncInterval);
		int volgende = initBussen();
		while ((volgende>=0) || !actieveBussen.isEmpty()) {
			counter=tijdFuncties.getCounter();
			tijd=tijdFuncties.getTijdCounter();
			System.out.println("De tijd is:" + tijdFuncties.getSimulatorWeergaveTijd());
			volgende = (counter==volgende) ? startBussen(counter) : volgende;
			moveBussen(tijd);
			sendETAs(tijd);
			tijdFuncties.simulatorStep();
		}
	}

	private static void addBusToCollection(int unknown, Lijnen lijn, Bedrijven bedrijf) {
		addBus(unknown, new Bus(lijn, bedrijf, 1));
		addBus(unknown, new Bus(lijn, bedrijf, -1));
	}
}
