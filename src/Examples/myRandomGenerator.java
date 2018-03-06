package Examples;

import org.apache.commons.math3.random.AbstractRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.Random;

/**
 * Created by ATAMKAPOOR on 3/4/2018.
 */
public class myRandomGenerator extends AbstractRandomGenerator implements RandomGenerator {
    private final Random generator;

    public myRandomGenerator() {
        this.generator = new Random();
    }

    public void setSeed(long seed){
        // do nothing for now.
    }

    public double nextDouble(){
        return generator.nextDouble();
    }

}
