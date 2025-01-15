package eshop.su.komponenty;

import eshop.bo.ciselniky.ObchodnyPartner;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.ciselnik.md.MDVyberObchodnyPartner;
import eshop.su.ciselnik.md.MDVyberZakaznik;
import netball.server.component.XComponent;
import netball.server.component.XCompoundField;
import netball.server.event.ClientPopupEvent;
import netball.server.event.ServerPopupEvent;
import netball.server.pack.ServerPack;
import netball.server.pack.UpdatedPack;
import netball.server.pack.ValuePack;
import netframework.mediator.BasicMediator;
import netframework.mediator.CallBackHandler;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MediatorCallBackObject;
import netframework.mediator.handler.PopupEventHandler;

public class ComponentZakaznik implements PopupEventHandler {

	private Object componentId;
	private Object mediatorId;
	
	private ComponentZakaznik(Object componentId) {
	   this.componentId = componentId; 
	}
	
	public static XCompoundField createComponent(Object componentId, BasicMediator mediator) {
		
		ComponentZakaznik zakaznik = new ComponentZakaznik(componentId);
		//ObchodnyPartner.KOD namiesto Zakaznik.ICO a nechapem ani tieto riadky tu
	    XComponent editor = ComponentBuilder.createFilterComponent(null, Zakaznik.KOD, mediator.getSessionObject());        
	    XCompoundField field = new XCompoundField(componentId, mediator.translateText(Zakaznik.KOD.getLongCaption()));
	    field.setEditor(editor);
	    field.setEditable(false);
	    field.addPopupEvent(new ServerPopupEvent());
	    mediator.registerHandler(componentId, zakaznik);      
	    return field;
	   }
	
	public static XCompoundField createExpressionComponent(Object componentId, BasicMediator mediator) {
		//ObchodnyPartner.KOD namiesto Zakaznik.ICO
		XCompoundField field = createComponent(componentId, mediator);
		field.setEditor(ComponentBuilder.createExpressionComponent(componentId, Zakaznik.KOD, mediator.getSessionObject()));
		field.setWidth(field.getEditor().getWidth()); 
		return field;
	   }
	
	@Override
	public void handleEvent(ClientPopupEvent event, ServerPack pack, BasicMediator mediator) {
		
				if (mediatorId == null) {
					mediatorId = mediator.runNext(MDVyberZakaznik.class, null, pack, BasicMediator.HIDE_ON_CLOSE);
		            mediator.getMediator(mediatorId).registerCallBackHandler(new CallBack());
			     }else{
			    	mediator.show(mediatorId, pack); 
			     }
	}
	
	private class CallBack implements CallBackHandler {
	    
		@Override
		public void receiveCallBack(BasicMediator mediator,	MediatorCallBackObject callBackObject, ServerPack pack) {
			MDVyberZakaznik.CallBack callBack = (MDVyberZakaznik.CallBack) callBackObject;
			ValuePack valuePack = new ValuePack();
			valuePack.put(componentId, callBack.kod);
			pack.addUpdatedPack(new UpdatedPack(mediator.getParent().getId(), valuePack));
	   }
	}

}
