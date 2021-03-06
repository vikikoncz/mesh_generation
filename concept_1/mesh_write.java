//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;


/* mesh_adap_logolas.java alapjan osszevadaszott parametereket kiirja tenylegesen fajlba
Mukodese nagyban hasonlit a current_write.java es a profile_write.java osztalyokhoz
*/


public class mesh_write.java{
	
	String name;   // ez lesz a fajnev eleresi uttal, ahova menteni kell majd

void start() throws IOException
{
	machine_settings ms=new machine_settings();
	String dirnev_elo=ms.getDIR_for_mesh();   //TODO ezt az osztalyt implementalni

	string_subdir string_subdir=new string_subdir();
	
	String subdir=string_subdir.get_subdir_name(sol, sol_t, tlist1);
	
	String dirnev=dirnev_elo+subdir;
	System.out.println(dirnev);
	System.out.println(dirnev_elo);
	System.out.println(subdir);
	
	string_filename string_filename=new string_filename();
	String s=string_filename.get_file_name(sol, sol_t, tlist1);
	
	Date dateNow = new Date ();
	String d1=dateNow.toString();
	String d2=d1.replaceAll(" ","_");
	String d3=d2.replaceAll(":",".");
	/*System.out.println(dateNow);
	System.out.println(d3);
	*/
	System.out.println("mesh");
	
	name=dirnev+s+"_"+d3+".dat";

	File f=new File(name);
	FileWriter ki_stream = new FileWriter(f);
	PrintWriter ki = new PrintWriter(ki_stream);
	
	ki.println("%Results of the time-dependent simulation of acid-base diode\n%MESH adaption results");

	ki.println("%Stationary settings and concentrations:");
	ki.println("%c_koh="+sol.c_koh);
	ki.println("%c_hcl="+sol.c_hcl);
	ki.println("%c_kcl_base="+sol.c_kcl_base);
	ki.println("%c_kcl_acid="+sol.c_kcl_acid);
	ki.println("%U0="+sol.U0);
	ki.println("%");
	
	ki.println("%Time-dependent settings and concentrations");
	ki.println("%c_koh_t="+sol_t.c_koh);
	ki.println("%c_hcl_t="+sol_t.c_hcl);
	ki.println("%c_kcl_base_t="+sol_t.c_kcl_base);
	ki.println("%c_kcl_acid_t="+sol_t.c_kcl_acid);
	ki.println("%U0_t="+sol_t.U0);
	ki.println("%");

	int x=tlist1.length;
	int y=tlist_solved_in_this_step.length;	 //szerintem ennek itt es most nincs relevanciaja
	
	
	ki.println("%Time_settings");
	ki.println("%Duration of the modeified concentration (peak/impulse)="+tlist1[x-1]);
	ki.println("%Time lists");
	
	ki.print("%tlist1=");
	for(int i=0; i<x; i++){
		ki.print(tlist1[i]+";");
	}
	ki.print("\n");

	ki.println("%");
	ki.println("%");
	
	ki.println("%Parameters used in the simulation");
	ki.println("%K(vizionszorzat)="+par.K);
	ki.println("%k_reak(viz disszociacio sebessegi allando)="+par.k_reak);
	ki.println("%L="+par.L);
	ki.println("%T="+par.T);
	ki.println("%D_h="+par.D_h);
	ki.println("%D_oh="+par.D_oh);
	ki.println("%D_k="+par.D_k);
	ki.println("%D_cl="+par.D_cl);
	ki.println("%K_fix="+par.K_fix);
	ki.println("%k_fix="+par.k_fix);
	ki.println("%c0_fa="+par.c0_fa);
	ki.println("%magick="+par.magick);
	ki.println("%R="+par.R);
	ki.println("%F="+par.F);
	
	ki.println("%");
	ki.println("%");

	//REKORDOK nevet ide kene majd kiirni    //TODO
	 
	ki.close();
}


void write()throws IOException
{
	File f=new File(name);
	FileWriter ki_stream = new FileWriter(f,true );
	PrintWriter ki = new PrintWriter(ki_stream);

	//Egy rekord kiirasa    //TODO



	ki.close();  
}




void finish() throws IOException
{

		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f, true ); //append-del lehet hozzafuzni
		PrintWriter ki = new PrintWriter(ki_stream);
	
		ki.println("%FINITO");
	
		ki.close();

}


}
