package eshop.su.ciselnik.md;

import netball.server.event.ClientActionEvent;
import netball.server.event.ClientWindowEvent;
import netball.server.event.ServerWindowEvent;
import netball.server.pack.ServerPack;
import netframework.mediator.MediatorParameters;

public class MDVyberZakaznik extends MDVyberObchodnyPartner {
	
	@Override
	public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
		super.init(parameters, pack);
	}
	
	@Override
	protected String getTitleText() {
		return "Zakaznici";
	}
	
	@Override
	   public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
	       try{
	           if(event.getSourceId().equals(POTVRD_ACTION)){
	   			   sendCallBack(event, pack);
	           }else if (event.getSourceId().equals(VYBER_ACTION)) {
	        	   
	           	   this.runNext(MDFilterZakaznik.class, this.getFilterParameters(), pack);
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
		
			this.runNext(MDFilterZakaznik.class, this.getFilterParameters(), pack);		
		}
		super.windowEventExecuted(event, pack);
	}

}
