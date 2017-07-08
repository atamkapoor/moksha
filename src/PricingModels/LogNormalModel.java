package PricingModels;

import FinancialProducts.GenericOption;
import FinancialsEnums.OptionType;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by atamkapoor on 2017-07-04.
 */
public class LogNormalModel {
    private static double daysPerYear = 365.242199;
    private static NormalDistribution ND = new NormalDistribution(0.0, 1.0);

    public static double optiond1(GenericOption option, LocalDate asOfDate, double forwardPrice, double volatility){
        double T = ChronoUnit.DAYS.between(asOfDate, option.getMaturityDate())/daysPerYear;
        double sigmaSqT = volatility * Math.sqrt(T);
        double d1 = Math.log(forwardPrice / option.getStrikePrice()) / (sigmaSqT) + 0.5 * sigmaSqT;
        return d1;
    }

    public static double optiond2(GenericOption option, LocalDate asOfDate, double forwardPrice, double volatility){
        double T = ChronoUnit.DAYS.between(asOfDate, option.getMaturityDate())/daysPerYear;
        double sigmaSqT = volatility * Math.sqrt(T);
        double d1 = optiond1(option, asOfDate, forwardPrice, volatility);
        return d1 - sigmaSqT;
    }

    public static double optionPrice(GenericOption option, LocalDate asOfDate, double forwardPrice, double volatility, double intRate){
        double T = ChronoUnit.DAYS.between(asOfDate, option.getMaturityDate())/daysPerYear;
        double d1 = optiond1(option, asOfDate, forwardPrice, volatility);
        double d2 = optiond2(option, asOfDate, forwardPrice, volatility);

        int optionSign = (option.getOptionType() == (OptionType.CALL)) ? 1 : -1;
        double price = optionSign * Math.exp(-intRate * T) * (forwardPrice * ND.cumulativeProbability(optionSign * d1) - option.getStrikePrice() * ND.cumulativeProbability(optionSign * d2));
        return price;
    }

}
