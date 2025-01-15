package eshop.su.common.report;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.common.report.prmt.PRObjednavka;
import eshop.su.common.view.ViewObjednavka;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.MDReportTreeItemPrmts;
import netframework.mediator.SessionObject;
import netframework.report.UserReport;
import netframework.report.UserReportGroupItem;
import netframework.view.View;

public class ReportObjednavka extends UserReport {

	@Override
	public String[] createColumns() {
		return new String[] {
                ViewObjednavka.CISLO_OBJEDNAVKY,
                ViewObjednavka.DATUM,
                ViewObjednavka.ZAKAZNIK_KOD,
                ViewObjednavka.ZAKAZNIK_NAZOV,
                ViewObjednavka.STAV_OBJEDNAVKY,
                //toto tu teoreticky nemusim mat?
                ViewObjednavka.POVODNA_OBJEDNAVKA_CISLO,
                };
	}
	
	//sem davam sumu
	@Override
	public String[] createSumColumns() {
		return new String[] {ViewObjednavka.SUMA};
	}
	
	//v podstate to iste ako create columns - predpokladam, ze nemozem mat nic navyse tam
	@Override
	public String[] createSortableColumns() {
		return new String[] {
                ViewObjednavka.CISLO_OBJEDNAVKY,
                ViewObjednavka.DATUM,
                ViewObjednavka.ZAKAZNIK_KOD,
                ViewObjednavka.ZAKAZNIK_NAZOV,
                ViewObjednavka.STAV_OBJEDNAVKY,
                //toto tu teoreticky nemusim mat?
                ViewObjednavka.POVODNA_OBJEDNAVKA_CISLO,
                };
	}
	
	//toto je podstatna funkcia, tu si nastavujem, co bude v groupingu
	@Override
	public UserReportGroupItem[] createGroupColumns() {
		UserReportGroupItem[] columns = new UserReportGroupItem[3]; 
        
		//grupovanie podla zakaznika
        columns[0] = new UserReportGroupItem(Zakaznik.KOD.getLongCaption(), ViewObjednavka.ZAKAZNIK_KOD); 
        columns[0].setHeaderColumns(new String[] {ViewObjednavka.ZAKAZNIK_KOD, ViewObjednavka.ZAKAZNIK_NAZOV});
        columns[0].setFooterColumns(new String[] {ViewObjednavka.ZAKAZNIK_KOD});                
        
        //TODO - pohrat sa s tymto
        /*
        columns[0] = new UserReportGroupItem(Zakaznik.ICO.getLongCaption(), ViewFaktura.ZAKAZNIK_ICO); 
        columns[0].setGroupingColumns(new String[] {ViewFaktura.ZAKAZNIK_NAZOV, ViewFaktura.ZAKAZNIK_ICO});
        columns[0].setHeaderColumns(new String[] {ViewFaktura.ZAKAZNIK_ICO, ViewFaktura.ZAKAZNIK_NAZOV});
        columns[0].setFooterColumns(new String[] {ViewFaktura.ZAKAZNIK_ICO});
        Vysledok: ORDERING Zakaznik.nazov,Zakaznik.ico,Zakaznik.id,Faktura.cislo,Faktura.obdobie
        
        columns[0] = new UserReportGroupItem(Zakaznik.ICO.getLongCaption(), ViewFaktura.ZAKAZNIK_ID); 
        columns[0].setGroupingColumns(new String[] {ViewFaktura.ZAKAZNIK_ICO, ViewFaktura.ZAKAZNIK_NAZOV});
        columns[0].setHeaderColumns(new String[] {ViewFaktura.ZAKAZNIK_ICO, ViewFaktura.ZAKAZNIK_NAZOV});
        columns[0].setFooterColumns(new String[] {ViewFaktura.ZAKAZNIK_ICO});    
        Vysledok: ORDERING Zakaznik.ico,Zakaznik.nazov,Zakaznik.id,Faktura.cislo,Faktura.obdobie
        
        columns[0] = new UserReportGroupItem(Zakaznik.ICO.getLongCaption(), ViewFaktura.ZAKAZNIK_ICO); 
        columns[0].setGroupingColumns(new String[] {ViewFaktura.ZAKAZNIK_ID, ViewFaktura.ZAKAZNIK_NAZOV});
        columns[0].setHeaderColumns(new String[] {ViewFaktura.ZAKAZNIK_ICO, ViewFaktura.ZAKAZNIK_NAZOV});
        columns[0].setFooterColumns(new String[] {ViewFaktura.ZAKAZNIK_ICO});
        Vysledok: ORDERING Zakaznik.id,Zakaznik.nazov,Zakaznik.ico,Faktura.cislo,Faktura.obdobie
        */
        
        //grupovanie podla stavu objednavky, otazka, ci to ma vyznam
        columns[1] = new UserReportGroupItem(Objednavka.STAV_OBJEDNAVKY.getLongCaption(), ViewObjednavka.STAV_OBJEDNAVKY); 
        columns[1].setHeaderColumns(new String[] {ViewObjednavka.STAV_OBJEDNAVKY});
        columns[1].setFooterColumns(new String[] {ViewObjednavka.STAV_OBJEDNAVKY});                

        //grupovanie podla datumu
        columns[2] = new UserReportGroupItem(Objednavka.DATUM.getLongCaption(), ViewObjednavka.DATUM); 
        columns[2].setHeaderColumns(new String[] {ViewObjednavka.DATUM});
        columns[2].setFooterColumns(new String[] {ViewObjednavka.DATUM});
        
        return columns;
	}
	
	@Override
	public View createView(SessionObject session) {
		return new ViewObjednavka();
	}
	
	@Override
	public String getName() {
		return "Objednavky";
	}

	@Override
	public String getDescription() {
		return "User Report Objednavky";
	}

	@Override
	public Class<PRObjednavka> getParameterMediatorClass() {
		return PRObjednavka.class;
	}

	@Override
	public String getCompanyName() {
		//return (String)((EclipseLinkSession) getSessionObject()).getProperty("companyName");
		return "ESHOP";
	}

}
