package eshop.su.ciselnik.md;

import javax.swing.SwingConstants;

import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.TypObchodnehoPartnera;
import eshop.su.common.view.ViewObchodnyPartner;
import netball.server.component.XBoxPanel;
import netball.server.component.XClientTable;
import netball.server.component.XForm;
import netball.server.component.XPanel;
import netball.server.component.table.ClientTableSelectedRow;
import netball.server.event.ClientActionEvent;
import netball.server.event.ClientEvent;
import netball.server.event.ClientMouseEvent;
import netball.server.event.ClientWindowEvent;
import netball.server.event.ServerActionEvent;
import netball.server.event.ServerMouseEvent;
import netball.server.event.ServerWindowEvent;
import netball.server.pack.EnabledPack;
import netball.server.pack.ServerPack;
import netframework.access.AccessAction;
import netframework.access.AccessControlObject;
import netframework.mediator.MDUtilities;
import netframework.mediator.MDViewBasicClientTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;
import netframework.mediator.resource.MediatorResourceBuilder;

public abstract class MDVyberObchodnyPartner extends MDViewBasicClientTable {
	
	//zmenil som obe na protected kvoli dedeniu
	protected final String POTVRD_ACTION = createId(MediatorResourceBuilder.OK_BUTTON);
    protected final String VYBER_ACTION = createId(MediatorResourceBuilder.READ_BUTTON);
    
    @Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
	}
      
	@Override
	public AccessAction[] getAccessActions() {
		return null;
	}

	@Override
	protected ViewTableDataSource createDataSource() {
		String cols[] = new String[] {	
			    ViewObchodnyPartner.ID,
			    ViewObchodnyPartner.KOD,
			    ViewObchodnyPartner.NAZOV,
			    };
		return new ViewTableDataSource(new ViewObchodnyPartner(), cols, ViewObchodnyPartner.ID, ViewObchodnyPartner.KOD);
	}

	@Override
	protected String getTitleText() {
		return "Obchodni partneri";
	}

	@Override
	protected EnabledPack createButtonEnabledPack() {
		EnabledPack pack = new EnabledPack();
	       pack.put(POTVRD_ACTION, getRowCount() > 0);
	       return pack;
	}
	
	//prekopirovane z MDVyberZakaznik v ramci dema
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
	
	/* odkomentovane, lebo mouse event to je
	@Override
	   protected XClientTable createTable(ServerPack serverPack) {
		   XClientTable table = super.createTable(serverPack);
		   ServerMouseEvent event = new ServerMouseEvent(ServerMouseEvent.MOUSE_CLICKED_EVENT);
		   event.setDoubleClick(true);
		   event.addReturnValue(TABLE);
		   table.addMouseEvent(event);
		   return table;
	   }
	*/
	
	@Override
	   public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
	     try{
	           if(event.getSourceId().equals(POTVRD_ACTION)){
	   			   sendCallBack(event, pack);
	           }else if (event.getSourceId().equals(VYBER_ACTION)) {
	        	           	   
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
	
	/*
	@Override
	public void mouseEventExecuted(ClientMouseEvent event, ServerPack pack) {
   		if (event.getSourceId().equals(TABLE)) {
   			sendCallBack(event, pack);
   		}
	}
	*/
	
	@Override
	public void windowEventExecuted(ClientWindowEvent event, ServerPack pack) {
		
		if (event.getCode() == ServerWindowEvent.WINDOW_OPENED_EVENT) {
			/*
			if (event.getValuePack().getValue(ObchodnyPartner.TYP).equals("ZAKAZNIK")) {
				this.runNext(MDFilterZakaznik.class, this.getFilterParameters(), pack);
			}else if (event.getValuePack().getValue(ObchodnyPartner.TYP).equals("ZAKAZNIK")){
				this.runNext(MDFilterDodavatel.class, this.getFilterParameters(), pack);
			}
			//MDFilterVyberZakaznik?
			//
			 * 
			 */
			//this.runNext(MDFilterZakaznik.class, this.getFilterParameters(), pack);
		}
		super.windowEventExecuted(event, pack);
	}
	
	protected void sendCallBack(ClientEvent event, ServerPack pack) {
        Object id = getSelectedPrimaryKeyFromEvent(event, pack);
        if(id != null){
        	ClientTableSelectedRow rows[] = (ClientTableSelectedRow[]) event.getValuePack().getValue(TABLE);
    	    Object kod = (String) rows[0].getRow().getValueAt(ViewObchodnyPartner.KOD);
     	    this.sendCallBack(new CallBack(id, kod), pack);
               close(pack);
        }
   	}
	
	public static class CallBack implements MediatorCallBackObject {
    	
    	public Object obchodnyPartnerId;
    	public Object kod;
    	
    	public CallBack(Object obchodnyPartnerId, Object kod) {
    		this.obchodnyPartnerId = obchodnyPartnerId;
    		this.kod = kod;
    	}
    }

}
