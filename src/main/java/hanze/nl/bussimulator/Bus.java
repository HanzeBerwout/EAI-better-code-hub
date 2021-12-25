package hanze.nl.bussimulator;

import com.thoughtworks.xstream.XStream;
import hanze.nl.bussimulator.Halte.Positie;

import java.util.ArrayList;

public class Bus{
	private Bedrijven bedrijf;
	private Lijnen lijn;
	private int halteNummer;
	private int totVolgendeHalte;
	private int richting;
	private boolean bijHalte;
	private String busID;
	
	Bus(Lijnen lijn, Bedrijven bedrijf, int richting){
		this.lijn=lijn;
		this.bedrijf=bedrijf;
		this.richting=richting;
		this.halteNummer = -1;
		this.totVolgendeHalte = 0;
		this.bijHalte = false;
		this.busID = "Niet gestart";
	}

	public Lijnen getLijn() {
		return this.lijn;
	}

	public Bedrijven getBedrijf() {
		return this.bedrijf;
	}

	public String getBusID() {
		return this.busID;
	}
	
	public void setbusID(int starttijd){
		this.busID=starttijd+lijn.name()+richting;
	}
	
	public void naarVolgendeHalte(){
		Positie volgendeHalte = lijn.getHalte(halteNummer+richting).getPositie();
		totVolgendeHalte = lijn.getHalte(halteNummer).afstand(volgendeHalte);
	}
	
	public boolean halteBereikt(){
		halteNummer+=richting;
		bijHalte=true;
		if ((halteNummer>=lijn.getLengte()-1) || (halteNummer == 0)) {
			System.out.printf("Bus %s heeft eindpunt (halte %s, richting %d) bereikt.%n", 
					lijn.name(), lijn.getHalte(halteNummer), lijn.getRichting(halteNummer));
			return true;
		}
		else {
			System.out.printf("Bus %s heeft halte %s, richting %d bereikt.%n", 
					lijn.name(), lijn.getHalte(halteNummer), lijn.getRichting(halteNummer));		
			naarVolgendeHalte();
		}		
		return false;
	}
	
	public void start() {
		halteNummer = (richting==1) ? 0 : lijn.getLengte()-1;
		System.out.printf("Bus %s is vertrokken van halte %s in richting %d.%n", 
				lijn.name(), lijn.getHalte(halteNummer), lijn.getRichting(halteNummer));		
		naarVolgendeHalte();
	}

	public boolean move(){
		boolean eindpuntBereikt = false;
		bijHalte=false;
		if (halteNummer == -1) {
			start();
		}
		else {
			totVolgendeHalte--;
			if (totVolgendeHalte==0){
				eindpuntBereikt=halteBereikt();
			}
		}
		return eindpuntBereikt;
	}

	// This function does not belong here.
	// Due to time reasons this was not resolved.
	public ArrayList<ETA> getETAs(int tijd) {
		ArrayList<ETA> etaArrayList = new ArrayList<ETA>();

		if (this.bijHalte)
			etaArrayList.add(new ETA(
					this.lijn.getHalte(this.halteNummer).name(),
					this.lijn.getRichting(this.halteNummer),
					0
			));

		Positie eerstVolgende=lijn.getHalte(halteNummer+richting).getPositie();
		int tijdNaarHalte=totVolgendeHalte+tijd;
		for (int i = halteNummer+richting ; !(i>=lijn.getLengte()) && !(i < 0); i=i+richting ){
			tijdNaarHalte += lijn.getHalte(i).afstand(eerstVolgende);
			etaArrayList.add(new ETA(
					lijn.getHalte(i).name(),
					lijn.getRichting(i),
					tijdNaarHalte
			));

			eerstVolgende=lijn.getHalte(i).getPositie();
		}

		return etaArrayList;
	}
}
