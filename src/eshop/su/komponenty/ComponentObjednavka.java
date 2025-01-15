package eshop.su.komponenty;

import eshop.bo.ciselniky.Objednavka;
import eshop.su.ciselnik.md.MDVyberObjednavka;
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

public class ComponentObjednavka implements PopupEventHandler {

	private Object componentId;
	private Object mediatorId;
	
	private ComponentObjednavka(Object componentId) {
	   this.componentId = componentId; 
	}
	
	public static XCompoundField createComponent(Object componentId, BasicMediator mediator) {
		
		ComponentObjednavka objednavka = new ComponentObjednavka(componentId);
		//ObchodnyPartner.KOD namiesto Zakaznik.ICO a nechapem ani tieto riadky tu
	    XComponent editor = ComponentBuilder.createFilterComponent(null, Objednavka.CISLO_OBJEDNAVKY, mediator.getSessionObject());        
	    XCompoundField field = new XCompoundField(componentId, mediator.translateText(Objednavka.CISLO_OBJEDNAVKY.getLongCaption()));
	    field.setEditor(editor);
	    field.setEditable(false);
	    field.addPopupEvent(new ServerPopupEvent());
	    mediator.registerHandler(componentId, objednavka);      
	    return field;
	   }
	
	public static XCompoundField createExpressionComponent(Object componentId, BasicMediator mediator) {
		//ObchodnyPartner.KOD namiesto Zakaznik.ICO
		XCompoundField field = createComponent(componentId, mediator);
		field.setEditor(ComponentBuilder.createExpressionComponent(componentId, Objednavka.CISLO_OBJEDNAVKY, mediator.getSessionObject()));
		field.setWidth(field.getEditor().getWidth()); 
		return field;
	   }
	
	@Override
	public void handleEvent(ClientPopupEvent event, ServerPack pack, BasicMediator mediator) {
		
				if (mediatorId == null) {
					mediatorId = mediator.runNext(MDVyberObjednavka.class, null, pack, BasicMediator.HIDE_ON_CLOSE);
		            mediator.getMediator(mediatorId).registerCallBackHandler(new CallBack());
			     }else{
			    	mediator.show(mediatorId, pack); 
			     }
	}
	
	private class CallBack implements CallBackHandler {
	    
		@Override
		public void receiveCallBack(BasicMediator mediator,	MediatorCallBackObject callBackObject, ServerPack pack) {
			MDVyberObjednavka.CallBack callBack = (MDVyberObjednavka.CallBack) callBackObject;
			ValuePack valuePack = new ValuePack();
			valuePack.put(componentId, callBack.cisloObjednavky);
			pack.addUpdatedPack(new UpdatedPack(mediator.getParent().getId(), valuePack));
	   }
	}


}
