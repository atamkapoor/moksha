package PricingModels

import FinancialsEnums.OptionType
import FinancialsEnums.OptionType.{CALL, PUT}
import MathStatsUtils.StatsUtils.{dnorm, pnorm}
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.solvers.BrentSolver

/**
  * Created by ATAMKAPOOR on 9/30/2017.
  */

object NormalGreekUtils {

  def NormalOption_d(ATMF: Double, strike: Double, sigma: Double, tau: Double) = (ATMF - strike) / (sigma * Math.sqrt(tau))

  def NormalOptionPrice(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType): Double = {
    val price = (tau, sigma) match {
      case (tp, sp) if ((tp > 0.0) & (sp > 0)) => {
        val d = NormalOption_d(ATMF, strike, sp, tp)
        val result = optType match {
          case CALL => sigma * Math.sqrt(tp) * (dnorm(d) + d * pnorm(d))
          case PUT => sigma * Math.sqrt(tp) * (dnorm(d) - d * pnorm(d))
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

  def NormalOptionDelta(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType): Double = {
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
      val d = NormalOption_d(ATMF, strike, sigma, tau)
      val result = optType match {
        case CALL => pnorm(d)
        case PUT => -pnorm(d)
      }
      result
    }
  }

  def NormalOptionGamma(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType): Double = {
    if (sigma <= 0.0 | tau  <= 0.0) {
      0.0
    } else {
      val d = NormalOption_d(ATMF, strike, sigma, tau)
      dnorm(d) / (sigma * Math.sqrt(tau))
    }
  }

  def NormalOptionVega(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType): Double = {
    if (sigma <= 0.0 | tau  <= 0.0) {
      0.0
    } else {
      val d = NormalOption_d(ATMF, strike, sigma, tau)
      dnorm(d) * Math.sqrt(tau)
    }
  }

  def NormalVolFromOptionPrice(ATMF: Double, strike: Double, tau: Double, optType: OptionType, optPrice: Double): Double = {
    val solver = new BrentSolver()
    val result = solver.solve(500, new UnivariateFunction {
      override def value(sigma: Double): Double = NormalOptionPrice(ATMF: Double, strike: Double, sigma: Double, tau: Double, optType: OptionType) - optPrice
    }, 0.0, 5.0 * Math.max(ATMF, 1))
    result
  }

}