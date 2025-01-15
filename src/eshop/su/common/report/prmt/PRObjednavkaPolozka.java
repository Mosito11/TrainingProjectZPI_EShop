package eshop.su.common.report.prmt;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.su.common.report.ReportObjednavkaPolozka;
import eshop.su.common.view.ViewObjednavkaPolozka;
import eshop.su.komponenty.ComponentTovar;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XPanel;
import netframework.mediator.MDUserReportPrmts;
import netframework.report.UserReport;

public class PRObjednavkaPolozka extends MDUserReportPrmts {

	@Override
	   protected XPanel createFilterPanel() {
			XDualComponentPanel panel = new XDualComponentPanel();
			//panel.add(createComponent(ViewObjednavkaPolozka.OBJEDNAVKA_CISLO, Objednavka.CISLO_OBJEDNAVKY));
			panel.add(createExpressionComponent(ViewObjednavkaPolozka.OBJEDNAVKA_DATUM, Objednavka.DATUM));
			panel.add(ComponentTovar.createComponent(ViewObjednavkaPolozka.TOVAR_KOD, this));
			//panel.add(ComponentTovar.createComponent(ViewObjednavkaPolozka.TOVAR_NAZOV, this));
			panel.add(createExpressionComponent(ViewObjednavkaPolozka.OBJEDNAVKA_STAV, Objednavka.STAV_OBJEDNAVKY));
			panel.add(createExpressionComponent(ViewObjednavkaPolozka.JEDNOTKOVA_CENA, ObjednavkaPolozka.JEDNOTKOVA_CENA));
			panel.add(createExpressionComponent(ViewObjednavkaPolozka.MNOZSTVO, ObjednavkaPolozka.MNOZSTVO));
			panel.add(createExpressionComponent(ViewObjednavkaPolozka.SUMA, ObjednavkaPolozka.SUMA));
			return panel; 
	    }

	    @Override
	    protected UserReport createUserReport() {
	        return new ReportObjednavkaPolozka();
	    }
}
