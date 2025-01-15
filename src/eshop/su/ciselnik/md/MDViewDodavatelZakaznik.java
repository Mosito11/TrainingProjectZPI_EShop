package eshop.su.ciselnik.md;

import java.awt.Color;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import com.sun.org.apache.xml.internal.security.Init;

import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.TypObchodnehoPartnera;
import eshop.su.ciselnik.uc.UCObchodnyPartner;
import eshop.su.common.view.ViewAdresa;
import eshop.su.common.view.ViewObchodnyPartner;
import netball.server.component.XBoxPanel;
import netball.server.component.XButton;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XForm;
import netball.server.component.XMenu;
import netball.server.component.XPanel;
import netball.server.component.XPopupMenuButton;
import netball.server.component.setting.PopupMenuButtonSettings;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.event.ServerPopupEvent;
import netball.server.event.alert.YesNoAlert;
import netball.server.pack.EnabledPack;
import netball.server.pack.FormPack;
import netball.server.pack.ServerPack;
import netball.server.pack.UpdatedPack;
import netball.server.pack.ValuePack;
import netframework.access.AccessAction;
import netframework.access.AccessControlObject;
import netframework.eclipselink.EclipseLinkSession;
import netframework.eclipselink.EclipseLinkView;
import netframework.mediator.BasicMediator;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDTablePreview;
import netframework.mediator.MDUtilities;
import netframework.mediator.MDViewBasicClientServerTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;
import netframework.mediator.ctb.MDViewClientServerTableCTB;
import netframework.mediator.resource.MediatorResourceBuilder;

public class MDViewDodavatelZakaznik extends MDViewBasicClientServerTable {
	
	//zakladne buttony v danom formulari
	private final String PRIDAJ_ACTION = createId(MediatorResourceBuilder.ADD_BUTTON);
    private final String OPRAVA_ACTION = createId(MediatorResourceBuilder.CORRECT_BUTTON);
    private final String VYMAZ_ACTION = createId(MediatorResourceBuilder.DELETE_BUTTON);
    private final String TLAC_ACTION = createId(MediatorResourceBuilder.PRINT_BUTTON);
    private final String VYBER_ACTION = createId(MediatorResourceBuilder.READ_BUTTON);
    //vytvaram si novy typ buttonu v MDHelperi
    private final String TYP_ACTION = createId(eshop.su.common.MDHelper.TYP_BUTTON.getId());
    
    

    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
		read(pack, null, null, false);
		//tymto vyriesim spristupnenie buttonov v pripade, ze uz mi nacita tabulku zaznamov (tj. nemusim robit vyber, ale na kozdom zazname bude mozne OPRAVA, VYMAZ a TLAC)
		addButtonEnabledPack(pack);
	}
	    
    private TypObchodnehoPartneraActions typObchodnehoPartneraActions = new TypObchodnehoPartneraActions();
    
	@Override
	public AccessAction[] getAccessActions() {
		return new AccessAction[] {
	               getAccessAction(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON),
	               getAccessAction(OPRAVA_ACTION, MediatorResourceBuilder.CORRECT_BUTTON),
	               getAccessAction(VYMAZ_ACTION, MediatorResourceBuilder.DELETE_BUTTON),
	               new AccessAction(TYP_ACTION, eshop.su.common.MDHelper.TYP_BUTTON.getText(), eshop.su.common.MDHelper.TYP_BUTTON.getDescription(), typObchodnehoPartneraActions)
	         };
	}
	
	private AccessAction getAccessAction(String actionCode, String buttonCode) {
    	return MediatorResourceBuilder.createAccessAction(actionCode, buttonCode, getLocale());
    }
	
	//TODO - dorobit ICO (skusil som ViewObchodnyPartner1 ako extends EclipseLinkView )
	@Override
	protected ViewTableDataSource createDataSource() {
		String[] columns = new String[] {ViewObchodnyPartner.ID,
				ViewObchodnyPartner.KOD,
				ViewObchodnyPartner.NAZOV,
				ViewObchodnyPartner.TYP,
				ViewObchodnyPartner.ICO,
				};
		return new ViewTableDataSource(new ViewObchodnyPartner(), columns, ViewObchodnyPartner.ID, ViewObchodnyPartner.NAZOV);		
	}

	@Override
	protected String getTitleText() {
		return "Obchodni partneri";
	}
		
	@Override
	protected XPanel createEastPanel(ServerPack pack) {
    	XBoxPanel panel = new XBoxPanel(SwingConstants.VERTICAL);
    	panel.setSameSizeForAllComponents(true);
    	panel.setGapForAll(5);
    	
    	/*
    	ServerActionEvent event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        event.addReturnProperty(TYP_ACTION);
        panel.add(createTypButton());
    	*/
        panel.add(createButton(VYBER_ACTION, MediatorResourceBuilder.READ_BUTTON, MDUtilities.READ_ICON, new ServerActionEvent()));
        
        ServerActionEvent event = new ServerActionEvent();
        panel.add(createPridajButton());
        //panel.add(createButton(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON, MDUtilities.ADD_ICON, new ServerActionEvent()));

        event = new ServerActionEvent();
    	event.addReturnValue(TABLE);
        panel.add(createButton(OPRAVA_ACTION, MediatorResourceBuilder.CORRECT_BUTTON, MDUtilities.CORRECT_ICON, event));

        event = new ServerActionEvent();
        event.addReturnValue(TABLE);
        event.addAlert(new YesNoAlert(getSessionObject().translateText(eshop.su.common.MDHelper.MESSAGE_VYMAZ_ZAZNAM)));
        panel.add(createButton(VYMAZ_ACTION, MediatorResourceBuilder.DELETE_BUTTON, MDUtilities.DELETE_ICON, event));
        
        panel.add(createButton(TLAC_ACTION, MediatorResourceBuilder.PRINT_BUTTON, MDUtilities.PRINT_ICON, new ServerActionEvent()));
        
        panel.add(createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, MDUtilities.HOME_ICON, new ServerActionEvent()));
        addButtonEnabledPack(pack);
        return panel;
    }
	
   /* button povodne na filtrovanie, cize vyber typu obchodneho partnera
	private XPopupMenuButton createTypButton() {
    	XPopupMenuButton button = MDUtilities.createPopupMenuButton(TYP_ACTION, eshop.su.common.MDHelper.TYP_BUTTON, null, getSessionObject());

        XMenu menuItem = new XMenu(typObchodnehoPartneraActions.ZAKAZNIK_ACTION, TypObchodnehoPartnera.ZAKAZNIK.getText());
        ServerActionEvent event = new ServerActionEvent();
        event.addReturnValue(TABLE);                     
        menuItem.addActionEvent(event);
        button.addMenuItem(menuItem);
    	
        menuItem = new XMenu(typObchodnehoPartneraActions.DODAVATEL_ACTION, TypObchodnehoPartnera.DODAVATEL.getText());
        event = new ServerActionEvent();
        event.addReturnValue(TABLE);                        
        menuItem.addActionEvent(event);
        button.addMenuItem(menuItem);
    	
    	return button;
    }
	*/
	private XPopupMenuButton createPridajButton() {
    	XPopupMenuButton button = MDUtilities.createPopupMenuButton(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON, MDUtilities.ADD_ICON, getSessionObject());
    	
    	button.setDescription(eshop.su.common.MDHelper.TYP_BUTTON.getDescription());
    	
        XMenu menuItem = new XMenu(typObchodnehoPartneraActions.ZAKAZNIK_ACTION, TypObchodnehoPartnera.ZAKAZNIK.getText());
        ServerActionEvent event = new ServerActionEvent();
        //event.addReturnValue(TABLE);     
        menuItem.addActionEvent(event);
        button.addMenuItem(menuItem);
    	
        menuItem = new XMenu(typObchodnehoPartneraActions.DODAVATEL_ACTION, TypObchodnehoPartnera.DODAVATEL.getText());
        event = new ServerActionEvent();
        //event.addReturnValue(TABLE);
        menuItem.addActionEvent(event);
        button.addMenuItem(menuItem);
    	
    	return button;
    }
	
	@Override
	protected EnabledPack createButtonEnabledPack() {
		EnabledPack pack = new EnabledPack();
        if (getRowCount() > 0) {
            pack.put(OPRAVA_ACTION, true);
            pack.put(TLAC_ACTION, true);
            pack.put(VYMAZ_ACTION, true);
        }else{
            pack.put(OPRAVA_ACTION, false);
            pack.put(TLAC_ACTION, false);
            pack.put(VYMAZ_ACTION, false);
        }        
		return pack;
	}
	
	//pokus o popup okno, asi slepa ulicka
	/*
	private XBoxPanel initPopUp(ServerPack serverPack) throws Exception {
		   
		XBoxPanel panel = new XBoxPanel();
		   panel.setInsets(new Insets(20, 20, 20, 20));
		   panel.setGapForAll(5);
		   
		   ServerActionEvent event = new ServerActionEvent();
	       event.addReturnValue(typObchodnehoPartneraActions);
	       event.addReturnProperty(TYP_ACTION);
	       panel.add(createTypButton());
	       
		  
		   XPopupMenuButton button = new XPopupMenuButton("button0", "Vyber typu obchodneho partnera");
		   
		   button.setIcon(MDUtilities.loadIcon(MDUtilities.ADD_ICON, getSessionObject()));
		   
		   button.addMenuItem(new XMenu("button0.item1", "Pridaj zakaznika"));   
	       button.addMenuItem(new XMenu("button0.item2", "Pridaj dodavatela"));      
	      
	       ServerActionEvent event = new ServerActionEvent();
		   //event.addReturnValue(TYP_ACTION);
		   event.addReturnValue(typObchodnehoPartneraActions);
	       //event.addReturnValue(TABLE);
	       button.addActionEvent(event);
	       
	       panel.add(button);
	      
	   	   XForm form = new XForm();
	   	   form.setPanel(panel); 
	   	   form.setTitle("Vyber pridanie zakaznika alebo dodavatela");   	
	   	   serverPack.addFormPack(new FormPack(getId(), form));
	   	   
	   	   System.out.println(event);
	   	   
	       addButtonEnabledPack(serverPack);
	   	   
	       return panel;	   	  
	}
	*/
			
	@Override
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
        try {
        	if (event.getSourceId().equals(typObchodnehoPartneraActions.ZAKAZNIK_ACTION)) {
        		
        		runNext(MDZakaznik.class, null, pack);
        		//kvoli filtrovaniu
        		//readByFilter(pack, MDFilterZakaznik.class);       			
        	}else if (event.getSourceId().equals(typObchodnehoPartneraActions.DODAVATEL_ACTION)) {
        		
        		runNext(MDDodavatel.class, null, pack);    
        		//readByFilter(pack, MDFilterDodavatel.class);
           	}
     	      	
        	if (event.getSourceId().equals(OPRAVA_ACTION)) {            
        		int index = this.getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			Object id = this.getPrimaryKeyValue(index);
        			//takto sa dostanem k datam na danom objekte
        			Object typ = this.getData().getRow(index).getValueAt(this.getData().getColumnIndex(ViewObchodnyPartner.TYP));
        			if (typ.equals(TypObchodnehoPartnera.ZAKAZNIK.getKey())) runNext(MDZakaznik.class, new MDObchodnyPartner.Parameters(id), pack);
        			if (typ.equals(TypObchodnehoPartnera.DODAVATEL.getKey())) runNext(MDDodavatel.class, new MDObchodnyPartner.Parameters(id), pack);
        			this.setSelectedIndex(index);
        		}
        	}else if (event.getSourceId().equals(PRIDAJ_ACTION)) {
        		
        		//tu sa potrebujem spytat, ci chcem pridat zakaznika alebo dodavatela - pokus o popup okno
     		    //initPopUp(pack);
        		        	
        	}else if (event.getSourceId().equals(VYMAZ_ACTION)) {             
        		int index = this.getSelectedIndexFromEvent(event, pack);
        		if (index != -1) {
        			Object id = this.getPrimaryKeyValue(index);
        			UCObchodnyPartner.delete(id, (EclipseLinkSession) this.getSessionObject());
        			//System.out.println("Obchodny partner vymazany!");
        			this.addDeletedPack(index, pack);
        			this.addButtonEnabledPack(pack);
        		}   
        	}else if (event.getSourceId().equals(VYBER_ACTION)) {        	  
        		this.read(pack, null, null, false);
        		this.addButtonEnabledPack(pack);
        	}else if (event.getSourceId().equals(TLAC_ACTION)) {
        		runNext(MDTablePreview.class, new MDTablePreview.Parameters(TABLE, getId()), pack);
        	}else{
            	super.actionEventExecuted(event, pack); 
        	}    
        }catch(Exception e) {
        	addExceptionToPack(e, pack);
        }                           
    }    
 		
	//v podstate som len zamenil triedu
	@Override
    protected boolean receiveCallBack(BasicMediator mediator,  MediatorCallBackObject obj, ServerPack pack) {
		
		if (obj instanceof MDObchodnyPartner.CallBack) {
			MDObchodnyPartner.CallBack callBack = (MDObchodnyPartner.CallBack) obj;
        	if (callBack.isNew) {
        		addInsertedPack(callBack.id, pack);          
        		addButtonEnabledPack(pack);
        	}else {
        		addUpdatedPack(callBack.id, this.getSelectedIndex(), pack);          
        	}
		}else {
        	return super.receiveCallBack(mediator, obj, pack);
        }  
        return false;        
    }
	
	private class TypObchodnehoPartneraActions implements AccessControlObject {
		
		final String ZAKAZNIK_ACTION = createId(TypObchodnehoPartnera.ZAKAZNIK.getKey());
		final String DODAVATEL_ACTION = createId(TypObchodnehoPartnera.DODAVATEL.getKey());
		
	   	public AccessAction[] getAccessActions() {
	   		return new AccessAction[] {
	   				new AccessAction(ZAKAZNIK_ACTION, TypObchodnehoPartnera.ZAKAZNIK.getText()),
	   				new AccessAction(DODAVATEL_ACTION, TypObchodnehoPartnera.DODAVATEL.getText()),
	   		};
	   	}
    }

}
