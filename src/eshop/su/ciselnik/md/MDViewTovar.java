package eshop.su.ciselnik.md;

import eshop.su.ciselnik.uc.UCTovar;
import eshop.su.ciselnik.uc.UCTovarDruh;
import eshop.su.common.view.ViewTovar;
import eshop.su.common.view.ViewTovarDruh;
import netball.server.pack.ServerPack;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.MDViewClientTable;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;

public class MDViewTovar extends MDViewClientTable {

	@Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
		read(pack, null, null, false);
	}
	
	@Override
	protected ViewTableDataSource createDataSource() {
		String[] columns = new String[] {ViewTovar.ID,
				ViewTovar.KOD,
				ViewTovar.NAZOV,
				ViewTovar.TOVAR_DRUH_NAZOV, 
				ViewTovar.VELKOST,
				ViewTovar.SKLADOVA_ZASOBA,
				ViewTovar.DODAVATEL_NAZOV,
				};
		return new ViewTableDataSource(new ViewTovar(), columns, ViewTovar.ID, ViewTovar.KOD);
	}
	
	@Override
	protected void insert(ServerPack pack) throws Exception {
		MDTovar.Parameters prmts = new MDTovar.Parameters(UCTovar.create((EclipseLinkSession) getSessionObject()).getId());
      	runNext(MDTovar.class, null, pack);		
	}

	@Override
	protected void update(ServerPack pack, Object id) throws Exception {
		runNext(MDTovar.class, new MDTovar.Parameters(UCTovar.read(id, (EclipseLinkSession) getSessionObject()).getId()), pack);
	}

	protected void delete(Object id) throws Exception {
		UCTovar.delete(id, (EclipseLinkSession) getSessionObject());
	}

	@Override
    protected boolean receiveCallBack(BasicMediator mediator,  MediatorCallBackObject obj, ServerPack pack) {
        if (obj instanceof MDTovar.CallBack) {
        	MDTovar.CallBack callBack = (MDTovar.CallBack) obj;
        	if (callBack.isNew) {
        		addInsertedPack(callBack.tovarId, pack);          
        		addButtonEnabledPack(pack);
        	}else {
        		addUpdatedPack(callBack.tovarId, pack);          
        	} 
        }else {
        	return super.receiveCallBack(mediator, obj, pack);
        }  
        return false;        
    } 

	@Override
	protected String getTitleText() {
		return "Tovary";
	}

}
