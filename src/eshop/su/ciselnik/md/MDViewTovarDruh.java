package eshop.su.ciselnik.md;

import eshop.su.ciselnik.uc.UCTovarDruh;
import eshop.su.common.view.ViewTovarDruh;
import netball.server.event.ClientActionEvent;
import netball.server.pack.ServerPack;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.MDViewClientTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;

public class MDViewTovarDruh extends MDViewClientTable {

	@Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
		read(pack, null, null, false);
	}
	
	@Override
	protected ViewTableDataSource createDataSource() {
		String[] columns = new String[] {ViewTovarDruh.ID,
				ViewTovarDruh.KOD,
				ViewTovarDruh.NAZOV};
		return new ViewTableDataSource(new ViewTovarDruh(), columns, ViewTovarDruh.ID, ViewTovarDruh.KOD);
	}
	
	@Override
	protected void insert(ServerPack pack) throws Exception {
		MDTovarDruh.Parameters prmts = new MDTovarDruh.Parameters(UCTovarDruh.create((EclipseLinkSession) getSessionObject()).getId());
      	runNext(MDTovarDruh.class, null, pack);
	}
/*
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
    	super.actionEventExecuted(event, pack); 
        try {
        	if (event.getSourceId().equals(OPRAVA_ACTION)) {  
        		//funkcia na ziskanie ID z eventu
        		Object id = getSelectedPrimaryKeyFromEvent(event, pack);
        		if (id != null) {
        			runNext(MDTovarDruh.class, new MDTovarDruh.Parameters(id), pack);
        		}
        	}else if (event.getSourceId().equals(PRIDAJ_ACTION)) {
        		runNext(MDTovarDruh.class, null, pack);
        	}
*/	
	@Override
	protected void update(ServerPack pack, Object id) throws Exception {
		runNext(MDTovarDruh.class, new MDTovarDruh.Parameters(UCTovarDruh.read(id, (EclipseLinkSession) getSessionObject()).getId()), pack);
	}

	@Override
	protected void delete(Object id) throws Exception {
		UCTovarDruh.delete(id, (EclipseLinkSession) getSessionObject());
	}

	@Override
    protected boolean receiveCallBack(BasicMediator mediator,  MediatorCallBackObject obj, ServerPack pack) {
        if (obj instanceof MDTovarDruh.CallBack) {
        	MDTovarDruh.CallBack callBack = (MDTovarDruh.CallBack) obj;
        	if (callBack.isNew) {
        		addInsertedPack(callBack.tovarDruhId, pack);          
        		addButtonEnabledPack(pack);
        	}else {
        		addUpdatedPack(callBack.tovarDruhId, pack);          
        	} 
        }else {
        	return super.receiveCallBack(mediator, obj, pack);
        }  
        return false;        
    } 
	
	@Override
	protected String getTitleText() {
		return "Druhy tovaru";
	}
}
