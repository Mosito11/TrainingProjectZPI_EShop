package eshop.su.ciselnik.md;

import java.awt.Color;
import java.awt.Insets;

import eshop.bo.ciselniky.TovarDruh;
import eshop.su.ciselnik.uc.UCTovarDruh;
import netball.server.component.XBorderPanel;
import netball.server.component.XBoxPanel;
import netball.server.component.XButton;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XForm;
import netball.server.component.XPanel;
import netball.server.component.border.XTitleBorder;
import netball.server.component.table.BasicTableCellColorModel;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.pack.FormPack;
import netball.server.pack.ServerPack;
import netframework.access.AccessAction;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDUtilities;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.resource.MediatorResourceBuilder;

public class MDTovarDruh extends BasicMediator {
	
	private final String CANCEL_ACTION = createId(MediatorResourceBuilder.CANCEL_BUTTON);
	private final String OK_ACTION = createId(MediatorResourceBuilder.OK_BUTTON);
	
    private UCTovarDruh tovarDruh;
    private boolean isNew;
        
    @Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
    	if (parameters == null) {
            this.tovarDruh = UCTovarDruh.create((EclipseLinkSession) this.getSessionObject());
            isNew = true;
        }else if (parameters instanceof Parameters) {
            Parameters prmts = (Parameters) parameters;    
            this.tovarDruh = UCTovarDruh.read(prmts.tovarDruhId, (EclipseLinkSession) this.getSessionObject());
        }else {
            throw new IllegalArgumentException("Chybny parameter!"); 
        }
    	putFormToPack(pack);
    }    
    
    @Override
    public AccessAction[] getAccessActions() {      
        return null;
    }     
    
    private void putFormToPack(ServerPack serverPack){
         XBoxPanel buttonPanel = new XBoxPanel(javax.swing.SwingConstants.HORIZONTAL);
         buttonPanel.setSameSizeForAllComponents(true);
         buttonPanel.setGapForAll(5);
         buttonPanel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);    
                  
         ServerActionEvent event = new ServerActionEvent();
         event.setReturnAllValues(true);
         buttonPanel.add(MediatorResourceBuilder.createButton(OK_ACTION, MediatorResourceBuilder.OK_BUTTON, event, MDUtilities.OK_ICON, getSessionObject()));
         buttonPanel.add(MediatorResourceBuilder.createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, new ServerActionEvent(), MDUtilities.HOME_ICON, getSessionObject()));
             
          XBorderPanel panel = new XBorderPanel(10, 10);
          panel.setInsets(new Insets(10,10,10,10)); 
          panel.setCenter(this.createAtributyPanel());
          panel.setSouth(buttonPanel);
           
          XForm form = new XForm();
          form.setTitle("Druh tovaru");
          form.setPanel(panel);
          form.setHotButton(OK_ACTION);
             
          FormPack formPack = new FormPack(getId(), form);        
          formPack.setValuePack(tovarDruh.getValuePack());
          formPack.setRequiredPack(tovarDruh.getRequiredPack());
          formPack.setEnabledPack(tovarDruh.getEnabledPack());
          serverPack.addFormPack(formPack);
    }       
    
    private XPanel createAtributyPanel() {
        XDualComponentPanel panel = new XDualComponentPanel();
        panel.setInsets(new Insets(10,10,10,10));
        panel.setBorder(new XTitleBorder());
        panel.add(ComponentBuilder.createComponent(UCTovarDruh.KOD, TovarDruh.KOD, getSessionObject()));
        panel.add(ComponentBuilder.createComponent(UCTovarDruh.NAZOV, TovarDruh.NAZOV, getSessionObject()));
        
        //panel.setBackground(Color.YELLOW);   
        return panel;
    }

    @Override
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
        if (event.getSourceId().equals(CANCEL_ACTION)) {
            close(pack);
        }else if (event.getSourceId().equals(OK_ACTION)) {
        	try {
        	    tovarDruh.execute(event.getValuePack());
        	    sendCallBack(new CallBack(tovarDruh.getId(), isNew), pack);
        	    close(pack);
        	}catch(Exception e) {
        		this.addExceptionToPack(e, pack);
        	}
        }     
    }     

	public static class Parameters implements MediatorParameters {
        public Object tovarDruhId;
            
        public Parameters (Object tovarDruhId) {
            this.tovarDruhId = tovarDruhId;
        }
    }
    
	public static class CallBack implements MediatorCallBackObject{
        
		public Object tovarDruhId;
		public boolean isNew;
    
    	public CallBack(Object tovarDruhId, boolean isNew) {
    		this.tovarDruhId = tovarDruhId;
    		this.isNew = isNew;
    	} 
	}
	
}
