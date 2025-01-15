package eshop.su.ciselnik.md;

import eshop.su.common.view.ViewObchodnyPartner;
import netball.server.event.ClientActionEvent;
import netball.server.event.ClientWindowEvent;
import netball.server.event.ServerWindowEvent;
import netball.server.pack.ServerPack;
import netframework.mediator.MediatorParameters;
import netframework.mediator.ViewTableDataSource;

public class MDVyberDodavatel extends MDVyberObchodnyPartner {
	
	@Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
	}
	
	@Override
	protected ViewTableDataSource createDataSource() {
		
		String[] cols = new String[] {	
			    ViewObchodnyPartner.ID,
			    ViewObchodnyPartner.KOD,
			    ViewObchodnyPartner.NAZOV,
			    ViewObchodnyPartner.ICO,			    
			    };
		return new ViewTableDataSource(new ViewObchodnyPartner(), cols, ViewObchodnyPartner.ID, ViewObchodnyPartner.KOD);
	}
	
	@Override
	protected String getTitleText() {
		return "Dodavatelia";
	}
	
	@Override
	   public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
	       try{
	           if(event.getSourceId().equals(POTVRD_ACTION)){
	   			   sendCallBack(event, pack);
	           }else if (event.getSourceId().equals(VYBER_ACTION)) {
	        	   
	           	   this.runNext(MDFilterDodavatel.class, this.getFilterParameters(), pack);
	           }else{
	        	   super.actionEventExecuted(event, pack);
	           }
	       }catch(Exception e){
	           addExceptionToPack(e, pack);
	       }
	   	}
	
	@Override
	public void windowEventExecuted(ClientWindowEvent event, ServerPack pack) {
		if (event.getCode() == ServerWindowEvent.WINDOW_OPENED_EVENT) {
			
			this.runNext(MDFilterDodavatel.class, this.getFilterParameters(), pack);
		}
		super.windowEventExecuted(event, pack);
	}

}
