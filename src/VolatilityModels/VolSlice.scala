package VolatilityModels

import FinancialsEnums.OptionType
import PricingModels.LogNormalGreekUtils
import breeze.integrate.{simpson, trapezoid}
import math.{exp, sqrt, max, min}

/**
  * Created by ATAMKAPOOR on 11/4/2017.
  */
class VolSlice(val strike_data: Array[Double], val vol_data: Array[Double],
               val sigmaATM: Double, val forward: Double, val timeToMaturity: Double,
               val strike_type: String = "NS", val sliceType: String = "ModifiedSVI") {
  val model = sliceType match {
    case "ModifiedSVI" => ModifiedSVIModel(strike_data, vol_data, sigmaATM, forward, timeToMaturity, strike_type)
  }

  def getVol(inputStrikes: Array[Double], strikeType: String) = model.getVol(inputStrikes, strikeType)
  def getVol(inputStrike: Double, strikeType: String) = model.getVol(inputStrike, strikeType)

  def getVarianceSwapStrike(lowNS: Double = -6.0, highNS: Double = 6.0, numNodes: Int = 2000, integrationMethod: String = "Simpson"): Double = {
    require(List("Simpson", "Trapezoid").contains(integrationMethod), "Integration Method not supported")

    val stdDev = sigmaATM * sqrt(timeToMaturity)
    val putPriceFunc: (Double => Double) = ns => LogNormalGreekUtils.LogNormalOptionPrice(forward, forward * exp(ns * stdDev), getVol(ns, "NS"), timeToMaturity, OptionType.PUT) / (forward * exp(ns * stdDev))
    val callPriceFunc: (Double => Double) = ns => LogNormalGreekUtils.LogNormalOptionPrice(forward, forward * exp(ns * stdDev), getVol(ns, "NS"), timeToMaturity, OptionType.CALL) / (forward * exp(ns * stdDev))

    val varianceIntegral = integrationMethod match {
      case "Simpson" => simpson(putPriceFunc, lowNS, 0.0, numNodes / 2) + simpson(callPriceFunc, 0.0, highNS, numNodes / 2)
      case "Trapezoid" => trapezoid(putPriceFunc, lowNS, 0.0, numNodes / 2) + trapezoid(callPriceFunc, 0.0, highNS, numNodes / 2)
    }
    sqrt(max(varianceIntegral, 0.0) * 20000.0 * stdDev / timeToMaturity)
  }
}

class VolSurface(inputVolSlices: Array[VolSlice]) {
  val volSlices = inputVolSlices.sortBy(_.timeToMaturity)
  val timeToMaturities = volSlices.map{_.timeToMaturity}
  val sigmaATMs = volSlices.map{_.sigmaATM}
  val forwards = volSlices.map{_.forward}
  val numSlices = volSlices.length

  def getVarianceSwapStrikes(lowNS: Double = -6.0, highNS: Double = 6.0, numNodes: Int = 2000, integrationMethod: String = "Simpson") = volSlices.map{ _.getVarianceSwapStrike(lowNS, highNS, numNodes, integrationMethod) }

  def getVol(inputStrikes: Array[Double], inputExpiries: Array[Double], strikeType: String, interpType: String): scala.collection.mutable.Map[(Double, Double), Double] = {
    require(List("vol", "var", "varT").contains(interpType), "Interpolation Type not supported")

    val result = scala.collection.mutable.Map[(Double, Double), Double]()
    inputExpiries.foreach { inputExpiry =>
      val idxT = min(timeToMaturities.lastIndexWhere(i => (i < inputExpiry)) + 1, numSlices - 1)
      val vols = idxT match {
        case n if (n == 0 | (n == numSlices - 1)) => volSlices(n).getVol(inputStrikes, strikeType)
        case _ => {
          val volLeft = volSlices(idxT - 1).getVol(inputStrikes, strikeType)
          val volRight = volSlices(idxT).getVol(inputStrikes, strikeType)
          val tL = timeToMaturities(idxT - 1)
          val tR = timeToMaturities(idxT)
          interpType match {
            case "vol" => (volLeft zip volRight).map { case (vL, vR) => vL + (vR - vL) * (inputExpiry - tL) / (tR - tL) }
            case "var" => (volLeft zip volRight).map { case (vL, vR) => sqrt(max(vL*vL + (vR*vR - vL*vL) * (inputExpiry - tL) / (tR - tL), 0.0)) }
            case "varT" => (volLeft zip volRight).map { case (vL, vR) => sqrt(max(vL*vL*tL + (vR*vR*tR - vL*vL*tL) * (inputExpiry - tL) / (tR - tL), 0.0) / inputExpiry) }
          }
        }
      }
      (0 until inputStrikes.length-1) foreach { idx => result += (inputExpiry, inputStrikes(idx)) -> vols(idx) }
    }
    result
  }

  def getVol(inputStrike: Double, inputExpiry: Double, strikeType: String, interpType: String): Double = {
    require(List("vol", "var", "varT").contains(interpType), "Interpolation Type not supported")
    getVol(Array(inputStrike), Array(inputExpiry), strikeType, interpType)(inputStrike, inputExpiry)
  }

}