package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.Kontakt;
import eshop.su.ciselnik.md.MDAdresa.Parameters;
import eshop.su.ciselnik.uc.UCAdresa;
import eshop.su.ciselnik.uc.UCKontakt;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XPanel;
import netball.server.component.border.XTitleBorder;
import netball.server.pack.EnabledPack;
import netball.server.pack.RequiredPack;
import netball.server.pack.ServerPack;
import netball.server.pack.ValuePack;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDOkCancel;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;

public class MDKontakt extends MDOkCancel {
	
	private UCKontakt kontakt; 
		
	@Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {  
		
        if (parameters != null && parameters instanceof Parameters) {
        	kontakt = ((Parameters) parameters).kontakt;
        }else{
        	throw new IllegalArgumentException("Chybny parameter!");
        }
        this.putFormToPack(pack);
    } 
	
	@Override
	protected XPanel createPanel(ServerPack pack) throws Exception {
		XDualComponentPanel panel = new XDualComponentPanel();
        panel.setInsets(new Insets(10,10,10,10));
        panel.setBorder(new XTitleBorder());
        panel.add(ComponentBuilder.createComponent(UCKontakt.MENO, Kontakt.MENO, getSessionObject()));
        panel.add(ComponentBuilder.createComponent(UCKontakt.EMAIL, Kontakt.EMAIL, getSessionObject()));
        panel.add(ComponentBuilder.createComponent(UCKontakt.TELEFON, Kontakt.TELEFON, getSessionObject()));
		return panel;
	}
	
	@Override
	protected String getTitleText() {
		return "Kontakty";
	}

	@Override
	protected void execute(ValuePack valuePack, ServerPack pack) throws Exception {
		kontakt.execute(valuePack); 
		if (sendCallBack(new CallBack(kontakt), pack))
	          close(pack);
	}
	
	@Override
	protected ValuePack getValuePack() {
		return kontakt.getValuePack();
	}

	@Override
	protected EnabledPack getEnabledPack() {
		return kontakt.getEnabledPack();
	}

	@Override
	protected RequiredPack getRequiredPack() {
		return kontakt.getRequiredPack();
	}

	public static class Parameters implements MediatorParameters {
	    
    	public UCKontakt kontakt;
    
    	public Parameters(UCKontakt kontakt) {
    		this.kontakt = kontakt;
    	} 
    }
	
	public static class CallBack implements MediatorCallBackObject{
        
		public UCKontakt kontakt;
    
		public CallBack(UCKontakt kontakt) {
			this.kontakt = kontakt;
		} 
	}
	
}
