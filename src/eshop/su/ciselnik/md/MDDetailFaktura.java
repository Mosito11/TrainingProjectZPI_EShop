package eshop.su.ciselnik.md;

import java.awt.Insets;

import eshop.bo.ciselniky.Faktura;
import eshop.bo.ciselniky.Objednavka;
import eshop.bo.ciselniky.ObjednavkaPolozka;
import eshop.bo.ciselniky.Tovar;
import eshop.bo.ciselniky.Zakaznik;
import eshop.bo.common.Helper;
import eshop.su.ciselnik.md.MDDetailObjednavka.Parameters;
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

public class MDDetailFaktura extends BasicMediator {

	private final String CANCEL_ACTION = createId(MediatorResourceBuilder.CANCEL_BUTTON);
	
	private Object fakturaId;
	private final String TABLE_POLOZKY = "tablePolozky";
	private final String DETAIL_POLOZKY_ACTION = "detailPolozkyAction";
		
	@Override
    public void init(MediatorParameters parameters, ServerPack pack) throws Exception {
        if (parameters instanceof Parameters) {
           Parameters prmts = (Parameters) parameters;    
           fakturaId = prmts.fakturaId;
        }else {
           throw new IllegalArgumentException("Chybny parameter !"); 
        }
        this.putFormToPack(pack);
    }
	
	@Override
	public AccessAction[] getAccessActions() {
		return null;
	}

	private ValuePack read() throws Exception{
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        query.addTable(new SQLJoinCondition(c.FAKTURA)); 
        
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA, SQLJoinCondition.LEFT_OUTER_JOIN, c.FAKTURA.OBJEDNAVKA, c.OBJEDNAVKA.ID));
        query.addTable(new SQLJoinCondition(c.OBCHODNY_PARTNER, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.ZAKAZNIK, c.OBCHODNY_PARTNER.ID));
        //tu bol ten WARNING pri vytvarani column, treba to obist, cez join a vytiahnut si udaje, co potrebujeme
        query.addTable(new SQLJoinCondition(c.UZIVATEL, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA.VYSTAVIL, c.UZIVATEL.ID));
        
        query.addField(Faktura.CISLO_FAKTURY.getId(), c.FAKTURA.CISLO_FAKTURY);
        query.addField(Faktura.DATUM_DODANIA.getId(), c.FAKTURA.DATUM_DODANIA);
        query.addField(Faktura.DATUM_VYSTAVENIA.getId(), c.FAKTURA.DATUM_VYSTAVENIA);
        query.addField(Faktura.DATUM_SPLATNOSTI.getId(), c.FAKTURA.DATUM_SPLATNOSTI);
        
        
        query.addField(Objednavka.CISLO_OBJEDNAVKY.getId(), c.OBJEDNAVKA.CISLO_OBJEDNAVKY);
        query.addField(Objednavka.DATUM.getId(), c.OBJEDNAVKA.DATUM);
        //otazka, preco nemozem vkladat do cursora ConstantAttribute? - mozem, chyba bola inde
        //query.addField(Objednavka.STAV_OBJEDNAVKY.getId(), c.OBJEDNAVKA.STAV_OBJEDNAVKY);
        query.addField(Objednavka.SUMA.getId(), c.OBJEDNAVKA.SUMA);
        query.addField(Zakaznik.NAZOV.getId(), c.OBCHODNY_PARTNER.NAZOV);
        query.addField(Uzivatel.MENO.getId(), c.UZIVATEL.MENO);
        query.setExpression(SQLExpressionBuilder.get(c.FAKTURA.ID).equal(this.fakturaId));
        ViewCursor cursor = ((EclipseLinkSession) getSessionObject()).execute(query);
        
        if(!cursor.hasNext())
            throw new IllegalArgumentException("Nepodarilo sa nacitat zaznam id = " + fakturaId + " !");
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
        panel.add(ComponentBuilder.createReadOnlyComponent(Faktura.CISLO_FAKTURY.getId(), Faktura.CISLO_FAKTURY, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Zakaznik.NAZOV.getId(), Zakaznik.NAZOV, Objednavka.ZAKAZNIK.getCaption(), getSessionObject()));
        
        panel.add(ComponentBuilder.createReadOnlyComponent(Objednavka.CISLO_OBJEDNAVKY.getId(), Objednavka.CISLO_OBJEDNAVKY, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Objednavka.SUMA.getId(), Objednavka.SUMA, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Objednavka.DATUM.getId(), Objednavka.DATUM, getSessionObject()));
        panel.add(ComponentBuilder.createReadOnlyComponent(Uzivatel.MENO.getId(), Uzivatel.MENO, getSessionObject()));
        return panel;
    }
	
	protected XPanel createPolozkyPanel() throws Exception {        
    	DBCatalog c = DBCatalog.getInstance();
        SQLQuery query = new SQLQuery();
        
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA));
        
        query.addTable(new SQLJoinCondition(c.FAKTURA, SQLJoinCondition.JOIN, c.FAKTURA.OBJEDNAVKA, c.OBJEDNAVKA.ID));
        query.addTable(new SQLJoinCondition(c.OBJEDNAVKA_POLOZKA, SQLJoinCondition.JOIN, c.OBJEDNAVKA.ID, c.OBJEDNAVKA_POLOZKA.OBJEDNAVKA));
        //FULL OUTER JOIN md1_objednavka ON (md1_objednavka_polozka.objednavka_id=md1_objednavka.id)
        
        query.addTable(new SQLJoinCondition(c.TOVAR, SQLJoinCondition.LEFT_OUTER_JOIN, c.OBJEDNAVKA_POLOZKA.TOVAR, c.TOVAR.ID));
        
        query.addField(ObjednavkaPolozka.ID.getId(), c.OBJEDNAVKA_POLOZKA.ID);
        //query.addField(Tovar.ID.getId(), c.TOVAR.ID);
        query.addField(Tovar.NAZOV.getId(), c.TOVAR.NAZOV);
        
        query.addField(ObjednavkaPolozka.MNOZSTVO.getId(), c.OBJEDNAVKA_POLOZKA.MNOZSTVO);
        query.addField(ObjednavkaPolozka.JEDNOTKOVA_CENA.getId(), c.OBJEDNAVKA_POLOZKA.JEDNOTKOVA_CENA);
        query.addField(ObjednavkaPolozka.SUMA.getId(), c.OBJEDNAVKA_POLOZKA.SUMA);
        query.addOrdering(c.OBJEDNAVKA_POLOZKA.TOVAR);
                      
        //ako parameter si posielam fakturaId, musim z toho nejako vytiahnut objednavkaId - cez joiny a spravnu formulaciu query      
        query.setExpression(SQLExpressionBuilder.get(c.FAKTURA.ID).equal(fakturaId));
                
        ViewCursor cursor = ((EclipseLinkSession) getSessionObject()).execute(query);
        TableContainer container = cursor.readToContainer();        
        
        XClientTable table = new XClientTable(TABLE_POLOZKY);
        table.addColumn(ComponentBuilder.createTableColumn(ObjednavkaPolozka.ID.getId(), ObjednavkaPolozka.ID, getSessionObject()));
        table.addColumn(ComponentBuilder.createTableColumn(Tovar.NAZOV.getId(), Tovar.NAZOV, Tovar.NAZOV.getCaption(), getSessionObject())); 
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
        form.setTitle(getSessionObject().translateText("Faktura"));
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
	    public Object fakturaId;
	    
	    public Parameters (Object fakturaId) {
	        this.fakturaId = fakturaId;
	    }
	}
	

}
