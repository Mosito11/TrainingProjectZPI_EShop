package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.TovarDruh;
import eshop.su.db.DBCatalog;
import netball.server.component.XBorderPanel;
import netball.server.component.XBoxPanel;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XForm;
import netball.server.component.XPanel;
import netball.server.component.border.XTitleBorder;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.pack.FormPack;
import netball.server.pack.ServerPack;
import netball.server.pack.ValuePack;
import netframework.access.AccessAction;
import netframework.eclipselink.EclipseLinkSession;
import netframework.mediator.BasicMediator;
import netframework.mediator.ComponentBuilder;
import netframework.mediator.MDUtilities;
import netframework.mediator.MediatorParameters;
import netframework.mediator.resource.MediatorResourceBuilder;
import netframework.sql.SQLExpressionBuilder;
import netframework.sql.SQLJoinCondition;
import netframework.sql.SQLQuery;
import netframework.view.ViewCursor;
import netframework.view.ViewRow;

public class MDDetailObjednavkaPolozka extends BasicMediator {
	
	private Object id;
    private final String CANCEL_ACTION = createId(MediatorResourceBuilder.CANCEL_BUTTON);

    @Override
	public void init(MediatorParameters prmt, ServerPack pack) throws Exception {
		id = ((Parameter) prmt).id;
		putFormToPack(pack);
	}
	
	@Override
	public AccessAction[] getAccessActions() {
		return null;
	}
	
	protected XPanel createFormPanel() {
        XDualComponentPanel panel = new XDualComponentPanel();
        panel.setBorder(new XTitleBorder());
        panel.setInsets(new Insets(10,10,10,10));
        //System.out.println("Toto je Tovar.NAZOV.getId() " + Tovar.NAZOV.getId());
        //System.out.println("Toto je Tovar.NAZOV " + Tovar.NAZOV);
        panel.add(ComponentBuilder.createReadOnlyComponent(Tovar.NAZOV.getId(), Tovar.NAZOV, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(TovarDruh.NAZOV.getId(), TovarDruh.NAZOV, TovarDruh.NAZOV.getCaption(), getSessionObject()));;
        panel.add(ComponentBuilder.createReadOnlyComponent(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), ObjednavkaPolozka.JEDNOTKOVA_CENA, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(ObjednavkaPolozka.MNOZSTVO.getId(), ObjednavkaPolozka.MNOZSTVO, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(ObjednavkaPolozka.SUMA.getId(), ObjednavkaPolozka.SUMA, getSessionObject()));
        return panel; 
	}
	
	private ValuePack read() throws Exception {
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA));
        query.addTable(new SQLJoinCondition(c.TOVAR, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA_POLOZKA.TOVAR, c.TOVAR.ID));
        query.addTable(new SQLJoinCondition(c.TOVAR_DRUH, SQLJoinCondition.LEFT_OUTER_JOIN, c.TOVAR.TOVAR_DRUH, c.TOVAR_DRUH.ID));
        
        query.addField(Tovar.NAZOV.getId(), c.TOVAR.NAZOV);
        query.addField(TovarDruh.NAZOV.getId(), c.TOVAR_DRUH.NAZOV);
        query.addField(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), c.OBJEDNAVKA_POLOZKA.JEDNOTKOVA_CENA);
        query.addField(ObjednavkaPolozka.MNOZSTVO.getId(), c.OBJEDNAVKA_POLOZKA.MNOZSTVO);
        query.addField(ObjednavkaPolozka.SUMA.getId(), c.OBJEDNAVKA_POLOZKA.SUMA);
        query.addOrdering(c.TOVAR.NAZOV);
        query.setExpression(SQLExpressionBuilder.get(c.OBJEDNAVKA_POLOZKA.ID).equal(id));
        
        ViewCursor cursor = ((EclipseLinkSession) getSessionObject()).execute(query);
        if(!cursor.hasNext())
            throw new IllegalArgumentException("Nepodarilo sa nacitat zaznam id = " + id + " !");
        ViewRow row = cursor.next();
        ValuePack valuePack = new ValuePack();
        for(int i = 0; i < row.getSize(); i++){
            valuePack.put(row.getColumnName(i), row.getValueAt(i));
        }
        return valuePack;
	}

	public void putFormToPack(ServerPack serverPack) throws Exception {
        XBoxPanel buttonPanel = new XBoxPanel(javax.swing.SwingConstants.HORIZONTAL);                
        buttonPanel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);            
        buttonPanel.add(MediatorResourceBuilder.createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, new ServerActionEvent(), MDUtilities.HOME_ICON, getSessionObject()));

        XBorderPanel mainPanel = new XBorderPanel(10, 10);
        mainPanel.setInsets(new Insets(10,10,10,10)); 
        mainPanel.setCenter(createFormPanel());
        mainPanel.setSouth(buttonPanel);     
          
        XForm form = new XForm();
        form.setTitle(getSessionObject().translateText("Polozka objednavky"));
        form.setPanel(mainPanel);
            
        FormPack formPack = new FormPack(getId(), form);        
        formPack.setValuePack(read());
        serverPack.addFormPack(formPack);
    }  
	
	@Override
	 public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
        if (event.getSourceId().equals(CANCEL_ACTION)) {
           close(pack);
        }
    }
	
	public static class Parameter implements MediatorParameters {
		public Object id;
		
		public Parameter(Object id) {
			this.id = id;
		}
	}

}
