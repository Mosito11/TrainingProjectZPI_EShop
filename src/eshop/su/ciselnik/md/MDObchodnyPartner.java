package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.su.ciselnik.uc.UCDodavatel;
import eshop.su.ciselnik.uc.UCKontakt;
import eshop.su.ciselnik.uc.UCObchodnyPartner;
import eshop.su.ciselnik.uc.UCObchodnyPartnerAdresa;
import eshop.su.ciselnik.uc.UCZakaznik;
import netball.server.component.XBorderPanel;
import netball.server.component.XBoxPanel;
import netball.server.component.XClientTable;
import netball.server.component.XCompoundField;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XPanel;
import netball.server.component.XTabbedPage;
import netball.server.component.XTabbedPane;
import netball.server.component.setting.ClientTableSettings;
import netball.server.event.ClientActionEvent;
import netball.server.event.ClientEvent;
import netball.server.event.ServerActionEvent;
import netball.server.event.ServerFocusEvent;
import netball.server.event.ServerMouseEvent;
import netball.server.event.alert.YesNoAlert;
import netball.server.pack.EnabledPack;
import netball.server.pack.RequiredPack;
import netball.server.pack.ServerPack;
import netball.server.pack.UpdatedPack;
import netball.server.pack.ValuePack;
import netframework.FrameworkUtilities;
import netframework.access.AccessAction;
import netframework.mediator.BasicMediator;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDOkCancel;
import netframework.mediator.MDUtilities;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.resource.MediatorResourceBuilder;

public abstract class MDObchodnyPartner extends MDOkCancel {
	
	private final String PRIDAJ_ACTION = createId(MediatorResourceBuilder.ADD_BUTTON);
    private final String OPRAVA_ACTION = createId(MediatorResourceBuilder.CORRECT_BUTTON);
    private final String VYMAZ_ACTION = createId(MediatorResourceBuilder.DELETE_BUTTON);
    private final String POTVRD_ACTION = createId(MediatorResourceBuilder.OK_BUTTON);
    
    //buttony na pracu s adresami
    private String VYMAZ_ADRESA_ACTION = createId(MediatorResourceBuilder.DELETE_BUTTON + "adresa");
    private String PRIDAJ_ADRESA_ACTION = createId(MediatorResourceBuilder.ADD_BUTTON + "adresa");
    
    private UCObchodnyPartner obchodnyPartner;
    private boolean isNew;
    private Object vyberAdresaId;
    
    @Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
    	if (parameters == null) {
    		obchodnyPartner = create();
    	}else if (parameters instanceof Parameters) {
    		obchodnyPartner = read(((Parameters) parameters).id);
        }else{
        	throw new IllegalArgumentException("Chybny parameter!");
        }
        this.putFormToPack(pack);	
    }
    
    protected abstract UCObchodnyPartner create();
    
    protected abstract UCObchodnyPartner read(Object id) throws Exception ;
       
	@Override
	public AccessAction[] getAccessActions() {
		return null;
	}

	protected XPanel createAtributyPanel() {
        XDualComponentPanel panel = new XDualComponentPanel();
        panel.setInsets(new Insets(10,10,10,10));
        //formular Obchodnych Partnerov
        panel.add(ComponentBuilder.createComponent(UCObchodnyPartner.KOD, ObchodnyPartner.KOD, getSessionObject()));
        panel.add(ComponentBuilder.createComponent(UCObchodnyPartner.NAZOV, ObchodnyPartner.NAZOV, getSessionObject()));
        //panel.add(ComponentBuilder.createComponent(UCObchodnyPartner.TYP, ObchodnyPartner.TYP, getSessionObject()));
        
        //toto asi nepotrebujem zatial
        /*
        XCompoundField fieldZakaznik = ComponentZakaznik.createComponent(UCOdberatelskaFaktura.ZAKAZNIK_ICO, this);
        ServerFocusEvent event = new ServerFocusEvent(ServerFocusEvent.FOCUS_LAST_EVENT);
        event.addReturnValue(UCOdberatelskaFaktura.ZAKAZNIK_ICO);
        fieldZakaznik.addFocusEvent(event);
        panel.add(fieldZakaznik);
        
        panel.add(ComponentBuilder.createReadOnlyComponent(UCOdberatelskaFaktura.ZAKAZNIK_NAZOV, Zakaznik.NAZOV, getSessionObject()));
        */
        return panel;
    }
	
	protected XPanel createKontaktyPanel() {
        XBoxPanel buttonPanel = new XBoxPanel(javax.swing.SwingConstants.HORIZONTAL);
        buttonPanel.setSameSizeForAllComponents(true);
        buttonPanel.setGapForAll(5);
        buttonPanel.add(MediatorResourceBuilder.createButton(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON, new ServerActionEvent(), MDUtilities.ADD_ICON, getSessionObject()));

        ServerActionEvent event = new ServerActionEvent();
        event.addReturnValue(UCObchodnyPartner.KONTAKTY);
        buttonPanel.add(MediatorResourceBuilder.createButton(OPRAVA_ACTION, MediatorResourceBuilder.CORRECT_BUTTON, event, MDUtilities.CORRECT_ICON, getSessionObject()));                
        
        event = new ServerActionEvent();
        event.addReturnValue(UCObchodnyPartner.KONTAKTY);
        event.addAlert(new YesNoAlert(getSessionObject().translateText(eshop.su.common.MDHelper.MESSAGE_VYMAZ_ZAZNAM)));
        buttonPanel.add(MediatorResourceBuilder.createButton(VYMAZ_ACTION, MediatorResourceBuilder.DELETE_BUTTON, event, MDUtilities.DELETE_ICON, getSessionObject()));

        XClientTable table = UCKontakt.createTableProperty(UCObchodnyPartner.KONTAKTY, getSessionObject());
        /*zatial neriesim MouseEventy
        ServerMouseEvent mouseEvent = new ServerMouseEvent(ServerMouseEvent.MOUSE_CLICKED_EVENT);
        mouseEvent.setDoubleClick(true);
        mouseEvent.addReturnValue(UCObchodnyPartner.KONTAKTY);
        table.addMouseEvent(mouseEvent);
        */
        table.setHeight(300);
        table.setWidth(600);
        
        XBorderPanel contentPanel = new XBorderPanel(10, 10);
        contentPanel.setInsets(new Insets(10,10,10,10)); 
        contentPanel.setCenter(table);
        contentPanel.setSouth(buttonPanel);     
        return contentPanel;
    }
	
	//tabulka na pridavanie adries
	protected XPanel createAdresyPanel() {
        XBoxPanel buttonPanel = new XBoxPanel(javax.swing.SwingConstants.HORIZONTAL);
        buttonPanel.setSameSizeForAllComponents(true);
        buttonPanel.setGapForAll(5);
        buttonPanel.add(MediatorResourceBuilder.createButton(PRIDAJ_ADRESA_ACTION, MediatorResourceBuilder.ADD_BUTTON, MDUtilities.ADD_ICON, getSessionObject()));

        ServerActionEvent event = new ServerActionEvent();
        event.addReturnValue(UCObchodnyPartner.ADRESY);
        event.addAlert(new YesNoAlert(getSessionObject().translateText(eshop.su.common.MDHelper.MESSAGE_VYMAZ_ZAZNAM)));
        buttonPanel.add(MediatorResourceBuilder.createButton(VYMAZ_ADRESA_ACTION, MediatorResourceBuilder.DELETE_BUTTON, event, MDUtilities.DELETE_ICON, getSessionObject()));
        
        //zdroj udajov
        XClientTable table = UCObchodnyPartnerAdresa.createTableProperty(UCObchodnyPartner.ADRESY, getSessionObject());
        table.setHeight(300);
        table.setWidth(600);
        
        XBorderPanel contentPane = new XBorderPanel(10, 10);
        contentPane.setInsets(new Insets(10,10,10,10)); 
        contentPane.setCenter(table);
        contentPane.setSouth(buttonPanel);     
        return contentPane;
    }  
	
	//metoda putFormToPack z MDOdberatelskaFaktura sem nedavam
	
	@Override
	protected XPanel createPanel(ServerPack pack) throws Exception {
        XTabbedPane tabbedPanel = new XTabbedPane("Zalozka");
        tabbedPanel.add(new XTabbedPage("zakladneUdaje", "Zakl. udaje", createAtributyPanel()));
        tabbedPanel.add(new XTabbedPage("kontakty", "Kontakty", createKontaktyPanel()));
        //pridavam panel s adresami
        tabbedPanel.add(new XTabbedPage("adresy", "Adresy", createAdresyPanel()));

        XBorderPanel contentPanel = new XBorderPanel(10, 10);
        contentPanel.setInsets(new Insets(10,10,10,10)); 
        contentPanel.setCenter(tabbedPanel);
        return contentPanel;
	}

	@Override
	protected void execute(ValuePack valuePack, ServerPack pack) throws Exception {
		obchodnyPartner.execute(valuePack);
		sendCallBack(new CallBack(obchodnyPartner.getId(), isNew), pack);
		close(pack);
	}

	@Override
	protected EnabledPack getEnabledPack() {
		return obchodnyPartner.getEnabledPack();
	}

	@Override
	protected RequiredPack getRequiredPack() {
		return obchodnyPartner.getRequiredPack();
	}

	@Override
	protected ValuePack getValuePack() {
		return obchodnyPartner.getValuePack();
	}
	
	//nepouziva sa pre ADD a OK buttony
	private void addEnabledPack(ServerPack serverPack) {
        EnabledPack pack = new EnabledPack();                
        pack.put(VYMAZ_ACTION, obchodnyPartner.getKontaktPocet() > 0);
        pack.put(OPRAVA_ACTION, obchodnyPartner.getKontaktPocet() > 0);
        pack.put(VYMAZ_ADRESA_ACTION, obchodnyPartner.getAdresaPocet() >0);
        
        UpdatedPack updatePack = new UpdatedPack(getId());
        updatePack.setEnabledPack(pack);
        serverPack.addUpdatedPack(updatePack);
    }
	
	private void addAktualizujKontaktyTable(ServerPack pack, Object key){
        ClientTableSettings tableValuePack = new ClientTableSettings();
        tableValuePack.setDataSource(obchodnyPartner.createDataContainerKontakt());    
        tableValuePack.setSelectedItem(key);
        tableValuePack.setScrollRowToVisible(key);
        ValuePack valuePack = new ValuePack();
        valuePack.put(UCObchodnyPartner.KONTAKTY, tableValuePack);                  
        UpdatedPack updatePack = new UpdatedPack(this.getId());
        updatePack.setValuePack(valuePack);
        pack.addUpdatedPack(updatePack);
        addEnabledPack(pack); 
    }
	
	//aktualizacia tabulky adries
	private void addAktualizujAdresyTable(ServerPack pack, Object key){
    	ClientTableSettings tableValuePack = new ClientTableSettings();
        tableValuePack.setDataSource(obchodnyPartner.createDataContainerAdresa());    
        tableValuePack.setSelectedItem(key);
        tableValuePack.setScrollRowToVisible(key);
        ValuePack valuePack = new ValuePack();
        valuePack.put(UCObchodnyPartner.ADRESY, tableValuePack);                  
        UpdatedPack updatePack = new UpdatedPack(this.getId());
        updatePack.setValuePack(valuePack);
        pack.addUpdatedPack(updatePack);
        addEnabledPack(pack); 
    }
	
	@Override
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
		try {
			if (event.getSourceId().equals(PRIDAJ_ACTION)) {
				runNext(MDKontakt.class, new MDKontakt.Parameters(obchodnyPartner.createKontakt()), pack);
			}else if (event.getSourceId().equals(OPRAVA_ACTION)) {
				startKontaktUpdate(event, pack);
			}else if (event.getSourceId().equals(VYMAZ_ACTION)) {
				// delete uz je doplnene
				Object id = FrameworkUtilities.getSelectedPrimaryKeyFromEvent(event, pack, UCObchodnyPartner.KONTAKTY, getSessionObject());
        		if (id != null) {     
        			//System.out.println("vymazany kontakt " + id);
        			obchodnyPartner.deleteKontakt(id);
        			addAktualizujKontaktyTable(pack, id);
        		} 
			}else if (event.getSourceId().equals(POTVRD_ACTION)) {                        
        		obchodnyPartner.execute(event.getValuePack());  
        		sendCallBack(new CallBack(obchodnyPartner.getId(), isNew), pack);
        		close(pack);
        	}else if (event.getSourceId().equals(VYMAZ_ADRESA_ACTION)) {            
            	Object id = FrameworkUtilities.getSelectedPrimaryKeyFromEvent(event, pack, UCObchodnyPartner.ADRESY, getSessionObject());
            	if (id != null) {   
            		//System.out.println("vymazana adresa " + id);
            		obchodnyPartner.deleteAdresa(id);
            		addAktualizujAdresyTable(pack, id);
            	}   
        	}else if (event.getSourceId().equals(PRIDAJ_ADRESA_ACTION)) {
        		if (vyberAdresaId == null) {
        			//TODO - HIDE_ON_CLOSE?
        			vyberAdresaId = this.runNext(MDVyberAdresa.class, null, pack, BasicMediator.HIDE_ON_CLOSE);
        		}else{
        			this.show(vyberAdresaId, pack);
        		}
        		((MDVyberAdresa) this.getMediator(vyberAdresaId)).oznacRiadky(obchodnyPartner.getAdresyIds(), pack); 
        	}else if (event.getSourceId().equals(CANCEL_ACTION)) {
        		close(pack);
        	}	
			//toto posledne potrebujem na to, aby mi aplikacia prestala pracovat po stlaceni tlacidla navrat
			else{
				super.actionEventExecuted(event, pack);
			}
		}catch(Exception e){
			addExceptionToPack(e, pack);
		}
	}
	
	private void startKontaktUpdate(ClientEvent event, ServerPack pack) {
		Object id = FrameworkUtilities.getSelectedPrimaryKeyFromEvent(event, pack, UCObchodnyPartner.KONTAKTY, getSessionObject());
		if (id != null) {
			this.runNext(MDKontakt.class, new MDKontakt.Parameters(obchodnyPartner.getKontakt(id)), pack); 
		}
    }
	
	//receiveCallBack rozsirujem o MDVyberAdresa, kontakt mozem vytvorit novy, ale adresu vyberam zo zoznamu
	@Override
	protected boolean receiveCallBack(BasicMediator mediator,  MediatorCallBackObject obj, ServerPack pack) {
		
		if (obj instanceof MDKontakt.CallBack) {
			MDKontakt.CallBack callback = (MDKontakt.CallBack) obj;		
        	addAktualizujKontaktyTable(pack, callback.kontakt.getId());
       	    if (callback.kontakt.isNew()) {
        		addEnabledPack(pack);
        	}
        }else if (obj instanceof MDVyberAdresa.CallBack) {
        	MDVyberAdresa.CallBack callback = (MDVyberAdresa.CallBack) obj;
        	try {        	    
        		obchodnyPartner.addAdresaIds(callback.adresyIds);
        		this.addAktualizujAdresyTable(pack, callback.adresyIds[0]);
        	}catch(Exception e) {
        		this.addExceptionToPack(e, pack);
        	}
        }
		return true;
    }
	
	public static class Parameters implements MediatorParameters {
		
		public Object id;
    
		public Parameters (Object id) {
			this.id = id;
		}
	}

	public class CallBack implements MediatorCallBackObject {
		
		public Object id;
		public boolean isNew;
	
		public CallBack(Object id, boolean isNew) {
			this.id = id;
			this.isNew = isNew;
		}
	}
}
