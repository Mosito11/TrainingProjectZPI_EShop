package eshop.su.common.view;

import eshop.bo.ciselniky.Kontakt;
import netframework.bo.attributes.Attribute;
import netframework.eclipselink.EclipseLinkView;

public class ViewKontakt extends EclipseLinkView {
	
	public static final String ID = Kontakt.ID.getId();
	public static final String MENO = Kontakt.MENO.getId();
	public static final String EMAIL = Kontakt.EMAIL.getId();
	public static final String TELEFON = Kontakt.TELEFON.getId();
	
	public ViewKontakt() {
		super(Kontakt.class);
		put(ID, Kontakt.ID);
		put(MENO, Kontakt.MENO);
		put(EMAIL, Kontakt.EMAIL);
		put(TELEFON, Kontakt.TELEFON);
	}

}
