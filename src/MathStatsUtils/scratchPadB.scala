package MathStatsUtils

import VolatilityModels.{ModifiedSVIModel, ModifiedSVIModelParameters, VolSlice}
import breeze.integrate._
import math.{exp, sqrt}
/**
  * Created by ATAMKAPOOR on 8/27/2017.
  */
object scratchPadB extends App {

  val spot = 2500
  val forward = 2581.85
  val sigmaATM = 7.3
  val timeToMaturity = 0.08
  val params = ModifiedSVIModelParameters(0.32, 0.12, 2.90, 1.86)
  val slice = ModifiedSVIModel(Array.empty[Double], Array.empty[Double], sigmaATM, forward, timeToMaturity)
  slice.modelParams = params
  slice.hasFit = true

  Range.Double.inclusive(-10.0, 4.0, 1) foreach {ns => printf("%.4f".format(100.0*slice.getVol(ns)) + ", ")}
}
