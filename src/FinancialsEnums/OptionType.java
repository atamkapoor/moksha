package FinancialsEnums;

/**
 * Created by atamkapoor on 2017-07-04.
 */
public enum OptionType {
    CALL('C'),
    PUT('P');

    private char optionTypeCharacter;
    OptionType(char c){
        this.optionTypeCharacter = c;
    }

    public char getOptionTypeCharacter(){
        return optionTypeCharacter;
    }

}
