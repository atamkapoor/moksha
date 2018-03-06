package MathStatsUtils
/**
  * Created by ATAMKAPOOR on 8/27/2017.
  */

import FinancialsEnums.ProcessType
import breeze.interpolation.LinearInterpolator
import breeze.linalg.DenseVector
import breeze.stats.distributions.Gaussian

object StatsUtils{

  def pnorm(mean: Double, stdev: Double, d: Double) = {
    Gaussian(mean, stdev).cdf(d)
  }

  def pnorm(d: Double): Double = {
    pnorm(0.0, 1.0, d)
  }

  def dnorm(mean: Double, stdev: Double, d: Double) = {
    Gaussian(mean, stdev).pdf(d)
  }

  def dnorm(d: Double): Double = {
    dnorm(0.0, 1.0, d)
  }

  def rnorm(mean: Double, stdev: Double, n: Int) = {
    Gaussian(mean, stdev).sample(n)
  }

/*
  def rnorm(n: Int) = {
    rnorm(0.0, 1.0, n)
  }
*/
}


