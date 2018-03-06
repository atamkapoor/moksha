package FinancialsEnums;

/**
 * Created by atamkapoor on 2017-07-04.
 */
public enum RateOptionType {
    PAYER('P'),
    RECEIVER('R'),
    CAP('C'),
    FLOOR('F');

    private char rateOptionTypeCharacter;
    RateOptionType(char c){
        this.rateOptionTypeCharacter = c;
    }

    public char getOptionTypeCharacter(){
        return rateOptionTypeCharacter;
    }

}
