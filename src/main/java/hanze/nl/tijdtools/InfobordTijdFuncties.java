package hanze.nl.tijdtools;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class InfobordTijdFuncties {

	public Tijd getCentralTime()
    {
    	try {
    		HTTPFuncties httpFuncties = new HTTPFuncties();
			String result = httpFuncties.executeGet("json");
			Tijd tijd = new ObjectMapper().readValue(result, Tijd.class);
	        return tijd;
    	} catch (IOException e) {
			e.printStackTrace();
			return new Tijd(0,0,0);
		}
    }
	
	public String getFormattedTimeFromCounter(int counter){
		int uur = counter/3600;
		int minuten = (counter-3600*uur)/60;
		int seconden = counter - 3600*uur - 60*minuten;
		Tijd tijd=new Tijd(uur,minuten,seconden);
		return tijd.toString();
	}
}
