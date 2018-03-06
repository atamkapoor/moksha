package PricingModels

import FinancialsEnums.OptionType
import FinancialsEnums.OptionType.{CALL, PUT}
import MathStatsUtils.StatsUtils.{dnorm, pnorm}
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.BrentSolver
/**
  * Created by ATAMKAPOOR on 9/30/2017.
  */


object LogNormalGreekUtils {

  def LogNormalOption_d(ATMF: Double, strike: Double, sigma: Double, tau: Double, d_type: String = "d1"): Double = {
    val d1 = (Math.log(ATMF/strike) + Math.pow(sigma, 2) * tau / 2.0) / (sigma * Math.sqrt(tau))
    d_type match {
      case "d1" => d1
      case "d2" => d1 - sigma * Math.sqrt(tau)
    }
  }

  def LogNormalOptionPrice(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType): Double = {
    val price = (tau, sigma) match {
      case (tp, sp) if ((tp > 0.0) & (sp > 0.0)) => {
        val d1 = LogNormalOption_d(ATMF, strike, sp, tp, "d1")
        val d2 = LogNormalOption_d(ATMF, strike, sp, tp, "d2")
        val result = optType match {
          case CALL => ATMF * pnorm(d1) - strike * pnorm(d2)
          case PUT => strike * pnorm(-d2) - ATMF * pnorm(-d1)
        }
        result
      }
      case (_, _) => {
        val result = optType match {
          case CALL => ATMF - strike
          case PUT => strike - ATMF
        }
        Math.max(result, 0.0)
      }
    }
    price
  }

  def LogNormalOptionDelta(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType): Double = {
    if (sigma <= 0.0 | tau  <= 0.0) {
      val result = optType match {
        case CALL => (ATMF - strike) match {
          case p if p > 0.0 => 1.0
          case n if n < 0.0 => 0.0
          case _ => 0.5
        }
        case PUT => (ATMF - strike) match {
          case p if p > 0.0 => 0.0
          case n if n < 0.0 => -1.0
          case _ => -0.5
        }
      }
      result
    } else {
      val d1 = LogNormalOption_d(ATMF, strike, sigma, tau, "d1")
      val result = optType match {
        case CALL => pnorm(d1)
        case PUT => -pnorm(-d1)
      }
      result
    }
  }

  def LogNormalOptionGamma(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType): Double = {
    if (sigma <= 0.0 | tau  <= 0.0) {
      0.0
    } else {
      val d1 = LogNormalOption_d(ATMF, strike, sigma, tau, "d1")
      dnorm(d1) / (ATMF * sigma * Math.sqrt(tau))
    }
  }

  def LogNormalOptionVega(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType): Double = {
    if (sigma <= 0.0 | tau  <= 0.0) {
      0.0
    } else {
      val d1 = LogNormalOption_d(ATMF, strike, sigma, tau, "d1")
      dnorm(d1) * ATMF * Math.sqrt(tau)
    }
  }

  def LogNormalVolFromOptionPrice(ATMF: Double, strike: Double, tau: Double, optType: OptionType, optPrice: Double): Double = {
    val solver = new BrentSolver()
    val result = solver.solve(500, new UnivariateFunction {
      override def value(sigma: Double): Double = LogNormalOptionPrice(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType) - optPrice
    }, 0.0, 5.0)
    result
  }

}