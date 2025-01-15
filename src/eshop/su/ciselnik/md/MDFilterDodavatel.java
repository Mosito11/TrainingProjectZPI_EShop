package eshop.su.ciselnik.md;

import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.TypObchodnehoPartnera;
import eshop.su.common.view.ViewObchodnyPartner;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XPanel;
import netball.server.pack.ValuePack;
import netframework.mediator.MDViewFilter;
import netframework.view.ViewExpression;
import netframework.view.ViewExpressionBuilder;

public class MDFilterDodavatel extends MDViewFilter {

	@Override
	protected XPanel createFilterPanel() {
		XDualComponentPanel panel = new XDualComponentPanel();
		panel.add(createComponent(ViewObchodnyPartner.KOD, ObchodnyPartner.KOD));
		panel.add(createComponent(ViewObchodnyPartner.NAZOV, ObchodnyPartner.NAZOV));
		//panel.add(createComponent(ViewObchodnyPartner.TYP, ObchodnyPartner.TYP));
		panel.add(createExpressionComponent(ViewObchodnyPartner.ICO, Dodavatel.ICO));
		/*panel.add(ComponentZakaznik.createExpressionComponent(ViewFaktura.ZAKAZNIK_ICO, this));
		panel.add(createExpressionComponent(ViewFaktura.OBDOBIE, Faktura.OBDOBIE));
		panel.add(createComponent(ViewFaktura.STAV, Faktura.STAV));
		panel.add(ComponentMena.createExpressionComponent(ViewFaktura.MENA_KOD, this));
		panel.add(createExpressionComponent(ViewFaktura.CELKOVA_SUMA, SumaVMene.SUMA));
		*/
	    return panel; 
	}

	@Override
	protected ViewExpression createExpression(ValuePack valuePack) {
		ViewExpression exp = super.createExpression(valuePack);
		ViewExpression exp1 = ViewExpressionBuilder.get(ViewObchodnyPartner.TYP).equal(TypObchodnehoPartnera.DODAVATEL.getKey());
		exp = exp != null ? exp.and(exp1) : exp1; 
		return exp;
	}
}
