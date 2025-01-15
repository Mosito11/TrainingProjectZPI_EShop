package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.su.ciselnik.uc.UCObjednavkaPolozka;
import eshop.su.komponenty.ComponentTovar;
import netball.server.component.XCompoundField;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XPanel;
import netball.server.component.border.XTitleBorder;
import netball.server.event.ClientFocusEvent;
import netball.server.event.ServerFocusEvent;
import netball.server.pack.EnabledPack;
import netball.server.pack.RequiredPack;
import netball.server.pack.ServerPack;
import netball.server.pack.UpdatedPack;
import netball.server.pack.ValuePack;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDOkCancel;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;

public class MDObjednavkaPolozka extends MDOkCancel {
	
	private UCObjednavkaPolozka polozka; 

	@Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {  
		
        if (parameters != null && parameters instanceof Parameters) {
        	polozka = ((Parameters) parameters).polozka;
        }else{
        	throw new IllegalArgumentException("Chybny parameter!");
        }
        this.putFormToPack(pack);
    }
	/*
	@Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
    	
    	if (parameters == null) {
    		polozka = UCObjednavkaPolozka.create((EclipseLinkSession) this.getSessionObject());
    		isNew = true;
    	}else if (parameters instanceof Parameters) {
    		polozka = UCObjednavkaPolozka.read(((Parameters) parameters).polozkaId, (EclipseLinkSession) this.getSessionObject());
    	}else{
    		throw new IllegalArgumentException("Chybny parameter!");
    	}
    	putFormToPack(pack);
    }
	*/
	//plus tovar + objednavka?
	@Override
	protected XPanel createPanel(ServerPack pack) throws Exception {
		XDualComponentPanel panel = new XDualComponentPanel();
        panel.setInsets(new Insets(10,10,10,10));
        panel.setBorder(new XTitleBorder());
        //panel.add(ComponentBuilder.createComponent(UCObjednavkaPolozka.JEDNOTKOVA_CENA, ObjednavkaPolozka.JEDNOTKOVA_CENA, getSessionObject()));
        
        //toto je kod, ktorym zabezpecim automaticke nacitanie atributov do poli formulara (zadam kod a ostatne sa dorobi) 
        //potrebujem k tomu 2 metody: focusEventExecuted v MDObjednavkaPolozka a setTovarKod v UCObjednavkaPolozka
        XCompoundField tovarComp = (XCompoundField) ComponentTovar.createComponent(UCObjednavkaPolozka.TOVAR_KOD, this);
        ServerFocusEvent focusEvent = new ServerFocusEvent(ServerFocusEvent.FOCUS_LAST_EVENT);
        focusEvent.addReturnValue(UCObjednavkaPolozka.TOVAR_KOD);
        tovarComp.addFocusEvent(focusEvent);
        panel.add(tovarComp);
        
        //ReadOnlyComponent setri pamat v zasade
        panel.add(ComponentBuilder.createReadOnlyComponent(UCObjednavkaPolozka.TOVAR_NAZOV, Tovar.NAZOV, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(UCObjednavkaPolozka.JEDNOTKOVA_CENA, Tovar.CENA, getSessionObject()));
        panel.add(ComponentBuilder.createComponent(UCObjednavkaPolozka.MNOZSTVO, ObjednavkaPolozka.MNOZSTVO, getSessionObject()));
        
        //panel.add(ComponentBuilder.createComponent(UCObjednavkaPolozka.TOVAR_NAZOV, ObjednavkaPolozka.TOVAR, getSessionObject()));
		return panel;
	}

	@Override
	protected void execute(ValuePack valuePack, ServerPack pack) throws Exception {
		polozka.execute(valuePack); 
		if (sendCallBack(new CallBack(polozka), pack))
	          close(pack);
	}
	
	@Override
	public void focusEventExecuted(ClientFocusEvent event, ServerPack pack) {
		if (event.getSourceId().equals(UCObjednavkaPolozka.TOVAR_KOD)) {
			Object kod = event.getValuePack().getValue(UCObjednavkaPolozka.TOVAR_KOD);
			try {
				pack.addUpdatedPack(new UpdatedPack(getId(), polozka.setTovarKod(kod)));
			} catch (Exception e) {
				addExceptionToPack(e, pack);
			}
		}
	}
	
	@Override
	protected EnabledPack getEnabledPack() {
		return polozka.getEnabledPack();
	}

	@Override
	protected RequiredPack getRequiredPack() {
		return polozka.getRequiredPack();
	}

	@Override
	protected String getTitleText() {
		return "Polozky";
	}

	@Override
	protected ValuePack getValuePack() {
		return polozka.getValuePack();
	}

	public static class Parameters implements MediatorParameters {
	    
    	public UCObjednavkaPolozka polozka;
    
    	public Parameters(UCObjednavkaPolozka polozka) {
    		this.polozka = polozka;
    	} 
    }
	
	public static class CallBack implements MediatorCallBackObject{
        
		public UCObjednavkaPolozka polozka;
    
		public CallBack(UCObjednavkaPolozka polozka) {
			this.polozka = polozka;
		} 
	}
	

}
