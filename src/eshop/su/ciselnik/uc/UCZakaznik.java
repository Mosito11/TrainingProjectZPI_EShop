package eshop.su.ciselnik.uc;

import eshop.bo.ciselniky.Zakaznik;
import netframework.eclipselink.EclipseLinkSession;

public class UCZakaznik extends UCObchodnyPartner {
	
	protected UCZakaznik(Zakaznik zakaznik, EclipseLinkSession session) {
		super(zakaznik, session);		
	}
	
    public static UCZakaznik create(EclipseLinkSession session) {
        return new UCZakaznik(new Zakaznik(), session);
    }

    public static UCZakaznik read(Object id, EclipseLinkSession session) throws Exception {
    	Zakaznik zakaznik = (Zakaznik) read(Zakaznik.class, id, session);
        return new UCZakaznik(zakaznik, session);
    }   

}
