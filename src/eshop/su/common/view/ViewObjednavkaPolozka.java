package eshop.su.common.view;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import netframework.bo.attributes.Attribute;
import netframework.eclipselink.EclipseLinkView;

public class ViewObjednavkaPolozka extends EclipseLinkView {
	
	public static final String ID = ObjednavkaPolozka.ID.getId(); 	
	public static final String JEDNOTKOVA_CENA = ObjednavkaPolozka.JEDNOTKOVA_CENA.getId();
	public static final String MNOZSTVO = ObjednavkaPolozka.MNOZSTVO.getId();
	public static final String SUMA = ObjednavkaPolozka.SUMA.getId();

	//chceme pravdepodobne zobrazovat vybrane atributy objednavky
	public static final String OBJEDNAVKA_DATUM = Objednavka.DATUM.getId();
	public static final String OBJEDNAVKA_STAV = Objednavka.STAV_OBJEDNAVKY.getId();
	public static final String OBJEDNAVKA_CISLO = Objednavka.CISLO_OBJEDNAVKY.getId();
	
	//chceme pravdepodobne zobrazovat vybrane atributy tovaru
	public static final String TOVAR_KOD = Tovar.KOD.getId(); 
	public static final String TOVAR_NAZOV = Tovar.NAZOV.getId();
	public static final String TOVAR_VELKOST = Tovar.VELKOST.getId();
	
	public ViewObjednavkaPolozka(Class<?> referenceClass) {
		super(ObjednavkaPolozka.class);
		put(ID, ObjednavkaPolozka.ID); 	
		put(JEDNOTKOVA_CENA, ObjednavkaPolozka.JEDNOTKOVA_CENA);
		put(MNOZSTVO, ObjednavkaPolozka.MNOZSTVO);
		put(SUMA, ObjednavkaPolozka.SUMA);
		
		put(OBJEDNAVKA_DATUM, new Attribute[] {ObjednavkaPolozka.OBJEDNAVKA, Objednavka.DATUM});
		put(OBJEDNAVKA_STAV, new Attribute[] {ObjednavkaPolozka.OBJEDNAVKA, Objednavka.STAV_OBJEDNAVKY});
		put(OBJEDNAVKA_CISLO, new Attribute[] {ObjednavkaPolozka.OBJEDNAVKA, Objednavka.CISLO_OBJEDNAVKY});
		
		put(TOVAR_KOD, new Attribute[] {ObjednavkaPolozka.TOVAR, Tovar.KOD});
		put(TOVAR_NAZOV, new Attribute[] {ObjednavkaPolozka.TOVAR, Tovar.NAZOV});
		put(TOVAR_VELKOST, new Attribute[] {ObjednavkaPolozka.TOVAR, Tovar.VELKOST}); 
	}

}
