package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.Dodavatel;
import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.su.ciselnik.uc.UCDodavatel;
import eshop.su.ciselnik.uc.UCObchodnyPartner;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XPanel;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.ComponentBuilder;

public class MDDodavatel extends MDObchodnyPartner {

	@Override
	protected UCObchodnyPartner create() {
		return UCDodavatel.create((EclipseLinkSession) getSessionObject());
	}

	@Override
	protected UCObchodnyPartner read(Object id) throws Exception {
		return UCDodavatel.read(id, (EclipseLinkSession) getSessionObject());
	}

	@Override
	protected String getTitleText() {
		return "Dodavatel - MDDodavatel";
	}
	
	protected XPanel createAtributyPanel() {
        XDualComponentPanel panel = (XDualComponentPanel) super.createAtributyPanel(); 
        panel.add(ComponentBuilder.createComponent(UCDodavatel.ICO, Dodavatel.ICO, getSessionObject()));
        return panel;
    }
	

}
