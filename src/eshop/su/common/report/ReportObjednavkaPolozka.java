package eshop.su.common.report;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.common.report.prmt.PRObjednavkaPolozka;
import eshop.su.common.view.ViewObjednavkaPolozka;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.MDReportTreeItemPrmts;
import netframework.mediator.ReportTreeItem;
import netframework.mediator.SessionObject;
import netframework.report.UserReport;
import netframework.report.UserReportGroupItem;
import netframework.view.View;

public class ReportObjednavkaPolozka extends UserReport {

	@Override
	public String[] createColumns() {
		return new String[] {
                ViewObjednavkaPolozka.JEDNOTKOVA_CENA,
                ViewObjednavkaPolozka.MNOZSTVO,
                ViewObjednavkaPolozka.OBJEDNAVKA_CISLO,
                ViewObjednavkaPolozka.OBJEDNAVKA_DATUM,
                ViewObjednavkaPolozka.TOVAR_KOD,
                ViewObjednavkaPolozka.TOVAR_NAZOV,
                ViewObjednavkaPolozka.TOVAR_VELKOST,
                }; 
	}
	
	@Override
	public String[] createSumColumns() {
		return new String[] {
                ViewObjednavkaPolozka.SUMA,
				};
    }
	
	@Override
	public String[] createSortableColumns() {
		return new String[] {
                ViewObjednavkaPolozka.JEDNOTKOVA_CENA,
                ViewObjednavkaPolozka.MNOZSTVO,
                ViewObjednavkaPolozka.OBJEDNAVKA_CISLO,
                ViewObjednavkaPolozka.OBJEDNAVKA_DATUM,
                ViewObjednavkaPolozka.TOVAR_KOD,
                ViewObjednavkaPolozka.TOVAR_NAZOV,
                ViewObjednavkaPolozka.TOVAR_VELKOST,
                };
	}
	
	@Override
	public UserReportGroupItem[] createGroupColumns() {
		UserReportGroupItem[] columns = new UserReportGroupItem[3]; 
			    
				//grupovanie podla cisla objednavky
		        columns[0] = new UserReportGroupItem(Objednavka.CISLO_OBJEDNAVKY.getLongCaption(), ViewObjednavkaPolozka.OBJEDNAVKA_CISLO); 
		        columns[0].setHeaderColumns(new String[] {ViewObjednavkaPolozka.OBJEDNAVKA_CISLO, ViewObjednavkaPolozka.OBJEDNAVKA_DATUM});
		        columns[0].setFooterColumns(new String[] {ViewObjednavkaPolozka.OBJEDNAVKA_CISLO});                
			        
		        columns[1] = new UserReportGroupItem(Tovar.KOD.getLongCaption(), ViewObjednavkaPolozka.TOVAR_KOD); 
		        columns[1].setHeaderColumns(new String[] {ViewObjednavkaPolozka.TOVAR_KOD, ViewObjednavkaPolozka.TOVAR_NAZOV});
		        columns[1].setFooterColumns(new String[] {ViewObjednavkaPolozka.TOVAR_KOD});                
		
		        columns[2] = new UserReportGroupItem(Objednavka.DATUM.getLongCaption(), ViewObjednavkaPolozka.OBJEDNAVKA_DATUM); 
		        columns[2].setHeaderColumns(new String[] {ViewObjednavkaPolozka.OBJEDNAVKA_DATUM});
		        columns[2].setFooterColumns(new String[] {ViewObjednavkaPolozka.OBJEDNAVKA_DATUM});
			        
		        return columns;
	}
	
	@Override
	public View createView(SessionObject session) {
		return new ViewObjednavkaPolozka(ObjednavkaPolozka.class);
	}
	
	@Override
	public String getName() {
		return "Polozky";
	}
	
	@Override
	public String getDescription() {
		return "User Report Polozky";
	}

	@Override
	public Class<PRObjednavkaPolozka> getParameterMediatorClass() {
		return PRObjednavkaPolozka.class;
	}

	@Override
	public String getCompanyName() {
		return (String)((EclipseLinkSession) getSessionObject()).getProperty("ESHOP");
	}

	
}
