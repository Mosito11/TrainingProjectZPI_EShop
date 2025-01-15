package eshop.su.ciselnik.md;

import javax.swing.SwingConstants;

import eshop.su.ciselnik.uc.UCAdresa;
import eshop.su.ciselnik.uc.UCTovarDruh;
import eshop.su.common.view.ViewAdresa;
import netball.server.component.XBoxPanel;
import netball.server.component.XPanel;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.event.alert.YesNoAlert;
import netball.server.pack.EnabledPack;
import netball.server.pack.ServerPack;
import netframework.access.AccessAction;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.MDTablePreview;
import netframework.mediator.MDUtilities;
import netframework.mediator.MDViewBasicClientTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;
import netframework.mediator.resource.MediatorResourceBuilder;

public class MDViewAdresa extends MDViewBasicClientTable {

	private final String PRIDAJ_ACTION = createId(MediatorResourceBuilder.ADD_BUTTON);
    private final String OPRAVA_ACTION = createId(MediatorResourceBuilder.CORRECT_BUTTON);
    private final String VYMAZ_ACTION = createId(MediatorResourceBuilder.DELETE_BUTTON);
    private final String TLAC_ACTION = createId(MediatorResourceBuilder.PRINT_BUTTON);
    private final String VYBER_ACTION = createId(MediatorResourceBuilder.READ_BUTTON);
	
    @Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
		read(pack, null, null, false);
	}

	@Override
	public AccessAction[] getAccessActions() {
		return new AccessAction[]{
	               getAccessAction(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON),
	               getAccessAction(OPRAVA_ACTION, MediatorResourceBuilder.CORRECT_BUTTON),
	               getAccessAction(VYMAZ_ACTION, MediatorResourceBuilder.DELETE_BUTTON),
	         };
	}
    
    private AccessAction getAccessAction(String actionCode, String buttonCode) {
    	return MediatorResourceBuilder.createAccessAction(actionCode, buttonCode, getLocale());
    }
    
	@Override
	protected ViewTableDataSource createDataSource() {
		String[] columns = new String[] {ViewAdresa.ID,
				ViewAdresa.ULICA,
				ViewAdresa.CISLO,
				ViewAdresa.MESTO,
				ViewAdresa.PSC};
		return new ViewTableDataSource(new ViewAdresa(), columns, ViewAdresa.ID, ViewAdresa.PSC);
	}

	@Override
	protected String getTitleText() {
		// TODO Auto-generated method stub
		return "Adresy";
	}

	@Override
	protected XPanel createEastPanel(ServerPack pack) {
    	XBoxPanel panel = new XBoxPanel(SwingConstants.VERTICAL);
    	panel.setSameSizeForAllComponents(true);
    	panel.setGapForAll(5);
    	
        panel.add(createButton(VYBER_ACTION, MediatorResourceBuilder.READ_BUTTON, MDUtilities.READ_ICON, new ServerActionEvent()));
        panel.add(createButton(PRIDAJ_ACTION, MediatorResourceBuilder.ADD_BUTTON, MDUtilities.ADD_ICON, new ServerActionEvent()));

    	ServerActionEvent event = new ServerActionEvent();
    	//TABLE je atribut z rodicovskej triedy MDViewBasicClientTable a vracia vsetky oznacene riadky
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

	@Override
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
    	super.actionEventExecuted(event, pack); 
        try {
        	if (event.getSourceId().equals(OPRAVA_ACTION)) {  
        		Object id = getSelectedPrimaryKeyFromEvent(event, pack);
        		if (id != null) {
        			runNext(MDAdresa.class, new MDAdresa.Parameters(id), pack);
        		}
        	}else if (event.getSourceId().equals(PRIDAJ_ACTION)) {
        		runNext(MDAdresa.class, null, pack);
        	}else if (event.getSourceId().equals(VYMAZ_ACTION)) {             
        	
        		Object id = getSelectedPrimaryKeyFromEvent(event, pack);
        		if (id != null) {
        			//stale mi chyba metoda na delete!
        			UCAdresa.delete(id, (EclipseLinkSession) getSessionObject());
        			this.addDeletedPack(id, pack);
        			this.addButtonEnabledPack(pack);
        		}        		
        	}else if (event.getSourceId().equals(VYBER_ACTION)) {        	  
        		this.read(pack, null, null);
        	
            }else if (event.getSourceId().equals(TLAC_ACTION)) {
        		runNext(MDTablePreview.class, new MDTablePreview.Parameters(TABLE, getId()), pack);
        	}    
        }catch(Exception e) {
        	addExceptionToPack(e, pack);
        }                           
    }    
	
	@Override
    protected boolean receiveCallBack(BasicMediator mediator,  MediatorCallBackObject obj, ServerPack pack) {
        if (obj instanceof MDAdresa.CallBack) {
        	MDAdresa.CallBack callBack = (MDAdresa.CallBack) obj;
        	if (callBack.isNew) {
        		addInsertedPack(callBack.adresaId, pack);          
        		addButtonEnabledPack(pack);
        	}else {
        		addUpdatedPack(callBack.adresaId, pack);          
        	} 
        }else {
        	return super.receiveCallBack(mediator, obj, pack);
        }  
        return false;        
    }   
	
	@Override
    public int getMaxRows() {
    	return 0;
    }
}
