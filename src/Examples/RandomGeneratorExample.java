package Examples;

import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.random.*;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class RandomGeneratorExample {

    public static void main(String args[]){

        myRandomGenerator generator = new myRandomGenerator();
        NormalizedRandomGenerator nrgUnif = new UniformRandomGenerator(generator);
        NormalizedRandomGenerator nrgNorm = new GaussianRandomGenerator(generator);

//        for (int i = 0; i < 10; i ++){
//            System.out.println(nrgUnif.nextNormalizedDouble());
//            System.out.println(nrgNorm.nextNormalizedDouble());
//        }

        double corr = -0.3;
        double[][] covData = {{1, corr}, {corr, 1.0}};
        RealMatrix covMat = new Array2DRowRealMatrix(covData);

        RandomVectorGenerator rvg = new CorrelatedRandomVectorGenerator(covMat,1.0e-12*covMat.getNorm(), nrgNorm);

        int numObservations = 1000;
        double[][] randNums = new double[numObservations][covMat.getColumnDimension()];
        for (int i = 0; i < numObservations; i ++){
            randNums[i] = rvg.nextVector();
        }

        RealMatrix resultMat = new Array2DRowRealMatrix(randNums);

        DescriptiveStatistics stats;
        stats = new DescriptiveStatistics(resultMat.getColumn(0));
        System.out.println(stats.toString());
        System.out.println("----------------------------");
        stats = new DescriptiveStatistics(resultMat.getColumn(1));
        System.out.println(stats.toString());
        System.out.println("----------------------------");
        System.out.println("Correlation");
        System.out.println(new PearsonsCorrelation().correlation(resultMat.getColumn(0), resultMat.getColumn(1)));

    }

}
