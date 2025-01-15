package eshop.su.komponenty;

import eshop.su.common.view.ViewTovar;
import eshop.su.common.view.ViewTovarDruh;
import netball.server.component.XCompoundField;
import netframework.mediator.BasicMediator;
import netframework.mediator.ViewCompoundFieldWithClientServerTable;
import netframework.view.View;

public class ComponentTovarDruh extends ViewCompoundFieldWithClientServerTable {

	public ComponentTovarDruh(Object componentId, BasicMediator mediator) {
		super(componentId, mediator);
	}

	@Override
	protected String[] getColumns() {
		return new String[] {ViewTovarDruh.KOD, ViewTovarDruh.NAZOV};
	}

	@Override
	protected View getView() {
		return new ViewTovarDruh();
	}
	
	@Override
	protected String getTitleText() {
		return "Druhy tovaru";
	}

	//toto neviem, aky je rozdiel medzi tym
	public static XCompoundField createComponent(Object componentId, BasicMediator mediator) {
			return new ComponentTovarDruh(componentId, mediator).createComponent();
	}
		
	public static XCompoundField createExpressionComponent(Object componentId, BasicMediator mediator) {
			return new ComponentTovarDruh(componentId, mediator).createExpressionComponent();
	}

}
