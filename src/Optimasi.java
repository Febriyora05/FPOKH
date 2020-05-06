import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Optimasi {
	int[][] timeslotHillClimbing, initialTimeslot, conflict_matrix, course_sorted;
	int[] timeslot;
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
		
		for(int i = 0; i < 1000000; i++) {
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
		
		for(int i = 0; i < 1000000; i++) {
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
//    	for (int course_index = 0; course_index < jumlahexam; course_index++)
//    		System.out.println("Timeslot untuk course "+ timeslotHillClimbing[course_index][0] +" adalah timeslot: " + timeslotHillClimbing[course_index][1]);       
    	System.out.println("=============================================================");
		System.out.println("		Metode HILL CLIMBING								 "); // print best penalty
		System.out.println("\nPenalty Initial : "+ initialPenalty); // print initial penalty
		System.out.println("Penalty Terbaik : "+ bestPenalty); // print best penalty
		System.out.println("Terjadi Peningkatan Penalti : " + deltaPenalty + " % dari inisial solusi");
		System.out.println("Timeslot yang dibutuhkan : " + schedule.getJumlahTimeSlot(timeslotHillClimbing) + "\n");
		System.out.println("=============================================================");
		
	}


//	// return timeslot each algorithm
	public int[][] getTimeslotHillClimbing() { return timeslotHillClimbing; }
	
//	// return timeslot each algorithm
	public int getJumlahTimeslotHC() { return schedule.getJumlahTimeSlot(timeslotHillClimbing); }

	private static int randomNumber(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min) + min;
	}

	private static int random(int number) {
		Random random = new Random();
		return random.nextInt(number);
	}

}
