package eshop.su.common;

import netframework.mediator.MDButton;

public class MDHelper {
	
	public static final String MESSAGE_VYMAZ_ZAZNAM = "SKUTOCNE SI PRAJETE VYMAZAT ZAZNAM?";
	public static final String MESSAGE_DOKONCENA = "SKUTOCNE SI PRAJETE NASTAVIT ZAZNAM NA DOKONCENA?";
	public static final String MESSAGE_STORNO = "SKUTOCNE SI PRAJETE STORNOVAT ZAZNAM?";
	//ViewCTB, je to pouzitelne?
	public static final String MESSAGE_ZAKAZNIK = "SKUTOCNE SI PRAJETE FILTROVAT ZAKAZNIKOV?";
	public static final String MESSAGE_DODAVATEL = "SKUTOCNE SI PRAJETE FILTROVAT DODAVATELOV?";
	
	//co je to mnemonic (char v ramci parametru kreatora)
	public static final MDButton TYP_BUTTON = new MDButton("typ", "Filter typ", 'T', "UMOZNUJE_VYBRAT_TYP_OBCHODNEHO_PARTNERA");
	public static final MDButton DETAIL_BUTTON = new MDButton("detail", "Detail", 'D', "UMOZNUJE_ZOBRAZIT_DETAIL_ZAZNAMU");
	public static final MDButton STAV_BUTTON = new MDButton("stav", "Stav", 'S', "UMOZNUJE_ZMENIT_STAV_OBJEDNAVKY");
	public static final MDButton TLAC_DOKLADU_BUTTON = new MDButton("tlacDokladu", "Tlac dokladu", 'D', "UMOZNUJE_VYTLACIT_DOKLAD");
	
	public static final MDButton FARBA_BUTTON = new MDButton("farba", "Farba", 'F', "UMOZNUJE_NASTAVIT_FARBU");
	
	
	//skusame insert button vec
	public static final MDButton INSERT_BUTTON = new MDButton("pridaj+", "Pridaj", 'P', "VKLADA CO TREBA");
	
	
}
