import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Optimasi {
	int[][] timeslotHillClimbing, initialTimeslot, conflict_matrix, course_sorted,timeslotTabuSearch;
	int[] timeslot;
        double[] tabuSearchPenaltyList1;
	String file;
	int jumlahexam, jumlahmurid, randomCourse, randomTimeslot, iterasi;
	double initialPenalty, bestPenalty, deltaPenalty;
	
	Jadwal schedule;
	
	Optimasi(String file, int[][] conflict_matrix, int[][] course_sorted, int jumlahexam, int jumlahmurid, int iterasi) { 
		this.file = file; 
		this.conflict_matrix = conflict_matrix;
		this.course_sorted = course_sorted;
		this.jumlahexam = jumlahexam;
		this.jumlahmurid = jumlahmurid;
		this.iterasi = iterasi;
	}
	
	// metode hill climbing
	public void getTimeslotByHillClimbing() throws IOException {
		schedule = new Jadwal(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		
		int[][] initialTimeslot = schedule.getSchedule(); // get initial solution
		timeslotHillClimbing = Evaluator.getTimeslot(initialTimeslot);
		initialPenalty = Evaluator.getPenalty(conflict_matrix, initialTimeslot, jumlahmurid);
		
		int[][] timeslotHillClimbingSementara = Evaluator.getTimeslot(timeslotHillClimbing); // menerima solusi sementara. jika lebih baik dari feasible, menggantikan inisial
		
		bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid);
		
		for(int i = 0; i < 10000; i++) {
			try {
				randomCourse = random(jumlahexam); // random course
				randomTimeslot = random(schedule.getJumlahTimeSlot(initialTimeslot)); // random timeslot
				
				if (Jadwal.checkRandomTimeslot(randomCourse, randomTimeslot, conflict_matrix, timeslotHillClimbingSementara)) {	
					timeslotHillClimbingSementara[randomCourse][1] = randomTimeslot;
					double penaltiAfterHillClimbing = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
					
					// compare between penalti. replace bestPenalty with penaltiAfterHillClimbing if initial penalti is greater
					if(bestPenalty > penaltiAfterHillClimbing) {
						bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbingSementara, jumlahmurid);
						timeslotHillClimbing[randomCourse][1] = timeslotHillClimbingSementara[randomCourse][1];
					} 
						else 
							timeslotHillClimbingSementara[randomCourse][1] = timeslotHillClimbing[randomCourse][1];
				}
				System.out.println("Iterasi ke " + (i+1) + " memiliki penalti : "+ bestPenalty);
			}
				catch (ArrayIndexOutOfBoundsException e) {
					//System.out.println("randomCourseIndex index ke- " + randomCourseIndex);
					//System.out.println("randomTimeslot index ke- " + randomTimeslot);
				}
			
		}
		
		deltaPenalty = ((initialPenalty-bestPenalty)/initialPenalty)*100;
		
		
    	System.out.println("=============================================================");
		System.out.println("		Metode HILL CLIMBING								 "); // print best penalty
		System.out.println("\nInitial Penalty : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : "+ bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti (delta) : " + deltaPenalty + " % dari inisial solusi" + "\n");
		System.out.println("Timeslot yang dibutuhkan : " + schedule.getJumlahTimeSlot(timeslotHillClimbing) + "\n");
		System.out.println("=============================================================");
		
	}
	public void getTimeslotByHillClimbing1() throws IOException {
		schedule = new Jadwal(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		LowLevelHeuristics lowLevelHeuristics = new LowLevelHeuristics(conflict_matrix);
		
		//schedule.printSchedule();
		int[][] initialTimeslot = schedule.getSchedule(); // get initial solution
		timeslotHillClimbing = Evaluator.getTimeslot(initialTimeslot);
		initialPenalty = Evaluator.getPenalty(conflict_matrix, initialTimeslot, jumlahmurid);
//		bestPenalty = Evaluator.getPenalty(conflict_matrix, initialTimeslot, jumlahmurid);
		int[][] timeslotHillClimbingSementara = new int[timeslotHillClimbing.length][2]; // handle temporary solution. if better than feasible, replace initial
		
		timeslotHillClimbingSementara = Evaluator.getTimeslot(timeslotHillClimbing);
		
		bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid); // initiate best penalty
		
		for(int i = 0; i < 10000; i++) {
			int llh = randomNumber(1, 5);
			int[][] timeslotLLH;
			switch (llh) {
				case 1:
					timeslotLLH = lowLevelHeuristics.move1(timeslotHillClimbingSementara);
					break;
				case 2:
					timeslotLLH = lowLevelHeuristics.swap2(timeslotHillClimbingSementara);
					break;
				case 3:
					timeslotLLH = lowLevelHeuristics.move2(timeslotHillClimbingSementara);
					break;
				case 4:
					timeslotLLH = lowLevelHeuristics.swap3(timeslotHillClimbingSementara);
					break;
				case 5:
					timeslotLLH = lowLevelHeuristics.move3(timeslotHillClimbingSementara);
					break;
				default:
					timeslotLLH = lowLevelHeuristics.swap2(timeslotHillClimbingSementara);
					break;
			}
			

			if (Evaluator.getPenalty(conflict_matrix, timeslotLLH, jumlahmurid) < Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid)) {
				timeslotHillClimbing = Evaluator.getTimeslot(timeslotLLH);
				bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotHillClimbing, jumlahmurid);
			}
				else 
					timeslotHillClimbingSementara[randomCourse][1] = timeslotHillClimbing[randomCourse][1];
			
			System.out.println("Iterasi ke " + (i+1) + ", memiliki penalti : "+ bestPenalty);
		}
		deltaPenalty = ((initialPenalty-bestPenalty)/initialPenalty)*100;
		// print updated timeslot
		System.out.println("\n================================================\n");

    	System.out.println("=============================================================");
		System.out.println("		Metode HILL CLIMBING								 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : "+ bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti : " + deltaPenalty + " % dari inisial solusi");
		System.out.println("Timeslot yang dibutuhkan : " + schedule.getJumlahTimeSlot(timeslotHillClimbing) + "\n");
		System.out.println("=============================================================");
		
	}
        public void getTimeslotByTabuSearch() {
		schedule = new Jadwal(file, conflict_matrix, jumlahexam);
		timeslot = schedule.schedulingByDegree(course_sorted);
		
		// initial solution
		timeslotTabuSearch = schedule.getSchedule();
		initialPenalty = Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid);
		
		int[][] bestTimeslot = Evaluator.getTimeslot(timeslotTabuSearch); // handle current best timeslot
		int[][] bestcandidate  = Evaluator.getTimeslot(timeslotTabuSearch);
		int[][] timeslotTabuSearchSementara = Evaluator.getTimeslot(timeslotTabuSearch);
		
//		int timeslot_dibutuhkan = Arrays.stream(timeslot).max().getAsInt();
		
		//inisiasi tabulist
        LinkedList<int[][]> tabuList = new LinkedList<int[][]>();
        int maxtabusize = 10;
        tabuList.addLast(Evaluator.getTimeslot(timeslotTabuSearch));
        
      //inisiasi iterasi
        int maxiteration = 10000;
        int iteration=0;
        
      //inisasi itung penalty
        double penalty1 = 0;
        double penalty2 = 0;
        double penalty3 = 0;
        
        boolean terminate = false;
        
        while(!terminate){
            iteration++;
            
//            search candidate solution / search neighbor
//            sneighborhood = getneighbor(bestcandidate)
           ArrayList<int[][]> sneighborhood = new ArrayList<>();
//           ArrayList<Double> listPenalty = new ArrayList<>();     
              
//        		int[][] timeslotLLH;
        	LowLevelHeuristics lowLevelHeuristics = new LowLevelHeuristics(conflict_matrix);
        	timeslotTabuSearchSementara = lowLevelHeuristics.move1(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.swap2(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.move2(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.swap3(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
			timeslotTabuSearchSementara = lowLevelHeuristics.move3(timeslotTabuSearchSementara);
			sneighborhood.add(timeslotTabuSearchSementara);
				
        		
        		//membandingkan neighbor, pilih best neighbor, membandingkan juga apa ada di tabu list
           int j = 0;
           while (sneighborhood.size() > j) {
               if( !(tabuList.contains(sneighborhood.get(j))) && 
            		   Evaluator.getPenalty(conflict_matrix, sneighborhood.get(j), jumlahmurid) < Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid))
                 bestcandidate = sneighborhood.get(j);
                	
               j++;
           }
                
           sneighborhood.clear();
                
           //bandingkan best neighbor dengan best best solution
           if(Evaluator.getPenalty(conflict_matrix, bestcandidate, jumlahmurid) < Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid))
              timeslotTabuSearch = Evaluator.getTimeslot(bestcandidate);
                
           //masukkan best neighbor tadi ke tabu
           tabuList.addLast(bestcandidate);
           if(tabuList.size() > maxtabusize)
              tabuList.removeFirst();
                
           //return sbest;
           tabuSearchPenaltyList1 = new double[100];
           if ((iteration+1)%10 == 0)
               System.out.println("Iterasi: " + (iteration+1) + " memiliki penalty " + Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid));
           
           if (iteration == maxiteration) 
        	   terminate = true;
        }
        bestPenalty = Evaluator.getPenalty(conflict_matrix, timeslotTabuSearch, jumlahmurid);
        deltaPenalty = ((initialPenalty-bestPenalty)/initialPenalty)*100;
        
        System.out.println("=============================================================");
		System.out.println("		Metode TABU SEARCH						 			 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : " + bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti : " + deltaPenalty + " % dari inisial solusi");
		System.out.println("Timeslot yang dibutuhkan : " + schedule.getJumlahTimeSlot(timeslotTabuSearch) + "\n");
		System.out.println("=============================================================");
	}


//	// mengembalikan timeslot di setiap algoritma
	public int[][] getTimeslotHillClimbing() { return timeslotHillClimbing; }
        public int[][] getTimeslotTabuSearch() { return timeslotTabuSearch; }
	
//	// mwngembalikan timeslot di setiap algoritma
	public int getJumlahTimeslotHC() { return schedule.getJumlahTimeSlot(timeslotHillClimbing); }
        public int getJumlahTimeslotTabuSearch() { return schedule.getJumlahTimeSlot(timeslotTabuSearch); }

	private static int randomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}

	private static int random(int number) {
		Random random = new Random();
		return random.nextInt(number);
	}

}
