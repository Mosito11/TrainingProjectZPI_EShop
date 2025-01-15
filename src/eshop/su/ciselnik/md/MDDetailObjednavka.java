package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.su.common.MDHelper;
import eshop.su.db.DBCatalog;
import netball.server.component.XBorderPanel;
import netball.server.component.XBoxPanel;
import netball.server.component.XClientTable;
import netball.server.component.XDualComponentPanel;
import netball.server.component.XForm;
import netball.server.component.XPanel;
import netball.server.component.XTabbedPage;
import netball.server.component.XTabbedPane;
import netball.server.component.table.TableContainer;
import netball.server.event.ClientActionEvent;
import netball.server.event.ServerActionEvent;
import netball.server.pack.FormPack;
import netball.server.pack.ServerPack;
import netball.server.pack.ValuePack;
import netframework.FrameworkUtilities;
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
import zelpo.eclipselink.autorizacia.Uzivatel;

public class MDDetailObjednavka extends BasicMediator {

	//nastavim buttony, v detaile mi staci Cancel, tj. navrat
	private final String CANCEL_ACTION = createId(MediatorResourceBuilder.CANCEL_BUTTON);
	//este neviem, na co pouzijem
    private Object objednavkaId;
    //tabulka polozky?
    private final String TABLE_POLOZKY = "tablePolozky";
    //dalsi button na rozklik do detailov polozky pravdepodobne
    private final String DETAIL_POLOZKY_ACTION = "detailPolozkyAction";
	
    //vytahujem id objednavky z parametrov, ktore pridu v mediatore
    @Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
        if (parameters instanceof Parameters) {
           Parameters prmts = (Parameters) parameters;    
           objednavkaId = prmts.objednavkaId;
        }else {
           throw new IllegalArgumentException("Chybny parameter !"); 
        }
        this.putFormToPack(pack);
    } 
    
	@Override
	public AccessAction[] getAccessActions() {
		return null;
	}

	//toto DBSkolenieCatalog na co sa pouziva? tabulka z viacerych DB v jednej?
	private ValuePack read() throws Exception{
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA)); 
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.ZAKAZNIK, c.OBCHODNY_PARTNER.ID));
        //tu bol ten WARNING pri vytvarani column, treba to obist, cez join a vytiahnut si udaje, co potrebujeme
        query.addTable(new SQLJoinCondition(c.UZIVATEL, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.VYSTAVIL, c.UZIVATEL.ID));
        query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        query.addField(Objednavka.DATUM.getId(), c.OBJEDNAVKA.DATUM);
        //otazka, preco nemozem vkladat do cursora ConstantAttribute?
        query.addField(Objednavka.STAV_OBJEDNAVKY.getId(), c.OBJEDNAVKA.STAV_OBJEDNAVKY);
        query.addField(Objednavka.SUMA.getId(), c.OBJEDNAVKA.SUMA);
        query.addField(Zakaznik.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
        query.addField(Uzivatel.MENO.getId(), c.UZIVATEL.MENO);
        query.setExpression(SQLExpressionBuilder.get(c.OBJEDNAVKA.ID).equal(this.objednavkaId));
        ViewCursor cursor = ((EclipseLinkSession) getSessionObject()).execute(query);
        
        if(!cursor.hasNext())
            throw new IllegalArgumentException("Nepodarilo sa nacitat zaznam id = " + objednavkaId + " !");
        ViewRow row = cursor.next();
        ValuePack valuePack = new ValuePack();
        for(int i = 0; i < row.getSize(); i++){
            valuePack.put(row.getColumnName(i), row.getValueAt(i));
        }
        return valuePack;
    }
	
	protected XPanel createAtributyPanel() throws Exception{
        XDualComponentPanel panel = new XDualComponentPanel();
        panel.setInsets(new Insets(10,10,10,10));
        panel.add(ComponentBuilder.createReadOnlyComponent(Objednavka.CISLO_OBJEDNAVKY.getId(), Objednavka.CISLO_OBJEDNAVKY, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Zakaznik.NAZOV.getId(), Zakaznik.NAZOV, Objednavka.ZAKAZNIK.getCaption(), getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Objednavka.SUMA.getId(), Objednavka.SUMA, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Objednavka.STAV_OBJEDNAVKY.getId(), Objednavka.STAV_OBJEDNAVKY, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Objednavka.DATUM.getId(), Objednavka.DATUM, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Uzivatel.MENO.getId(), Uzivatel.MENO, getSessionObject()));
        return panel;
    }
	
	protected XPanel createPolozkyPanel() throws Exception {        
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA));
        query.addTable(new SQLJoinCondition(c.TOVAR, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA_POLOZKA.TOVAR, c.TOVAR.ID));
        
        query.addField(ObjednavkaPolozka.ID.getId(), c.OBJEDNAVKA_POLOZKA.ID);
        query.addField(Tovar.ID.getId(), c.TOVAR.ID);
        query.addField(ObjednavkaPolozka.MNOZSTVO.getId(), c.OBJEDNAVKA_POLOZKA.MNOZSTVO);
        query.addField(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), c.OBJEDNAVKA_POLOZKA.JEDNOTKOVA_CENA);
        query.addField(ObjednavkaPolozka.SUMA.getId(), c.OBJEDNAVKA_POLOZKA.SUMA);
        query.addOrdering(c.OBJEDNAVKA_POLOZKA.TOVAR);
        query.setExpression(SQLExpressionBuilder.get(c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA).equal(objednavkaId));
        ViewCursor cursor = ((EclipseLinkSession) getSessionObject()).execute(query);
        TableContainer container = cursor.readToContainer();        
        
        XClientTable table = new XClientTable(TABLE_POLOZKY);
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.ID.getId(), ObjednavkaPolozka.ID, getSessionObject()));
        table.addColumn(ComponentBuilder.createTableColumn(Tovar.ID.getId(), Tovar.ID, Tovar.ID.getCaption(), getSessionObject())); 
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.MNOZSTVO.getId(), ObjednavkaPolozka.MNOZSTVO, getSessionObject()));
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), ObjednavkaPolozka.JEDNOTKOVA_CENA, getSessionObject()));
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.SUMA.getId(), ObjednavkaPolozka.SUMA, getSessionObject()));
        table.getColumn(ObjednavkaPolozka.ID.getId()).setVisible(false);
        
        table.setPrimaryKey(ObjednavkaPolozka.ID.getId());
        table.setDataSource(container);
        table.setWidth(600);
        
        XBoxPanel buttonPanel = new XBoxPanel(javax.swing.SwingConstants.HORIZONTAL);                
        buttonPanel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);       
        ServerActionEvent event = new ServerActionEvent();
        event.addReturnValue(TABLE_POLOZKY);
        buttonPanel.add(MDUtilities.createButton(DETAIL_POLOZKY_ACTION, MDHelper.DETAIL_BUTTON, event, getSessionObject()));             
        
        XBorderPanel panel = new XBorderPanel(10, 10);
        panel.setInsets(new Insets(10,10,10,10)); 
        panel.setCenter(table);
        panel.setSouth(buttonPanel);
        return panel;
    }

	public void putFormToPack(ServerPack serverPack) throws Exception {
        XBoxPanel buttonPanel = new XBoxPanel(javax.swing.SwingConstants.HORIZONTAL);                
        buttonPanel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);            
        buttonPanel.add(MediatorResourceBuilder.createButton(CANCEL_ACTION, MediatorResourceBuilder.CANCEL_BUTTON, new ServerActionEvent(), MDUtilities.HOME_ICON, getSessionObject()));

        XTabbedPane tabbedPanel = new XTabbedPane("zalozka");
        tabbedPanel.add(new XTabbedPage("zakladneUdaje", "Zakl. udaje", createAtributyPanel()));
        tabbedPanel.add(new XTabbedPage("polozky", "Polozky", createPolozkyPanel()));
                
        XBorderPanel mainPanel = new XBorderPanel(10, 10);
        mainPanel.setInsets(new Insets(10,10,10,10)); 
        mainPanel.setCenter(tabbedPanel);
        mainPanel.setSouth(buttonPanel);     
      
        XForm form = new XForm();
        form.setTitle(getSessionObject().translateText("Objednavka"));
        form.setPanel(mainPanel);
        
        FormPack formPack = new FormPack(getId(), form); 
        formPack.setValuePack(read());
        serverPack.addFormPack(formPack);
	}
	
	@Override
	public void actionEventExecuted(ClientActionEvent event, ServerPack pack) {
        if (event.getSourceId().equals(CANCEL_ACTION)) {
           close(pack);
        }else if (event.getSourceId().equals(DETAIL_POLOZKY_ACTION)) {
           Object id = FrameworkUtilities.getSelectedPrimaryKeyFromEvent(event, pack, TABLE_POLOZKY, getSessionObject());
           if (id != null) {
        	   runNext(MDDetailObjednavkaPolozka.class, new MDDetailObjednavkaPolozka.Parameter(id), pack);
           }
        }
        
    }
	
	public static class Parameters implements MediatorParameters {
	    public Object objednavkaId;
	    
	    public Parameters (Object objednavkaId) {
	        this.objednavkaId = objednavkaId;
	    }
	}
	
}
