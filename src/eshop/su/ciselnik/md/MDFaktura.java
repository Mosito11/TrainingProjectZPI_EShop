package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.Faktura;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.ciselnik.md.MDObjednavka.CallBack;
import eshop.su.ciselnik.md.MDObjednavka.Parameters;
import eshop.su.ciselnik.uc.UCFaktura;
import eshop.su.ciselnik.uc.UCObjednavka;
import eshop.su.ciselnik.uc.UCObjednavkaPolozka;
import eshop.su.komponenty.ComponentObjednavka;
import eshop.su.komponenty.ComponentZakaznik;
import netball.server.component.XBorderPanel;
import netball.server.component.XBoxPanel;
import netball.server.component.XClientTable;
import netball.server.component.XCompoundField;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XForm;
import netball.server.component.XPanel;
import netball.server.component.XTabbedPage;
import netball.server.component.XTabbedPane;
import netball.server.component.setting.ClientTableSettings;
import netball.server.event.ClientActionEvent;
import netball.server.event.ClientFocusEvent;
import netball.server.event.ServerActionEvent;
import netball.server.event.ServerFocusEvent;
import netball.server.event.alert.YesNoAlert;
import netball.server.pack.EnabledPack;
import netball.server.pack.FormPack;
import netball.server.pack.ServerPack;
import netball.server.pack.UpdatedPack;
import netball.server.pack.ValuePack;
import netframework.FrameworkUtilities;
import netframework.access.AccessAction;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDUtilities;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.resource.MediatorResourceBuilder;
import zelpo.eclipselink.autorizacia.md.ComponentUzivatel;

public class MDFaktura extends BasicMediator {

	private final String CANCEL_ACTION = createId(MediatorResourceBuilder.CANCEL_BUTTON);
    private final String POTVRD_ACTION = createId(MediatorResourceBuilder.OK_BUTTON);
	
    private UCFaktura faktura;
    private boolean isNew;
    
    @Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		if (parameters == null) {
	           faktura = UCFaktura.create((EclipseLinkSession) getSessionObject()); 
	           isNew = true;             
	        }else if (parameters instanceof Parameters) {
	           Parameters prmts = (Parameters) parameters;    
	           faktura = UCFaktura.read(prmts.fakturaId, (EclipseLinkSession) getSessionObject());
	        }else {
	            throw new IllegalArgumentException("Chybny parameter!"); 
	        }
		this.putFormToPack(pack);	
	}
    
    @Override
	public AccessAction[] getAccessActions() {
		return null;
	}
    
    protected XPanel createPanel() {
        XDualComponentPanel panel = new XDualComponentPanel();
        panel.setInsets(new Insets(10,10,10,10));
        panel.add(ComponentBuilder.createComponent(UCFaktura.CISLO_FAKTURY, Faktura.CISLO_FAKTURY, "Cislo faktury", getSessionObject()));        
        
        //TODO - treba sa mi troska pohrat s tymi Componentami este
        //panel.add(ComponentBuilder.createComponent(UCObjednavka.ZAKAZNIK_KOD, Zakaznik.KOD, false, getSessionObject()));        
        
        XCompoundField objednavkaComp = (XCompoundField) ComponentObjednavka.createComponent(UCFaktura.CISLO_OBJEDNAVKY, this);
        ServerFocusEvent focusEvent = new ServerFocusEvent(ServerFocusEvent.FOCUS_LAST_EVENT);
        focusEvent.addReturnValue(UCFaktura.CISLO_OBJEDNAVKY);
        objednavkaComp.addFocusEvent(focusEvent);
        panel.add(objednavkaComp);
        
        panel.add(ComponentBuilder.createComponent(UCFaktura.DATUM_DODANIA, Faktura.DATUM_DODANIA	, "Datum dodania", getSessionObject()));        
        
        //panel.add(ComponentBuilder.createReadOnlyComponent(UCFaktura.ZAKAZNIK_NAZOV, Zakaznik.NAZOV, getSessionObject()));    
        /*
        //je mozne, ze tu bude nie meno ale uzivatelske meno
        XCompoundField vystavilComp = (XCompoundField) ComponentUzivatel.createComponent(UCObjednavka.VYSTAVIL, this);
        //musim si robit novy ServerFocusEvent?
        ServerFocusEvent focusEvent1 = new ServerFocusEvent(ServerFocusEvent.FOCUS_LAST_EVENT);
        focusEvent1.addReturnValue(UCObjednavka.VYSTAVIL);
        vystavilComp.addFocusEvent(focusEvent1);
        panel.add(vystavilComp);
        */    
        return panel;
    }
	
    //TODO - polozky z objednavky iba precitat
    /*
	protected XPanel createPolozkyPanel() {
        XBoxPanel buttonPanel = new XBoxPanel(javax.swing.SwingConstants.HORIZONTAL);
        buttonPanel.setSameSizeForAllComponents(true);
        buttonPanel.setGapForAll(5);
        buttonPanel.add(MediatorResourceBuilder.createButton(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON, new ServerActionEvent(), MDUtilities.ADD_ICON, getSessionObject()));

        ServerActionEvent event = new ServerActionEvent();
        event.addReturnValue(UCObjednavka.POLOZKY);
        buttonPanel.add(MediatorResourceBuilder.createButton(OPRAVA_ACTION, MediatorResourceBuilder.CORRECT_BUTTON, event, MDUtilities.CORRECT_ICON, getSessionObject()));                
        
        event = new ServerActionEvent();
        event.addReturnValue(UCObjednavka.POLOZKY);
        event.addAlert(new YesNoAlert(getSessionObject().translateText(eshop.su.common.MDHelper.MESSAGE_VYMAZ_ZAZNAM)));
        buttonPanel.add(MediatorResourceBuilder.createButton(VYMAZ_ACTION, MediatorResourceBuilder.DELETE_BUTTON, event, MDUtilities.DELETE_ICON, getSessionObject()));

        //v MDObjednavkaPolozka vyrobit tabulku, ale este neviem ako
        XClientTable table = UCObjednavkaPolozka.createTableProperty(UCObjednavka.POLOZKY, getSessionObject());
        
        table.setHeight(300);
        table.setWidth(600);
        
        XBorderPanel contentPanel = new XBorderPanel(10, 10);
        contentPanel.setInsets(new Insets(10,10,10,10)); 
        contentPanel.setCenter(table);
        contentPanel.setSouth(buttonPanel);     
        return contentPanel;
    }
	*/
    
    public void putFormToPack(ServerPack serverPack){
        XBoxPanel buttonPanel = new XBoxPanel(javax.swing.SwingConstants.HORIZONTAL);
        buttonPanel.setSameSizeForAllComponents(true);
        buttonPanel.setGapForAll(5);
        buttonPanel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);        
        
        ServerActionEvent event = new ServerActionEvent();
        event.setReturnAllValues(true);        
        //co je toto, potrebujem?
        event.setDoControlOfRequiredValues(true);
        buttonPanel.add(MediatorResourceBuilder.createButton(POTVRD_ACTION, MediatorResourceBuilder.OK_BUTTON, event, MDUtilities.OK_ICON, getSessionObject()));
        buttonPanel.add(MediatorResourceBuilder.createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, MDUtilities.HOME_ICON, getSessionObject()));
              
        XTabbedPane tabbedPanel = new XTabbedPane("Zalozka");
        tabbedPanel.add(new XTabbedPage("zakladneUdaje", "Zakl. udaje", createPanel()));
        //tabbedPanel.add(new XTabbedPage("polozky", "Polozky", createPolozkyPanel()));
        
        XBorderPanel contentPanel = new XBorderPanel(10, 10);
        contentPanel.setInsets(new Insets(10,10,10,10)); 
        contentPanel.setCenter(tabbedPanel);
        contentPanel.setSouth(buttonPanel);     
        
        //toto mam z MD Faktury, neviem ci a ako to tu ma byt (v MDObchodnyPartner to nemam)
        XForm form = new XForm();
        form.setTitle("Faktury");
        form.setPanel(contentPanel);            
        form.setHotButton(POTVRD_ACTION);
        addEnabledPack(serverPack);
       
        FormPack formPack = new FormPack(getId(), form);             
        formPack.setValuePack(faktura.getValuePack());
        formPack.setEnabledPack(faktura.getEnabledPack());
        formPack.setRequiredPack(faktura.getRequiredPack());     
        serverPack.addFormPack(formPack);    
    }
    
    private void addEnabledPack(ServerPack serverPack) {
        EnabledPack pack = new EnabledPack();                
        //pack.put(VYMAZ_ACTION, faktura.getPolozkaPocet() > 0);
        //pack.put(OPRAVA_ACTION, faktura.getPolozkaPocet() > 0);
        
        UpdatedPack updatePack = new UpdatedPack(getId());
        updatePack.setEnabledPack(pack);
        serverPack.addUpdatedPack(updatePack);
	}
    /*
    private void addAktualizujPolozkyTable(ServerPack pack, Object key){
        ClientTableSettings tableValuePack = new ClientTableSettings();
        tableValuePack.setDataSource(faktura.createDataContainerPolozka());    
        tableValuePack.setSelectedItem(key);
        tableValuePack.setScrollRowToVisible(key);
        ValuePack valuePack = new ValuePack();
        valuePack.put(UCObjednavka.POLOZKY, tableValuePack);                  
        UpdatedPack updatePack = new UpdatedPack(this.getId());
        updatePack.setValuePack(valuePack);
        pack.addUpdatedPack(updatePack);
        addEnabledPack(pack); 
    }
    */
    @Override
	public void focusEventExecuted(ClientFocusEvent event, ServerPack pack) {
		if (event.getSourceId().equals(UCFaktura.CISLO_OBJEDNAVKY)) {
			Object cisloObjednavky = event.getValuePack().getValue(UCFaktura.CISLO_OBJEDNAVKY);
			try {
				pack.addUpdatedPack(new UpdatedPack(getId(), faktura.setCisloObjednavky(cisloObjednavky)));
			} catch (Exception e) {
				addExceptionToPack(e, pack);
			}
		}/*else if (event.getSourceId().equals(UCObjednavka.VYSTAVIL)) {
			Object vystavil = event.getValuePack().getValue(UCObjednavka.VYSTAVIL);
			try {
				pack.addUpdatedPack(new UpdatedPack(getId(), objednavka.setVystavilUzivatelskeMeno(vystavil)));
			} catch (Exception e) {
				addExceptionToPack(e, pack);
			}
		}*/	
	}
    
    @Override
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
	    try {/*
        	if (event.getSourceId().equals(VYMAZ_ACTION)) {            
        		Object id = FrameworkUtilities.getSelectedPrimaryKeyFromEvent(event, pack, UCObjednavka.POLOZKY, getSessionObject());
        		
        		if (id != null) {            
        			objednavka.deletePolozka(id);
        			addAktualizujPolozkyTable(pack, id);
        		}   
        	}else if (event.getSourceId().equals(PRIDAJ_ACTION)) {
        		this.runNext(MDObjednavkaPolozka.class, new MDObjednavkaPolozka.Parameters(objednavka.createPolozka()), pack);        	 
        	}else if (event.getSourceId().equals(OPRAVA_ACTION)) {
        		startObjednavkaPolozkaUpdate(event, pack);
        	}else */if (event.getSourceId().equals(POTVRD_ACTION)) {                        
        		faktura.execute(event.getValuePack());  
        		sendCallBack(new CallBack(faktura.getId(), isNew), pack);
        		close(pack);
        	}/*else if ((event.getSourceId().equals(DETAIL_ACTION))){
        		Object id = FrameworkUtilities.getSelectedPrimaryKeyFromEvent(event, pack, TABLE_OBJEDNAVKA, getSessionObject());
                if (id != null) {
                	System.out.println("Som v Action Event Executed, DETAIL ACTION s tymto id " + id);
             	   //runNext(MDDetailObjednavka.class, new MDDetailObjednavka.Parameter(id), pack);
                }
        	}*/else if (event.getSourceId().equals(CANCEL_ACTION)) {
        		close(pack);
        	}
        }catch(Exception e) {
        	addExceptionToPack(e, pack);
        }                           
    }
    /*
    @Override
	protected boolean receiveCallBack(BasicMediator mediator,  MediatorCallBackObject obj, ServerPack pack) {
        if (obj instanceof MDObjednavkaPolozka.CallBack) {
        	
        	MDObjednavkaPolozka.CallBack callback = (MDObjednavkaPolozka.CallBack) obj;
        	addAktualizujPolozkyTable(pack, callback.polozka.getId());
       	    if (callback.polozka.isNew()) {
        		addEnabledPack(pack);
        	}
        }
        return true;
    } 
	*/
	public static class Parameters implements MediatorParameters {
		
		public Object fakturaId;
    
		public Parameters (Object fakturaId) {
			this.fakturaId = fakturaId;
		}
	}

	public class CallBack implements MediatorCallBackObject {
		
		public Object fakturaId;
		public boolean isNew;
	
		public CallBack(Object fakturaId, boolean isNew) {
			this.fakturaId = fakturaId;
			this.isNew = isNew;
		}
	}

}
