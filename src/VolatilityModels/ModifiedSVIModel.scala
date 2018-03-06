package VolatilityModels

import org.apache.commons.math3.fitting.leastsquares.{LeastSquaresProblem, LevenbergMarquardtOptimizer}
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.optim.ConvergenceChecker
import org.apache.commons.math3.util.Incrementor

import math.{exp, log, max, min, sqrt}

/**
  * Created by ATAMKAPOOR on 11/4/2017.
  */
case class ModifiedSVIModelParameters(skew: Double, smile: Double, put_wing: Double, call_wing: Double) {
  def this() = this(Double.NaN, Double.NaN, Double.NaN, Double.NaN)
  def numParameters = 4
}

case class ModifiedSVIModel(strike_data: Array[Double], vol_data: Array[Double], sigmaATM: Double, forward: Double, timeToMaturity: Double, strike_type: String = "NS") {
  def this(modifiedSVIModelParameters: ModifiedSVIModelParameters, sigmaATM: Double, forward: Double, timeToMaturity: Double) = {
    this(Array.empty[Double], Array.empty[Double], sigmaATM, forward, timeToMaturity)
    modelParams = modifiedSVIModelParameters
    hasFit = true
  }

  var modelParams = new ModifiedSVIModelParameters()
  var hasFit = false

  def calibrateModel(firstGuess: Array[Double], constraints: Array[Double], errorFunctionType: String = "VEGA") = {
    // replace below with actual calibration result - Use LM
//    val lmOptimizer = new LevenbergMarquardtOptimizer()
//    lmOptimizer.optimize(new LeastSquaresProblem {
//
//      override def getObservationSize: Int = strike_data.length
//
//      override def getStart: RealVector = ???
//
//      override def getParameterSize: Int = modelParams.numParameters
//
//      override def evaluate(point: RealVector): LeastSquaresProblem.Evaluation = ???
//
//      override def getConvergenceChecker: ConvergenceChecker[LeastSquaresProblem.Evaluation] = ???
//
//      override def getIterationCounter: Incrementor = ???
//
//      override def getEvaluationCounter: Incrementor = ???
//    })
//
//
//
    hasFit = true
    modelParams = new ModifiedSVIModelParameters()
  }

  def getVol(inputStrikes: Array[Double], strikeType: String): Array[Double] = {
    strikeType match {
      case "NS" => inputStrikes.map { strike => getVol(strike) }
      case "K" => {
        inputStrikes.map { strike =>
          val ns = log(strike / forward) / (sigmaATM * sqrt(timeToMaturity) / 100.0)
          getVol(ns)
        }
      }
      case "LogKF" => {
        inputStrikes.map { strike =>
          val ns = strike / (sigmaATM * sqrt(timeToMaturity) / 100.0)
          getVol(ns)
        }
      }
    }
  }

  def getVol(strike: Double, strikeType: String): Double = {
    strikeType match {
      case "NS" => getVol(strike)
      case "K" => {
        val ns = log(strike / forward) / (sigmaATM * sqrt(timeToMaturity) / 100.0)
        getVol(ns)
      }
      case "LogKF" => {
        val ns = strike / (sigmaATM * sqrt(timeToMaturity) / 100.0)
        getVol(ns)
      }
    }
  }

  def getVol(NS: Double): Double = {
    // check if the model is fit - if not, first calibrate the model, then run this function.
    require(hasFit, "Model is not calibrated yet. Fit the model using calibrateModel function first")

    val skew = modelParams.skew
    val smile = modelParams.smile
    val put_wing = modelParams.put_wing
    val call_wing = modelParams.call_wing
    val cut_off = 0.9

    val (skew_wing, smile_wing, slope) = NS match {
      case n if n < 0 => {
        val skew_wing = skew match {
          case pskew if pskew >= 0 => log(1.0 - min(put_wing, cut_off))
          case _ => 1.0 / (4.0 * skew)
        }
        val smile_wing = log(1.0 - min(put_wing, cut_off))
        val slope = -1.0 * max(put_wing - cut_off, 0.0)
        (skew_wing, smile_wing, slope)
      }
      case _ => {
        val skew_wing = skew match {
          case pskew if pskew > 0 => 1.0 / (4.0 * skew)
          case _ => -1.0 * log(1.0 - min(call_wing, cut_off))
        }
        val smile_wing = -1.0 * log(1.0 - min(call_wing, cut_off))
        val slope = max(call_wing - cut_off, 0.0)
        (skew_wing, smile_wing, slope)
      }
    }

    val skew_term = 2.0 * skew_wing - exp(-NS / skew_wing) * (NS + 2.0 * skew_wing)
    val smile_term = (smile_wing - (NS + smile_wing) * exp(-NS / smile_wing)) * smile_wing
    val variance = sigmaATM * sigmaATM * (1.0 + slope * NS - skew_term * (slope + 2.0 * skew) + 2.0 * smile_term * smile)

    sqrt(max(variance, 0.0)) / 100.0
  }
}