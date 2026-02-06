package assignments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Approximates PI using the Monte Carlo method.  Demonstrates
 * use of Callables, Futures, and thread pools.
 */
public class Pi 
{
    public static void main(String[] args) throws Exception 
    {
	long total=0;
	// 10 workers, 50000 iterations each
	total = new Master().doRun(50000, 10);
	System.out.println("total from Master = " + total);
    }
}

/**
 * Creates workers to run the Monte Carlo simulation
 * and aggregates the results.
 */
class Master {
    public long doRun(int totalCount, int numWorkers) throws InterruptedException, ExecutionException 
    {

	long startTime = System.currentTimeMillis();

	// Create a collection of tasks
	List<Callable<Long>> tasks = new ArrayList<Callable<Long>>();
	for (int i = 0; i < numWorkers; ++i) 
	    {
		tasks.add(new Worker(totalCount));
	    }
    
	// Run them and receive a collection of Futures
	ExecutorService exec = Executors.newFixedThreadPool(numWorkers);
	List<Future<Long>> results = exec.invokeAll(tasks);
	long total = 0;
    
	// Assemble the results.
	for (Future<Long> f : results)
	    {
		// Call to get() is an implicit barrier.  This will block
		// until result from corresponding worker is ready.
		total += f.get();
	    }
	double pi = 4.0 * total / totalCount / numWorkers;

	long stopTime = System.currentTimeMillis();
	System.out.println("\nPi : " + pi );
	System.out.println("Error: " + (Math.abs((pi - Math.PI)) / Math.PI) +"\n");

	System.out.println("Ntot: " + totalCount*numWorkers);
	System.out.println("Available processors: " + numWorkers);
	System.out.println("Time Duration (ms): " + (stopTime - startTime) + "\n");

	System.out.println( (Math.abs((pi - Math.PI)) / Math.PI) +" "+ totalCount*numWorkers +" "+ numWorkers +" "+ (stopTime - startTime));

	exec.shutdown();
	return total;
    }
}

/**
 * Task for running the Monte Carlo simulation.
 */
class Worker implements Callable<Long> 
{
    public long T1;
    private int numIterations;
    public Worker(int num) 
	{ 
	    this.numIterations = num; 
	}

  @Override
      public Long call() 
      {
	  long circleCount = 0;
	  Random prng = new Random ();
	  for (int j = 0; j < numIterations; j++)
	      {
		  double x = prng.nextDouble();
		  double y = prng.nextDouble();
		  if ((x * x + y * y) < 1)  ++circleCount;
	      }
	  return circleCount;
      }
}

class TestPi {
    public static void main(String[] args) throws Exception
    {
        int[] tab = {1,2,4,8,16};
        long[] temps = new long[tab.length];
        long totalTemps = 0;
        long[] result = new long[tab.length];
        int totalCount = 12000000;
        for (int i = 0; i < tab.length; ++i){
            int nb = tab[i];
            long startTime = System.currentTimeMillis();
            long total= new Master().doRun(totalCount, nb);
            long stopTime = System.currentTimeMillis();
            temps[i] = (stopTime - startTime);
            totalTemps += temps[i];
            result[i] = total;
        }
        float t1 = (float) temps[0] ;//(float)totalTemps / (float) temps.length; Si on veut la moyenne
        for (int i = 0; i < tab.length; ++i){
            System.out.println(t1/(float)temps[i]);
            //System.out.println("nb Processeurs : "+tab[i] + " SpeedUp : "+t1/(float)temps[i]+ " temps : "+temps[i]+"ms");
        }
    }
}
