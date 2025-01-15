package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.Adresa;
import eshop.bo.ciselniky.Tovar;
import eshop.su.ciselnik.md.MDAdresa.CallBack;
import eshop.su.ciselnik.md.MDAdresa.Parameters;
import eshop.su.ciselnik.uc.UCAdresa;
import eshop.su.ciselnik.uc.UCObjednavka;
import eshop.su.ciselnik.uc.UCTovar;
import eshop.su.komponenty.ComponentDodavatel;
import eshop.su.komponenty.ComponentTovarDruh;
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
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDOkCancel;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;

public class MDTovar extends MDOkCancel {

	private UCTovar tovar; 
    private boolean isNew;
	
    @Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
    	if (parameters == null) {
    		tovar = UCTovar.create((EclipseLinkSession) this.getSessionObject());
    		isNew = true;
    	}else if (parameters instanceof Parameters) {
    		tovar = UCTovar.read(((Parameters) parameters).tovarId, (EclipseLinkSession) this.getSessionObject());
    	}else{
    		throw new IllegalArgumentException("Chybny parameter!");
    	}
    	putFormToPack(pack);
    }
    
	@Override
	protected XPanel createPanel(ServerPack paramServerPack) throws Exception {
		
			XDualComponentPanel panel = new XDualComponentPanel();
			panel.setInsets(new Insets(10,10,10,10));
			panel.setBorder(new XTitleBorder());

			panel.add(ComponentBuilder.createComponent(UCTovar.KOD, Tovar.KOD, getSessionObject()));
			panel.add(ComponentBuilder.createComponent(UCTovar.NAZOV, Tovar.NAZOV, getSessionObject()));
			panel.add(ComponentBuilder.createComponent(UCTovar.CENA, Tovar.CENA, getSessionObject()));
			panel.add(ComponentBuilder.createComponent(UCTovar.VELKOST, Tovar.VELKOST, getSessionObject()));
			panel.add(ComponentBuilder.createComponent(UCTovar.SKLADOVA_ZASOBA, Tovar.SKLADOVA_ZASOBA, getSessionObject()));
			/*
			XCompoundField tovarDruhComp = (XCompoundField) ComponentTovarDruh.createComponent(UCTovar.TOVAR_DRUH_KOD, this);
	        ServerFocusEvent focusEvent = new ServerFocusEvent(ServerFocusEvent.FOCUS_LAST_EVENT);
	        focusEvent.addReturnValue(UCTovar.TOVAR_DRUH_KOD);
	        tovarDruhComp.addFocusEvent(focusEvent);
	        panel.add(tovarDruhComp);
			*/
			panel.add(ComponentTovarDruh.createComponent(UCTovar.TOVAR_DRUH_KOD, this));
					
			//TODO - dodavatel?
			XCompoundField dodavatelComp = (XCompoundField) ComponentDodavatel.createComponent(UCTovar.DODAVATEL_KOD, this);
	        ServerFocusEvent focusEvent1 = new ServerFocusEvent(ServerFocusEvent.FOCUS_LAST_EVENT);
	        focusEvent1.addReturnValue(UCTovar.DODAVATEL_KOD);
	        dodavatelComp.addFocusEvent(focusEvent1);
	        panel.add(dodavatelComp);
			
			return panel;
	}

	@Override
	protected String getTitleText() {
		return "Tovary";
	}

	@Override
	protected void execute(ValuePack valuePack, ServerPack pack) throws Exception {
		tovar.execute(valuePack);
		sendCallBack(new CallBack(tovar.getId(), isNew), pack);
		close(pack);
		
	}

	@Override
	protected ValuePack getValuePack() {
		return tovar.getValuePack();
	}

	@Override
	protected EnabledPack getEnabledPack() {
		return tovar.getEnabledPack();
	}

	@Override
	protected RequiredPack getRequiredPack() {
		return tovar.getRequiredPack();
	}
	
	@Override
	public void focusEventExecuted(ClientFocusEvent event, ServerPack pack) {
		if (event.getSourceId().equals(UCTovar.DODAVATEL_KOD)) {
			Object kod = event.getValuePack().getValue(UCTovar.DODAVATEL_KOD);
			try {
				pack.addUpdatedPack(new UpdatedPack(getId(), tovar.setDodavatelKod(kod)));
			} catch (Exception e) {
				addExceptionToPack(e, pack);
			}
		}
	/*	
		if (event.getSourceId().equals(UCTovar.TOVAR_DRUH_KOD)) {
			Object kod = event.getValuePack().getValue(UCTovar.TOVAR_DRUH_KOD);
			try {
				pack.addUpdatedPack(new UpdatedPack(getId(), tovar.setTovarDruhKod(kod)));
			} catch (Exception e) {
				addExceptionToPack(e, pack);
			}
		}
	*/	
	}

	//vytvaram triedu Parameters
		public static class Parameters implements MediatorParameters {
		    
	    	public Object tovarId;
	    
	    	public Parameters(Object tovarId) {
	    		this.tovarId = tovarId;
	    	} 
	    }
		
		//CallBack je objekt, ktory mi nacitava tabulku dynamicky
		public static class CallBack implements MediatorCallBackObject{
	        
			public Object tovarId;
			public boolean isNew;
	    
	    	public CallBack(Object tovarId, boolean isNew) {
	    		this.tovarId = tovarId;
	    		this.isNew = isNew;
	    	} 
		}

}
