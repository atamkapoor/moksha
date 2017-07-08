package FinancialProducts;

import FinancialsEnums.ExerciseType;
import FinancialsEnums.OptionType;

import java.time.LocalDate;

/**
 * Created by atamkapoor on 2017-07-04.
 */
public class GenericOption extends GenericDerivative {
    private OptionType optionType;
    private ExerciseType exerciseType;
    private double strikePrice;

    public GenericOption(OptionType optType, LocalDate maturityDate, double strike, GenericProduct undl){
        setOptionType(optType);
        setMaturityDate(maturityDate);
        setStrikePrice(strike);
        setUnderlyingProduct(undl);
    }

    public void setOptionType(OptionType o){
        optionType = o;
    }

    public OptionType getOptionType(){
        return optionType;
    }

    public void setExerciseType(ExerciseType e){
        exerciseType = e;
    }

    public ExerciseType getExerciseType(){
        return exerciseType;
    }

    public void setStrikePrice(double K){
        strikePrice = K;
    }

    public double getStrikePrice(){
        return strikePrice;
    }
}
