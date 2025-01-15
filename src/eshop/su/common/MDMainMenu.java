package eshop.su.common;

import java.util.ArrayList;
import java.util.List;

import eshop.su.ciselnik.md.MDObjednavka;
import eshop.su.ciselnik.md.MDTovar;
import eshop.su.ciselnik.md.MDViewAdresa;
import eshop.su.ciselnik.md.MDViewDodavatelZakaznik;
import eshop.su.ciselnik.md.MDViewFaktura;
import eshop.su.ciselnik.md.MDViewObjednavka;
import eshop.su.ciselnik.md.MDViewTovar;
import eshop.su.ciselnik.md.MDViewTovarDruh;
import eshop.su.common.report.ReportTree;
import netball.server.component.XMenu;
import netframework.mediator.MDFontSelection;
import netframework.mediator.MDMainMediator;
import netframework.mediator.MDReportTree;
import netframework.mediator.resource.MediatorResourceBuilder;
import zelpo.eclipselink.autorizacia.md.MDUzivatelNoveHeslo;
import zelpo.eclipselink.autorizacia.md.MDViewAkcia;
import zelpo.eclipselink.autorizacia.md.MDViewPridelenieUzivatela;
import zelpo.eclipselink.autorizacia.md.MDViewRola;
import zelpo.eclipselink.autorizacia.md.MDViewUzivatel;


public class MDMainMenu extends MDMainMediator {

	@Override
	protected List<XMenu> createMenu() {
		List<XMenu> mainMenu = new ArrayList<XMenu>();
		
		XMenu menu = createMenu("objednavkyAPartneri", "Objednavky a partneri");
		menu.add(createMenuItem("objednavky", "Objednavky", MDViewObjednavka.class));
		menu.add(createMenuItem("obchodniPartneri", "Obchodni partneri", MDViewDodavatelZakaznik.class));
		//menu.add(createMenuItem("obchodniPartneriCTB", "Obchodni partneri CTB", MDViewDodavatelZakaznikCTB.class));
		mainMenu.add(menu);
		
		menu = createMenu("faktury", "Faktury");
		menu.add(createMenuItem("faktury", "Faktury", MDViewFaktura.class));
		mainMenu.add(menu);
		   
		menu = createMenu("ciselniky", "Ciselniky");
		menu.add(createMenuItem("adresy", "Adresy", MDViewAdresa.class));
		menu.add(createMenuItem("druhyTovaru", "Druhy tovaru", MDViewTovarDruh.class));
		menu.add(createMenuItem("tovary", "Tovary", MDViewTovar.class));
		mainMenu.add(menu);
		
		menu = createMenu("prehlady", "Prehlady");
		menu.add(createMenuItem("prehladyItem", "Prehlady", ReportTree.class));
		//dynamicke prehlady?
		//menu.add(createMenuItem("prehladyDynamic", /*~~*/"Dynamicke prehlady", MDSkolenieDynamicReport.class));
		mainMenu.add(menu);
		
		menu = createMenu("autorizacia", "Autorizacia");
		//check MDViewRola.class
		menu.add(createMenuItem("role", "Role", MDViewRola.class));
		//check MDViewPridelenieUzivatela.class
		menu.add(createMenuItem("uzivateliaAplikacie", "Uzivatelia aplikacie", MDViewPridelenieUzivatela.class));
		//check MDViewAkcia.class
		menu.add(createMenuItem("akcie", "Akcie", MDViewAkcia.class));
		mainMenu.add(menu);
		 
		return mainMenu;
	}

	@Override
	protected String getTitleText() {
		return "Eshop hlavne menu";
	}
	
	//reporty = prehlady? 
	/*
	@Override
	protected Class<? extends MDReportTree> getReportTree() {
		return ReportTree.class;
	}
	*/
	
	@Override
	protected XMenu createFavoritMenu(List<XMenu> mainMenu) {
		XMenu menu = super.createFavoritMenu(mainMenu);
		//TODO - este s tymto sa pohrat
		//menu.add(createMenuItem("zmenaFontu", MediatorResourceBuilder.getString(MediatorResourceBuilder.FONT_SELECTION_TITLE, getLocale()), MDFontSelection.class));
		return menu;
	}

}
