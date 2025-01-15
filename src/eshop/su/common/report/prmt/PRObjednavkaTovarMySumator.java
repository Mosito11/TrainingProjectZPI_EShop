package eshop.su.common.report.prmt;

import eshop.bo.ciselniky.Objednavka;
import eshop.su.common.report.ReportObjednavkaMySumator;
import eshop.su.common.report.ReportObjednavkaTovarMySumator;
import eshop.su.komponenty.ComponentZakaznik;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XPanel;
import netball.server.component.border.XTitleBorder;
import netball.server.pack.RequiredPack;
import netball.server.pack.ServerPack;
import netframework.mediator.MDReportPrmts;
import netframework.report.Report;

public class PRObjednavkaTovarMySumator extends MDReportPrmts {

	@Override
    protected XPanel createFilterPanel(ServerPack pack) {        
       XDualComponentPanel panel = new XDualComponentPanel();
       panel.setBorder(new XTitleBorder());
       panel.setInsets(new java.awt.Insets(10,10,10,10));
       panel.add(createExpressionComponent(ReportObjednavkaTovarMySumator.DATUM, Objednavka.DATUM));       
       //panel.add(ComponentZakaznik.createComponent(ReportObjednavkaMySumator.ZAKAZNIK_KOD, this));       
       return panel; 
    }

	@Override
    protected Report createReport() {
        return new ReportObjednavkaTovarMySumator();
    }
    
	@Override
    protected RequiredPack getRequiredPack() {
		RequiredPack pack = new RequiredPack();
        pack.put(ReportObjednavkaTovarMySumator.DATUM, true);
        return pack;
    } 

}
