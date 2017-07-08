import FinancialsEnums.OptionType;

import java.time.LocalDate;
import java.util.Currency;

/**
 * Created by atamkapoor on 2017-07-04.
 */
public class HelloWorld {
    public static void main(String[] args) {

        for (OptionType otIdx: OptionType.values()){
            System.out.println(otIdx.getOptionTypeCharacter());
        }

        FinancialProducts.GenericProduct vix = new FinancialProducts.GenericProduct();
        vix.setCurrency(Currency.getInstance("USD"));
        System.out.println(vix.getCurrency());

        LocalDate matDt = LocalDate.of(2017, 12, 20);
        LocalDate asofDt = LocalDate.of(2017, 06, 20);

        FinancialProducts.GenericOption option = new FinancialProducts.GenericOption(FinancialsEnums.OptionType.CALL, matDt, 2400, new FinancialProducts.GenericProduct());

        double optPrice = PricingModels.LogNormalModel.optionPrice(option, asofDt,2400, 0.10, 0);
        System.out.println("The option price is: " + optPrice);
    }
}
