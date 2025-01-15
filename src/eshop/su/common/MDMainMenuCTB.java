package eshop.su.common;

import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import eshop.su.ciselnik.md.MDViewDodavatelZakaznik;
import eshop.su.ciselnik.md.MDViewObjednavka;
import eshop.su.ciselnik.md.ctb.MDViewDodavatelZakaznikCTB;
import eshop.su.ciselnik.md.ctb.MDViewObjednavkaCTB;
import eshop.su.common.report.ReportTree;
import netball.server.component.XComponent;
import netball.server.component.XIcon;
import netball.server.component.XLabel;
import netball.server.component.XMenu;
import netball.server.component.XVerticalMenuBar;
import netball.server.pack.ServerPack;
import netball.server.utilities.Utilities;
import netframework.mediator.MDReportTree;
import netframework.mediator.MDUtilities;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ctb.MDMainMediatorCTB;
import zelpo.eclipselink.autorizacia.md.MDViewAkciaCTB;
import zelpo.eclipselink.autorizacia.md.MDViewPridelenieUzivatelaCTB;
import zelpo.eclipselink.autorizacia.md.MDViewRolaCTB;
import zelpo.eclipselink.autorizacia.md.MDViewUzivatelCTB;

public class MDMainMenuCTB extends MDMainMediatorCTB {

	private Insets margin;
	
	@Override
	public void init(MediatorParameters parameters, ServerPack serverPack) throws Exception {
		margin = new Insets(5, 3, 5, 5);;
		super.init(parameters, serverPack);
	}
	
	protected XVerticalMenuBar createVerticalMenuBar() {
		XVerticalMenuBar menu = new XVerticalMenuBar(VERTICAL_MENU_BAR);
		
		XLabel label = new XLabel();
		label.setId("logoLabel");
		label.setBackground(new Color(0, 85, 109));
		label.setWidth(110);
		label.setForeground(Color.WHITE);
   	    byte[] icon = Utilities.loadIcon("src/images/logo.png"); 
   	 //C:\Users\dvorsky\Documents\logo_lego.png
   	    label.setIcon(new XIcon(icon));
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		
		menu.setBottomComponent(label);
		return menu; 
	}
	
	@Override
	protected List<XMenu> createMenu() {
		java.util.List<XMenu> mainMenu = new ArrayList<XMenu>();
	   	   
		   XMenu menu = createMenu("menu", "Menu");
		   menu.setIcon(MDUtilities.loadIcon(MDUtilities.DETAIL_ICON, getSessionObject()));
		   menu.add(createMenuItem("obchodniPartneri", "Obchodni partneri", MDViewDodavatelZakaznikCTB.class));
		   menu.add(createMenuItem("objednavky", "Objednavky", MDViewObjednavkaCTB.class));
		   //menu.add(createMenuItem("odberatelskeFakturyClientServer", ResourceConstants.ODBERATELSKE_FAKTURY, MDViewOdberatelskaFakturaClientServerTableCTB.class));
		   //menu.add(createMenuItem("MDViewTreeTableUzolCTB", "MDViewTreeTableUzolCTB", MDViewTreeTableUzolCTB.class));
		   //menu.add(createMenuItem("MDViewTreeUzolCTB", "MDViewTreeUzolCTB", MDViewTreeUzolCTB.class));
		   //menu.add(createMenuItem("pridelenieUzivatela", "Pridelenie uzivatela", MDViewPridelenieUzivatelaCTB.class));
		   //menu.add(createMenuItem("report", "Reporty", ReportTreeCTB.class));
		   //menu.add(createMenuItem("role", "Role", MDViewRolaCTB.class));
		   menu.add(createMenuItem("prehladyItem", "Prehlady", ReportTree.class));
		   menu.add(createMenuItem("uzivatelia", "Uzivatelia", MDViewUzivatelCTB.class));
		   menu.add(createMenuItem("akcie", "Akcie", MDViewAkciaCTB.class));
		   
		   mainMenu.add(menu);
		   // ak chceme viac vaty 
		   if (getClientSystemProperties() != null) {
			   for (int i = 0; i < mainMenu.size(); i++) {
				   mainMenu.get(i).setMargin(margin);
			   }
		   }
	   	   return mainMenu;   
	}
	
	@Override
	protected XMenu createFavoritMenu(List<XMenu> mainMenu) {
	   XMenu menu = super.createFavoritMenu(mainMenu);
	   menu.setIcon(MDUtilities.loadIcon(MDUtilities.PRINT_ICON, getSessionObject()));
	   if (menu != null && getClientSystemProperties() != null) {
		   menu.setMargin(margin);
	   }
	   return menu;
	}

	@Override
	protected String getTitleText() {
		return "Hlavne menu CTB";
	}
	
	@Override
	protected Class<? extends MDReportTree> getReportTree() {
		return ReportTree.class;
	}

	@Override
	protected void adjustComponent(XComponent component) {
	}
	
	@Override
	protected boolean installHorizontalMenuBar() {
		return false; 
	}

}
