package eshop.su.common.view;

import eshop.bo.ciselniky.TovarDruh;
import netframework.eclipselink.EclipseLinkView;

public class ViewTovarDruh extends EclipseLinkView {
	
	public static final String ID = TovarDruh.ID.getId();
    public static final String KOD = TovarDruh.KOD.getId();
    public static final String NAZOV = TovarDruh.NAZOV.getId();
    	
	public ViewTovarDruh() {
		super(TovarDruh.class);
		put(ID, TovarDruh.ID);
		put(KOD, TovarDruh.KOD);
		put(NAZOV, TovarDruh.NAZOV);
	}

}
