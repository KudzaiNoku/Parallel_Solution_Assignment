package MonteCarloMini;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.Random;

public class MonteCarloMinimizationParallel extends RecursiveTask<MinVal>{
   // class variables
   static long endTime;
   static long startTime;
   private int hi; 
   private int lo;
   private Search[] arr;
   private static final int SEQUENTIAL_CUTOFF = 1000;
   static final ForkJoinPool fjPool = new ForkJoinPool();
   static int finder;
   
	//timers : time in milliseconds
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	private static void tock(){
		endTime = System.currentTimeMillis(); 
	}

    MonteCarloMinimizationParallel(Search[] a, int l, int h) {
        lo = l;//lower limit of subarray
        hi = h;//upper limit of subarray
        arr = a;
    }
    
	   //write compute() method
    protected MinVal compute() {
        if ((hi - lo) < SEQUENTIAL_CUTOFF) {//if the range of indices is smaller than the sequential cutoff, run a squential search
            int min=Integer.MAX_VALUE;            
          	int local_min=Integer.MAX_VALUE;
            
            MinVal minNum = new MinVal(min,0);
            MinVal localNum = new MinVal(local_min,0);
            
            int finder =-1;
          	for  (int i=lo;i<hi;i++) {
               localNum.value = arr[i].find_valleys();
               
          		if((!arr[i].isStopped())&&(localNum.value<minNum.value)) { //don't look at those who stopped because hit exisiting path
                  minNum.value=localNum.value;
                  minNum.finder = i;//keep track of who found it
          		}            
            }
            return minNum;
        } else {//else, split range in two, left is given to fork/join pool, right is computed
            MonteCarloMinimizationParallel left = new MonteCarloMinimizationParallel(arr, lo, (hi + lo) / 2);
            MonteCarloMinimizationParallel right = new MonteCarloMinimizationParallel(arr, (hi + lo) / 2, hi);
            left.fork();
            MinVal rightAns = right.compute();

            MinVal leftAns = left.join();
           
            return MinVal.getMinimum(leftAns,rightAns);//return global minimum obj
        }
    }//end of compute


    
    
    static MinVal findMin(Search[] arr) {
      MonteCarloMinimizationParallel minFind = new MonteCarloMinimizationParallel(arr, 0 , arr.length);
      tick();
      MinVal globalMin = fjPool.invoke(minFind); // we only time how long it takes for the parallel search  to complete
      tock(); 
      return globalMin;  
      }//findMin
      
      
      
    public static void main(String[] args){
      //initializing everything
     	int num_searches;		// Number of searches	    	
      Random rand = new Random();  //the random number generator
    	
    	if (args.length!=7) {  
    		System.out.println("Incorrect number of command line arguments provided.");   	
    		System.exit(0);
    	}
    	/* Read argument values */
    	int rows = Integer.parseInt( args[0] ); //grid size
    	int columns = Integer.parseInt( args[1] ); //grid size
      //x and y terrain limits
    	double xmin = Double.parseDouble(args[2] ); 
    	double xmax = Double.parseDouble(args[3] );
    	double ymin = Double.parseDouble(args[4] );
    	double ymax = Double.parseDouble(args[5] );
      
    	double searches_density = Double.parseDouble(args[6] ); // Density - number of Monte Carlo  searches per grid position - usually less than 1!
      
    	TerrainArea terrain = new TerrainArea(rows, columns, xmin,xmax,ymin,ymax);
    	num_searches = (int)( rows * columns * searches_density );
    	Search[] searches= new Search [num_searches]; // Array of searches
      
    	for (int i=0;i<num_searches;i++) 
    		searches[i]=new Search(i+1, rand.nextInt(rows),rand.nextInt(columns),terrain);//initializing search array
    	
      
      //get minimum
      MinVal minFound = findMin(searches);
      int finder = minFound.finder;
      
      //summary
		System.out.printf("Run parameters\n");
		System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
		System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax );
		System.out.printf("\t Search density: %f (%d searches)\n", searches_density,num_searches );

		System.out.printf("Time: %d ms\n",endTime - startTime );// total computation time
		int tmp=terrain.getGrid_points_visited();
		System.out.printf("Grid points visited: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
		tmp=terrain.getGrid_points_evaluated();
		System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");

		//Results
		System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", minFound.value, terrain.getXcoord(searches[finder].getPos_row()), terrain.getYcoord(searches[finder].getPos_col()) );
		
            
    }
}//MonteCarloMinimizationParallel class

   
class MinVal {//a class representing a value and the index pointing to its coordinates in terrain
   int value;
   int finder;
   
   MinVal(int v, int f){
      value = v;
      finder = f;
   }
   
  static MinVal getMinimum(MinVal obj1, MinVal obj2){//returns obj with smallest value
      if (obj1.value<obj2.value) {return obj1;}
      if (obj2.value<obj1.value) {return obj2;}
      return obj2;//if they are equal
   }
}