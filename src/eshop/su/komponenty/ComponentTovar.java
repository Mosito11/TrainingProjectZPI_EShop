package eshop.su.komponenty;

import eshop.su.common.view.ViewTovar;
import netball.server.component.XCompoundField;
import netframework.mediator.BasicMediator;
import netframework.mediator.ViewCompoundFieldWithClientServerTable;
import netframework.view.View;

public class ComponentTovar extends ViewCompoundFieldWithClientServerTable {

	public ComponentTovar(Object componentId, BasicMediator mediator) {
		super(componentId, mediator);
		// ostalo rovnake zo superclass
	}

	@Override
	protected String[] getColumns() {
		return new String[] {ViewTovar.KOD, ViewTovar.NAZOV};
	}

	@Override
	protected View getView() {
		return new ViewTovar();
	}
	
	@Override
	protected String getTitleText() {
		return "Tovary";
	}

	//toto neviem, aky je rozdiel medzi tym
	public static XCompoundField createComponent(Object componentId, BasicMediator mediator) {
		return new ComponentTovar(componentId, mediator).createComponent();
	}
	
	public static XCompoundField createExpressionComponent(Object componentId, BasicMediator mediator) {
		return new ComponentTovar(componentId, mediator).createExpressionComponent();
	}
	
}
