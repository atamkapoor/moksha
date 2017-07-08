package FinancialProducts;

/**
 * Created by atamkapoor on 2017-07-04.
 */
public class GenericFuture extends GenericDerivative{
    private double contractSize;

    public double getContractSize(){
        return contractSize;
    }

    public void setContractSize(double d){
        contractSize = d;
    }

}
