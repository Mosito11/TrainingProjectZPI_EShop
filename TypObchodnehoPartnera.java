package eshop.bo.ciselniky;

import netframework.bo.Constant;

public class TypObchodnehoPartnera implements Constant<String> {

	private String kod;
	private String nazov;
	
	public static final TypObchodnehoPartnera DODAVATEL = new TypObchodnehoPartnera("D", "DODAVATEL");
	public static final TypObchodnehoPartnera ZAKAZNIK = new TypObchodnehoPartnera("Z", "ZAKAZNIK");
    
    public static final TypObchodnehoPartnera[] ZOZNAM = new  TypObchodnehoPartnera[] {DODAVATEL, ZAKAZNIK};  
	
	private TypObchodnehoPartnera(String kod, String nazov) {
		this.kod = kod;
		this.nazov = nazov;
	}
	
	public static TypObchodnehoPartnera convert(String kod)  {
        if (kod == null)
           return null;        
        for(int i = 0; i < ZOZNAM.length; i++) {
            if(ZOZNAM[i].getKey().equals(kod)){
                return ZOZNAM[i];
            } 
        }
        return null;
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kod == null) ? 0 : kod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypObchodnehoPartnera other = (TypObchodnehoPartnera) obj;
		if (kod == null) {
			if (other.kod != null)
				return false;
		} else if (!kod.equals(other.kod))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return kod +"-"+ nazov;
	}
	
	@Override
	public String getKey() {
		return kod;
	}

	@Override
	public String getText() {
		return nazov;
	}

	@Override
	public String getTextForReport() {
		return nazov;
	}

	@Override
	public String getTextForTable() {
		return nazov;
	}

}
