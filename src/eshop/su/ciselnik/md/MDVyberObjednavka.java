package eshop.su.ciselnik.md;

import javax.swing.SwingConstants;

import eshop.su.ciselnik.md.MDVyberObchodnyPartner.CallBack;
import eshop.su.common.view.ViewObchodnyPartner;
import eshop.su.common.view.ViewObjednavka;
import netball.server.component.XBoxPanel;
import netball.server.component.XClientTable;
import netball.server.component.XForm;
import netball.server.component.XPanel;
import netball.server.component.table.ClientTableSelectedRow;
import netball.server.event.ClientActionEvent;
import netball.server.event.ClientEvent;
import netball.server.event.ClientWindowEvent;
import netball.server.event.ServerActionEvent;
import netball.server.event.ServerWindowEvent;
import netball.server.pack.EnabledPack;
import netball.server.pack.ServerPack;
import netframework.access.AccessAction;
import netframework.mediator.MDUtilities;
import netframework.mediator.MDViewBasicClientTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;
import netframework.mediator.resource.MediatorResourceBuilder;

public class MDVyberObjednavka extends MDViewBasicClientTable {

	protected final String POTVRD_ACTION = createId(MediatorResourceBuilder.OK_BUTTON);
    protected final String VYBER_ACTION = createId(MediatorResourceBuilder.READ_BUTTON);
	
    @Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
		this.read(pack, null, null, false);
	}
    
    @Override
	public AccessAction[] getAccessActions() {
		return null;
	}
    
    @Override
	protected ViewTableDataSource createDataSource() {
		String cols[] = new String[] {	
			    ViewObjednavka.ID,
			    ViewObjednavka.CISLO_OBJEDNAVKY,
			    ViewObjednavka.ZAKAZNIK_NAZOV,
			    ViewObjednavka.VYSTAVIL
			    };
		return new ViewTableDataSource(new ViewObjednavka(), cols, ViewObjednavka.ID, ViewObjednavka.CISLO_OBJEDNAVKY);
	}

    @Override
    protected String getTitleText() {
    	return "Objednavky";
    }
    
    @Override
	protected EnabledPack createButtonEnabledPack() {
		EnabledPack pack = new EnabledPack();
	       pack.put(POTVRD_ACTION, getRowCount() > 0);
	       return pack;
	}

    @Override
	protected XPanel createEastPanel(ServerPack pack) {
    	XBoxPanel panel = new XBoxPanel(SwingConstants.VERTICAL);
    	panel.setSameSizeForAllComponents(true);
    	panel.setGapForAll(5);
    	
        panel.add(createButton(VYBER_ACTION, MediatorResourceBuilder.READ_BUTTON, MDUtilities.READ_ICON, new ServerActionEvent()));
    	
    	ServerActionEvent event = new ServerActionEvent();
    	event.addReturnValue(TABLE);    	
    	panel.add(createButton(POTVRD_ACTION, MediatorResourceBuilder.OK_BUTTON, MDUtilities.OK_ICON, event));
    	
        panel.add(createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, MDUtilities.HOME_ICON, new ServerActionEvent()));
        addButtonEnabledPack(pack);
        return panel;
    }
    
    @Override	
	   protected XForm createForm(){
		   XForm form = super.createForm();
	       form.setHotButton(POTVRD_ACTION);
	       form.addWindowEvent(new ServerWindowEvent(ServerWindowEvent.WINDOW_OPENED_EVENT));
	       return form;
	}
    
    @Override
	   public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
	     try{
	           if(event.getSourceId().equals(POTVRD_ACTION)){
	   			   sendCallBack(event, pack);
	           }else if (event.getSourceId().equals(VYBER_ACTION)) {
	        	   //this.runNext(MDViewObjednavka.class, this.getFilterParameters(), pack);        	   
	        	   //MDFilterVyberZakaznik?
	        	   //this.runNext(MDFilterZakaznik.class, this.getFilterParameters(), pack);
	           	   //this.runNext(MDFilterDodavatel.class, this.getFilterParameters(), pack);
	           }else{
	        	   super.actionEventExecuted(event, pack);
	           }
	       }catch(Exception e){
	           addExceptionToPack(e, pack);
	       }
	 }
    
    @Override
    protected XClientTable createTable(ServerPack serverPack) {
    	XClientTable table = super.createTable(serverPack);
    	table.setSelectionMode(XClientTable.MULTIPLE_INTERVAL_SELECTION);
		return table;
    }
    /*
    @Override
	public void windowEventExecuted(ClientWindowEvent event, ServerPack pack) {
		
		if (event.getCode() == ServerWindowEvent.WINDOW_OPENED_EVENT) {
			//this.runNext(MDObjednavka.class, this.getFilterParameters(), pack);
			}
		super.windowEventExecuted(event, pack);
	}
    */
    protected void sendCallBack(ClientEvent event, ServerPack pack) {
        Object id = getSelectedPrimaryKeyFromEvent(event, pack);
        if(id != null){
        	ClientTableSelectedRow rows[] = (ClientTableSelectedRow[]) event.getValuePack().getValue(TABLE);
    	    Object cisloObjednavky = (String) rows[0].getRow().getValueAt(ViewObjednavka.CISLO_OBJEDNAVKY);
     	    this.sendCallBack(new CallBack(id, cisloObjednavky), pack);
               close(pack);
        }
   	}
	
	public static class CallBack implements MediatorCallBackObject {
    	
    	public Object objednavkaId;
    	public Object cisloObjednavky;
    	
    	public CallBack(Object objednavkaId, Object cisloObjednavky) {
    		this.objednavkaId = objednavkaId;
    		this.cisloObjednavky = cisloObjednavky;
    	}
    }
}
