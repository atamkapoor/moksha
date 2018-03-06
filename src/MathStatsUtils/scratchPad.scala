package MathStatsUtils

import FinancialsEnums.OptionType
import PricingModels.NormalGreekUtils
import PricingModels.LogNormalGreekUtils

/**
  * Created by ATAMKAPOOR on 8/27/2017.
  */
object scratchPad extends App {
  val ATMF = 0.025
  val tau = 1.0
  val optType = OptionType.CALL
  var strikes = Range.Double.inclusive(-0.0225, 0.0375, 0.0025).toArray
  strikes = strikes.map(x => x + ATMF)
  strikes foreach { K => printf("%.4f".format(K) + ", ") }
  println()

  var NVols = strikes.map(x => 4.0)
//  var NVols = Array(4.5978, 4.5386, 4.490037, 4.452229, 4.424687, 4.404236, 4.403286, 4.451817, 4.523915, 4.606343, 4.702399, 4.812621, 4.93702, 5.075597, 5.228352, 5.395287, 5.576393, 5.77168, 5.981145, 6.204787, 6.442607, 6.6946)
  NVols = NVols.map(x => x * Math.sqrt(252.0) / 10000.0)

  val optPrices = (strikes, NVols).zipped.map((K, S) => NormalGreekUtils.NormalOptionPrice(ATMF, K, S, tau, optType))
  optPrices foreach { p => printf("%.4f".format(p) + ", ") }
  println()

  val LNVols = (strikes, optPrices).zipped.map((K, prc) => LogNormalGreekUtils.LogNormalVolFromOptionPrice(ATMF, K, tau, optType, prc))
  NVols foreach { p => printf("%.4f".format(p) + ", ") }
  println()
  LNVols foreach { p => printf("%.4f".format(p) + ", ") }
  println()
}
