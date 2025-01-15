package eshop.bo.ciselniky;

import java.math.BigDecimal;

import org.eclipse.persistence.zpi.indirection.ValueHolder;
import org.eclipse.persistence.zpi.indirection.ValueHolderInterface;

import eshop.bo.common.Helper;
import netframework.bo.PersistentObject;
import netframework.bo.attributes.NumberAttribute;
import netframework.bo.attributes.RelationAttribute;
import netframework.eclipselink.EclipseLinkSession;

public class ObjednavkaPolozka extends PersistentObject {
	
	public static final NumberAttribute ID = new NumberAttribute("ObjednavkaPolozka.id", "id");
	//potrebujem este do konstruktora doplnit NumberAttribute.BIG_DECIMAL? 
	public static final NumberAttribute JEDNOTKOVA_CENA = new NumberAttribute("ObjednavkaPolozka.jednotkovaCena", "jednotkovaCena");
	//tu viem v ramci NumberAttribute nastavit aj typ premennej, ktora vstupuje z formulara
    public static final NumberAttribute MNOZSTVO = new NumberAttribute("ObjednavkaPolozka.mnozstvo", "mnozstvo", NumberAttribute.BIG_DECIMAL);
    public static final NumberAttribute SUMA = new NumberAttribute("ObjednavkaPolozka.suma", "suma");
    public static final RelationAttribute TOVAR = new RelationAttribute("ObjednavkaPolozka.tovar", "tovar");
    public static final RelationAttribute OBJEDNAVKA = new RelationAttribute("ObjednavkaPolozka.objednavka", "objednavka");

    static{   	
    	//Caption a LongCaption je definicia, ako sa atribut zobrazuje vo formularoch
    	JEDNOTKOVA_CENA.setCaption("Jednotkova cena");
    	JEDNOTKOVA_CENA.setColumnName("Jednotkova\ncena");
    	JEDNOTKOVA_CENA.setRequired(true);
    	JEDNOTKOVA_CENA.setZeroAccepted(true);
    	JEDNOTKOVA_CENA.setNegativeAccepted(false);
    	JEDNOTKOVA_CENA.setLength(8);
    	JEDNOTKOVA_CENA.setScale(2);
    	JEDNOTKOVA_CENA.setMask("###,##0.00");
		
		MNOZSTVO.setCaption("Mnozstvo");
		MNOZSTVO.setRequired(true);
		MNOZSTVO.setZeroAccepted(false);
		MNOZSTVO.setLength(8);
		MNOZSTVO.setScale(0);
		MNOZSTVO.setMask("##,###,##0");
		
		SUMA.setCaption("Suma");
		/*
		CENA.setColumnName();
		*/
		SUMA.setRequired(true);
		SUMA.setZeroAccepted(true);
		SUMA.setLength(8);
		SUMA.setScale(2);
		SUMA.setMask("###,##0.00");
		
		TOVAR.setCaption("Tovar");
		TOVAR.setLongCaption("Tovar");
		TOVAR.setColumnName(TOVAR.getCaption());
		TOVAR.setLongColumnName(TOVAR.getLongCaption());
		TOVAR.setRequired(true);
		
		OBJEDNAVKA.setRequired(true);
    }
	
	private BigDecimal jednotkovaCena = Helper.DEFAULT_VALUE;
	private BigDecimal mnozstvo = Helper.DEFAULT_VALUE;
	private BigDecimal suma = Helper.DEFAULT_VALUE;
	private ValueHolderInterface tovar = new ValueHolder();
	private ValueHolderInterface objednavka = new ValueHolder();
	
	public BigDecimal getJednotkovaCena() {
		return jednotkovaCena;
	}
	
	public void setJednotkovaCena(BigDecimal jednotkovaCena) {
		if (jednotkovaCena == null)
			jednotkovaCena = Helper.DEFAULT_VALUE;
		this.jednotkovaCena = jednotkovaCena;
	}
	
	public BigDecimal getMnozstvo() {
		return mnozstvo;
	}
	
	public void setMnozstvo(BigDecimal mnozstvo) {
		if (mnozstvo == null)
			mnozstvo = Helper.DEFAULT_VALUE;
		this.mnozstvo = mnozstvo;
	}
	
	public BigDecimal getSuma() {
		return suma;
	}
/*	
	public void setSuma(BigDecimal suma) {
		this.suma = suma;
	}
	*/
	public Tovar getTovar() {
		return (Tovar) tovar.getValue();
	}
	
	public void setTovar(Tovar tovar) {
		this.tovar.setValue(tovar); 
	}
	
	public Objednavka getObjednavka() {
		return (Objednavka) objednavka.getValue();
	}
	
	public void setObjednavka(Objednavka objednavka) {
		this.objednavka.setValue(objednavka); 
	}
	
	public void validate(EclipseLinkSession session) throws Exception {
        //do validacie davam aj vypocet atributov, ktore viem dotiahnut a vypocitat, ako je suma a jednotkova cena tovaru
		TOVAR.checkValue(getTovar(), session);
		jednotkovaCena = getTovar().getCena();
        JEDNOTKOVA_CENA.checkValue(getJednotkovaCena(), session);
        //TODO mnozstvo musi byt mensie ako je skladova zasoba daneho tovaru
        if (this.mnozstvo.doubleValue() > this.getTovar().getSkladovaZasoba().doubleValue()) 
        	throw new IllegalArgumentException("Mnozstvo v polozke nemoze byt vacsie ako skladova zasoba tovaru!");
        MNOZSTVO.checkValue(getMnozstvo(), session);
        
        OBJEDNAVKA.checkValue(getObjednavka(), session);
        //vypocet + zaokruhlenie
        suma = getJednotkovaCena().multiply(getMnozstvo());
        suma = suma.setScale(JEDNOTKOVA_CENA.getScale(), BigDecimal.ROUND_HALF_UP);
        SUMA.checkValue(getSuma(), session);
    }
	
	public ObjednavkaPolozka stornuj(Objednavka stornoObjednavka) {
		ObjednavkaPolozka polozka = new ObjednavkaPolozka();
		polozka.setObjednavka(stornoObjednavka);
		polozka.setJednotkovaCena(getJednotkovaCena());
		polozka.setMnozstvo(getMnozstvo().multiply(new BigDecimal ("-1")));
		polozka.setTovar(getTovar());
		polozka.suma = polozka.getJednotkovaCena().multiply(polozka.getMnozstvo()).setScale(JEDNOTKOVA_CENA.getScale(), BigDecimal.ROUND_HALF_UP);
		return polozka; 
				
	}
	
}
