package FinancialProducts;

import java.time.LocalDate;

/**
 * Created by atamkapoor on 2017-07-04.
 */
public class GenericDerivative extends GenericProduct {
    private GenericProduct underlying;
    private LocalDate maturityDate;
    private boolean exchangeTraded = true;

    public void setUnderlyingProduct(GenericProduct p){
        underlying = p;
    }

    public GenericProduct getUnderlyingProduct(){
        return underlying;
    }

    public void setMaturityDate(LocalDate d){
        maturityDate = d;
    }

    public LocalDate getMaturityDate(){
        return maturityDate;
    }

    public void setExchangeTraded(boolean b){
        exchangeTraded = b;
    }

    public boolean isExchangeTraded(){
        return exchangeTraded;
    }

}
