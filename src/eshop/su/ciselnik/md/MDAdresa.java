package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.Adresa;
import eshop.su.ciselnik.uc.UCAdresa;
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

public class MDAdresa extends MDOkCancel {
	
	private UCAdresa adresa; 
    private boolean isNew;

    //zacinam prepisanim metody init, plus potrebujem doplnit triedu Parameters na koniec init je public, ostatne su protected
    @Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
    	
    	if (parameters == null) {
    		adresa = UCAdresa.create((EclipseLinkSession) this.getSessionObject());
    		isNew = true;
    	}else if (parameters instanceof Parameters) {
    		adresa = UCAdresa.read(((Parameters) parameters).adresaId, (EclipseLinkSession) this.getSessionObject());
    	}else{
    		throw new IllegalArgumentException("Chybny parameter!");
    	}
    	putFormToPack(pack);
    }
    
    //prepisujem panel, vyberam si z componentov a pridavam polozky do neho    
	@Override
	protected XPanel createPanel(ServerPack pack) throws Exception {
		
		XDualComponentPanel panel = new XDualComponentPanel();
		panel.setInsets(new Insets(10,10,10,10));
		panel.setBorder(new XTitleBorder());

		panel.add(ComponentBuilder.createComponent(UCAdresa.ULICA, Adresa.ULICA, getSessionObject()));
		panel.add(ComponentBuilder.createComponent(UCAdresa.CISLO, Adresa.CISLO, getSessionObject()));
		panel.add(ComponentBuilder.createComponent(UCAdresa.MESTO, Adresa.MESTO, getSessionObject()));
		panel.add(ComponentBuilder.createComponent(UCAdresa.PSC, Adresa.PSC, getSessionObject()));
		return panel;
	}

	@Override
	protected String getTitleText() {
		return "Adresy";
	}
	
	//prepisujem metodu execute
	@Override
	protected void execute(ValuePack valuePack, ServerPack pack) throws Exception {
		adresa.execute(valuePack);
		sendCallBack(new CallBack(adresa.getId(), isNew), pack);
		close(pack);
	}

	@Override
	protected ValuePack getValuePack() {
		return adresa.getValuePack();
	}

	@Override
	protected EnabledPack getEnabledPack() {
		return adresa.getEnabledPack();
	}

	@Override
	protected RequiredPack getRequiredPack() {
		return adresa.getRequiredPack();
	}
	
	//vytvaram triedu Parameters
	public static class Parameters implements MediatorParameters {
	    
    	public Object adresaId;
    
    	public Parameters(Object adresaId) {
    		this.adresaId = adresaId;
    	} 
    }
	
	//CallBack je objekt, ktory mi nacitava tabulku dynamicky
	public static class CallBack implements MediatorCallBackObject{
        
		public Object adresaId;
		public boolean isNew;
    
    	public CallBack(Object adresaId, boolean isNew) {
    		this.adresaId = adresaId;
    		this.isNew = isNew;
    	} 
	}
}
