//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Ez fogja tudni a tenyleges mesh-t letrehozni
*/

public class mesh{
	double [][] vtx;
	int [][] edge;
	mesh_lepcsofok [] mesh_steps;
	mesh_settings mesh_settings;
	parameters par;

	mesh(mesh_settings mesh_settings, parameters par){
		this.mesh_settings=mesh_settings;
		this.par=par;
	}


	//Ez hivja majd meg a beallitasok alapjan a tenyleges VTX eloallito metodust
	// generate_edge()-nek eleg egyszer, vagyis itt szerepelnie
	void generate(){
		
		int n_lepcso = (int)Math.round(mesh_settings.n_mesh * mesh_settings.n_lepcso_ossz_arany);  //itt gond lehet meg a kerekitessel
		int n_alap = mesh_settings.n_mesh - n_lepcso;
		
		vtx=new double [1][mesh_settings.n_mesh+1];   //itt ezzel lehet gond szerintem
		vtx[0][0]=0;
		vtx[0][mesh_settings.n_mesh]=par.L; //TODO par osztaly-t is lathatova kell tenni
		
		
		double kezdeti_pont=mesh_settings.x_kezdeti_pont;    //mesh surites (lepcso) kezdeti pontja
		double veg_pont=mesh_settings.x_vegpont;   //mesh surites (lepcso) utolso pontja
		
		int lepcso_bal=(int)Math.rint(n_alap*(kezdeti_pont/(par.L-(veg_pont-kezdeti_pont))));  //egeszosztas marad 	
		int lepcso_jobb=lepcso_bal+n_lepcso;
		int veg=n_alap-lepcso_bal;
		
		double r_veg=par.L-veg_pont;  //lepcso-tol jobbra eso resz hossza
		
		//Elso resz bepakolasa a VTX tombbe, ez a lepcso elotti resz
		int h=1;
	
		for(int i=1;i<lepcso_bal;i++){
			vtx[0][i]=(double)kezdeti_pont/lepcso_bal*h; 
			h++;		
		} 	
		
		
			
		
		this.calculate_mesh_steps(lepcso_bal, lepcso_jobb);	


	//Lepcso bepakolasa a VTX tombbe
		for(int j=0; j<mesh_settings.n_lepcsok_szama; j++){
			h=1;
			for(int i=mesh_steps[j].bal_step ; i<mesh_steps[j].jobb_step ; i++){
				vtx[0][i]=(double)mesh_steps[j].r_step/mesh_steps[j].n_step*h+vtx[0][mesh_steps[j].bal_step-1];
				h++;
			}	
		}
		
		

		//Utolso, lepcso utani resz bepakolasa a VTX tombbe
		h=1; 	
		for(int i=lepcso_jobb;i<mesh_settings.n_mesh;i++){
			vtx[0][i]=(double)r_veg/veg*h+vtx[0][lepcso_jobb-1];
			h++;	
		}	
		
		this.generate_edge();
	}

	//Edge generalas - ez mindig igy mukodik
	void generate_edge(){
		int n_pont=vtx[0].length-1;
		edge=new int [2][n_pont];
		for(int i=0;i<edge[0].length;i++){
			for(int j=0;j<edge.length;j++){
				if(j==0) edge[j][i]=i;			
				else edge[j][i]=i+1;			
			}
		}
	}
	
	//Mesh lepcsok parametereit kitalalja - mi hogyan menjen
	void calculate_mesh_steps(int lepcso_bal, int lepcso_jobb){
		
		int n_lepcso = (int)Math.round(mesh_settings.n_mesh * mesh_settings.n_lepcso_ossz_arany); // itt gond lehet meg a kerekitessel
		
		//Paratlan szamu lepcsofok legyen megadva!!!
		if(mesh_settings.n_lepcsok_szama%2==0){
			System.out.println("WARNING: MESH lepcsok szama paros!!! Meg lett novelve 1-gyel");
			mesh_settings.n_lepcsok_szama=mesh_settings.n_lepcsok_szama+1;	
		}
		
		mesh_steps=new mesh_lepcsofok[mesh_settings.n_lepcsok_szama];
		for(int i=0;i<mesh_steps.length;i++){
			mesh_steps[i]=new mesh_lepcsofok();
		}
		System.out.println("mesh_steps.length="+mesh_steps.length);

		int index_kozepso=(mesh_settings.n_lepcsok_szama-1)/2;    //Kozepso lepcsofok, ahol a MAX van, annak az indexe
		System.out.println("index_kozepso="+index_kozepso);
		
		int mesh_density_level=index_kozepso+1;  //szerintem erre nincs szukseg
		
		double egyutthato_n_step=1;
		double egyutthato_szelesseg=1;
		
		for(int i=1; i<mesh_density_level; i++){
			//double add=2*(mesh_settings.n_step_decrease_factor)^i;
			double add_n_step=2*Math.pow(mesh_settings.n_step_decrease_factor,i);
			egyutthato_n_step+=add_n_step;
			//System.out.println("add_n_step="+add_n_step);

			double add_szelesseg=2*Math.pow(mesh_settings.lepcso_szelesseg_factor,i);
			egyutthato_szelesseg+=add_szelesseg;			
		}
		
		int n_max=(int)Math.round(n_lepcso/egyutthato_n_step); //itt is lehetnek persze kerekitesi problemak
		
		System.out.println("egyutthato_n_step="+egyutthato_n_step);
		//mesh_steps=new mesh_lepcsofok[mesh_settings.n_lepcsok_szama];
		System.out.println("n_lepcsok_szama="+mesh_settings.n_lepcsok_szama);
		
		System.out.println("n_max="+n_max);
		//mesh_steps[0].set_n_step(1);
		
		mesh_steps[index_kozepso].n_step=n_max;
		
		//Kiszámolni a tobbi lepcsobe kerulo elemszamot
		
		int N_szumma_check=n_max; //INTEGER-re valo konverzio miatt lehetnek elteresek; meg kell nezni, h mennyi lett az ossz elemszam; ez alapjan javitani
		
		for(int i=1; i<mesh_density_level; i++){
			//Itt megint lehet gond az INTEGER-re valo kerekitesben, konverzioban
			//Math.pow(x,i) hatvanyozas
			int n_i=(int)Math.round(n_max*Math.pow(mesh_settings.n_step_decrease_factor,i)); //aktualis lepcsofokba kerulo elemek szama
			
			mesh_steps[index_kozepso-i].n_step=n_i;
			mesh_steps[index_kozepso+i].n_step=n_i;
			
			N_szumma_check+=2*n_i;
		}
		
		System.out.println("N_szumma_check="+N_szumma_check);
		
		//KORREKCIO , ha szukseges
		if(N_szumma_check!=n_lepcso){
			int n_lepcso_difference=n_lepcso - N_szumma_check; //elteresek
			System.out.println("n_lepcso_difference="+n_lepcso_difference);
			
			//Itt mas fajta korrekcio is elkepzelheto  - ez meg rossz
			//mesh_steps[index_kozepso].n_step = mesh_steps[index_kozepso].n_step 
		}

		//Egyes elemek szelesseget szamoljuk ki:
		//kozepso
		double dense_width=mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont;
		
		double d_kozepso = dense_width/egyutthato_szelesseg;

		mesh_steps[index_kozepso].r_step=d_kozepso;

		System.out.println("d_kozepso="+d_kozepso);
		System.out.println("egyutthato_szelesseg="+egyutthato_szelesseg);

		for(int i=1; i<mesh_density_level; i++){
			double d_i=d_kozepso*Math.pow(mesh_settings.lepcso_szelesseg_factor,i);
	
			mesh_steps[index_kozepso-i].r_step=d_i;
			mesh_steps[index_kozepso+i].r_step=d_i;		
		}	

		//mesh_lepcsofok : x_bal_step, x_jobb_step reszenek a szamitasa

		int j=mesh_settings.n_lepcsok_szama-1;  //lepcsok tombjenek utolso koordinataja
		mesh_steps[0].x_bal_step=mesh_settings.x_kezdeti_pont;

		for(int i=0; i<j; i++){
			mesh_steps[i].x_jobb_step=mesh_steps[i].x_bal_step + mesh_steps[i].r_step;
			mesh_steps[i+1].x_bal_step=mesh_steps[i].x_jobb_step;
		}
		
		mesh_steps[j].x_jobb_step=mesh_settings.x_vegpont;

		//mesh_lepcsofok: bal_step, jobb_step valtozok kitalalasa

		mesh_steps[0].bal_step=lepcso_bal; 
	
		for(int i=0; i<j; i++){
			mesh_steps[i].jobb_step=mesh_steps[i].bal_step + mesh_steps[i].n_step;
			mesh_steps[i+1].bal_step=mesh_steps[i].jobb_step;
		}	

		mesh_steps[j].jobb_step=lepcso_jobb;   
			
	
	
	}	

	//Debugging purposes -> checking mesh_steps
	void write_mesh_steps(){
		System.out.println("Writting MESH_STEPS");
		
		int n=mesh_steps.length;
		for (int i=0; i<n; i++){
			System.out.println("Step="+i);
			System.out.println("\t"+"n_step="+mesh_steps[i].n_step);
			System.out.println("\t"+"bal_step="+mesh_steps[i].bal_step);
			System.out.println("\t"+"jobb_step="+mesh_steps[i].jobb_step);
			System.out.println("\t"+"r_step="+mesh_steps[i].r_step);
			System.out.println("\t"+"x_bal_step="+mesh_steps[i].x_bal_step);
			System.out.println("\t"+"x_jobb_step="+mesh_steps[i].x_jobb_step);

		}
	}


	void write_mesh_vtx(){
		System.out.println("Writting VTX");

		int n=vtx[0].length;
		for (int i=0; i<n; i++){
			System.out.println(i+"\t"+vtx[0][i]);

		}			
	}

	void check_vtx(){
		int n=vtx[0].length-1;
		
		int proba=0;

		for (int i=0; i<n; i++){
			if(vtx[0][i+1]<vtx[0][i]){proba=1;}	
		}

		if(proba==0){System.out.println("VTX SZIG MON NO");}
		else{System.out.println("VTX NOT GOOD");}
	}

	void write_mesh_steps_FILE() throws IOException{
		String name="mesh_steps_1.dat";	
		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f);
		PrintWriter ki = new PrintWriter(ki_stream);	

		//HEADER
		ki.print("%Step number"+"\t");
		ki.print("n_step"+"\t");
		ki.print("bal_step"+"\t");
		ki.print("jobb_step"+"\t");
		ki.print("r_step"+"\t");
		ki.print("x_bal_step"+"\t");
		ki.print("x_jobb_step"+"\n");	

		int n=mesh_steps.length;
		for (int i=0; i<n; i++){
			ki.print(i);
			ki.print("\t"+mesh_steps[i].n_step);
			ki.print("\t"+mesh_steps[i].bal_step);
			ki.print("\t"+mesh_steps[i].jobb_step);
			ki.print("\t"+mesh_steps[i].r_step);
			ki.print("\t"+mesh_steps[i].x_bal_step);
			ki.print("\t"+mesh_steps[i].x_jobb_step+"\n");

		} 		

		ki.close();
	}

	void write_mesh_vtx_FILE() throws IOException{
		String name="mesh_denstity_1.dat";	
		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f);
		PrintWriter ki = new PrintWriter(ki_stream);

		//HEADER
		ki.print("%Szakasz_index"+"\t");
		ki.print("x_coord"+"\t");
		ki.print("mesh_density"+"\t"); //L=1

		int n=vtx[0].length-1;
		
		for (int i=0; i<n; i++){
			double mesh_density=1/(vtx[0][i+1] - vtx[0][i]); 
			double x_coord=(vtx[0][i+1] + vtx[0][i]) / 2;
			ki.println(i+"\t"+x_coord+"\t"+mesh_density);

		}	
		


		ki.close();
	}

	
}
