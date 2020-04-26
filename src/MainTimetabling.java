import java.io.IOException;
import java.util.Scanner;

public class MainTimetabling {

    static String folderDataset = "B:\\SI\\SEMESTER 6\\OKH\\FP\\Toronto\\";
    static String namafile[][] = {{"car-f-92", "Carleton92"}, {"car-s-91", "Carleton91"}, {"ear-f-83", "EarlHaig83"}, {"hec-s-92", "EdHEC92"}, {"kfu-s-93", "KingFahd93"}, 
                                   {"lse-f-91", "LSE91"}, {"pur-s-93", "pur93"}, {"rye-s-93", "rye92"}, {"sta-f-83", "St.Andrews83"},{"tre-s-92", "Trent92"}, 
                                   {"uta-s-92", "TorontoAS92"}, {"ute-s-92", "TorontoE92"}, {"yor-f-83", "YorkMills83"}
                                  };
    
    static int timeSlot[]; // fill with course & its timeslot
    static int[][] conflict_matrix, course_sorted, hasil_timeslot;
	
	private static Scanner scanner;
	
    public static void main(String[] args) throws IOException {
        scanner = new Scanner(System.in);
        for	(int i=0; i< namafile.length; i++)
        	System.out.println(i+1 + ". Penjadwalan " + namafile[i][1]);
        
        System.out.print("\nSilahkan pilih file untuk dijadwalkan : ");
        int pilih = scanner.nextInt();
        
        String Input = namafile[pilih-1][0];
        String Output = namafile[pilih-1][1];
        
        String file = folderDataset + Input;
        
        Kelas course = new Kelas(file);
        int jumlahexam = course.getJumlahCourse();
        
        conflict_matrix = course.getConflictMatrix();
        int jumlahmurid = course.getJumlahMurid();
        
	// mengurutkan exam berdasarkan degree
	course_sorted = course.sortingByDegree(conflict_matrix, jumlahexam);
		
		
	long starttimeLargestDegree = System.nanoTime();
            Jadwal schedule = new Jadwal(file, conflict_matrix, jumlahexam);
             timeSlot = schedule.schedulingByDegree(course_sorted);
                int[][] timeslotByLargestDegree = schedule.getSchedule();
                    long endtimeLargestDegree = System.nanoTime();

		System.out.println("PENJADWALAN  " + Output + "\n");
		
		System.out.println("Timeslot        : " + schedule.getJumlahTimeSlot(schedule.getSchedule()));
		System.out.println("Penalti  	    : " + Evaluator.getPenalty(conflict_matrix, schedule.getSchedule(), jumlahmurid));
		System.out.println("Waktu eksekusi  :"+ ((double) (endtimeLargestDegree - starttimeLargestDegree)/1000000000) + " detik.\n");

    }


}