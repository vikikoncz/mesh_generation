//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Ez fogja tudni a tenyleges mesh-t letrehozni
*/

public class mesh{
	double [][] vtx;  //ezek attranszformalva az x1 - x2 zonaba; meg mindig 200e pont
	double [][] vtx_real;  //200e pontbol mintavetelezessel osszeallt a tenyleges pontszam 
	int [][] edge;   //egyes pontok osszerendeleset vegzi
	double [] vtx_scaled; //kritikus zonaba generalt mesh pontok; 0 koruli intervallum
	double [] vtx_ekvi;

	mesh_settings mesh_settings;
	parameters par;

	mesh(mesh_settings mesh_settings, parameters par){
		this.mesh_settings=mesh_settings;
		this.par=par;
		this.generate_vtx_scaled();
	}

	void generate_vtx_scaled(){
		
		vtx_scaled=new double [mesh_settings.n_kritikus_zona+1];
		vtx_scaled[0]=mesh_settings.x1_scaled;
		vtx_scaled[mesh_settings.n_kritikus_zona]=mesh_settings.x2_scaled;
		int current_i=0;
		for(int i=1; i<mesh_settings.n_kritikus_zona; i++){
			double x_i_prev = vtx_scaled[i-1];  
			double d_i = 1 / mesh_settings.get_mesh_density(x_i_prev);
			vtx_scaled[i] = vtx_scaled[i-1] + d_i;
			current_i=i;
			if(vtx_scaled[i]>mesh_settings.x2_scaled){
				current_i=current_i-1;
				System.out.println("Cutted points="+(mesh_settings.n_kritikus_zona - current_i +1));
				break;
			}	
		}
		//utolso pont visszaallitasa
		vtx_scaled[current_i+1]=mesh_settings.x2_scaled;
		double diff = vtx_scaled[mesh_settings.n_kritikus_zona] - vtx_scaled[mesh_settings.n_kritikus_zona-1];
		System.out.println("Diff="+diff);
		
		//TODO   - lehet valami threshold-ot beállítani
		

	}


	//Ez hivja majd meg a beallitasok alapjan a tenyleges VTX eloallito metodust
	// generate_edge()-nek eleg egyszer, vagyis itt szerepelnie
	void generate(){
		
		int n_lepcso = mesh_settings.n_kritikus_zona;  //itt gond lehet meg a kerekitessel
		int n_alap = mesh_settings.n_alap;
		
		vtx=new double [1][mesh_settings.n_mesh+1];   //itt ezzel lehet gond szerintem
		vtx[0][0]=0;
		vtx[0][mesh_settings.n_mesh]=par.L; //TODO par osztaly-t is lathatova kell tenni
		
		vtx_ekvi=new double [mesh_settings.n_mesh+1];
		vtx_ekvi[0]=0;
		vtx_ekvi[mesh_settings.n_mesh]=par.L;
		
		double h_ekvi = 1.0/mesh_settings.n_mesh;
		for(int i=1; i<mesh_settings.n_mesh; i++){
			vtx_ekvi[i]=i*h_ekvi;
		}

		System.out.println("vtx[0][0]="+vtx[0][0]);
		System.out.println("vtx[0][mesh_settings.n_mesh]="+vtx[0][mesh_settings.n_mesh]);
		
		
		double kezdeti_pont=mesh_settings.x_kezdeti_pont;    //mesh surites (lepcso) kezdeti pontja
		double veg_pont=mesh_settings.x_vegpont;   //mesh surites (lepcso) utolso pontja
		
//TODO	ezt a reszt mesh_settings alapjan update-elni	
		int lepcso_bal=(int)Math.rint(n_alap*(kezdeti_pont/(par.L-(veg_pont-kezdeti_pont))));  //egeszosztas marad 	
		int lepcso_jobb=lepcso_bal+n_lepcso;
		int veg=n_alap-lepcso_bal;
//
		
		double r_veg=par.L-veg_pont;  //lepcso-tol jobbra eso resz hossza
		
		//Elso resz bepakolasa a VTX tombbe, ez a lepcso elotti resz
		int h=1;
		
		System.out.println("lepcso_bal="+lepcso_bal);
		double diff_alap=1/mesh_settings.ro_alap;
	
		for(int i=1;i<lepcso_bal;i++){
			vtx[0][i]=(double)diff_alap*h; 
			h++;		
		} 	

		double a = (veg_pont - kezdeti_pont) / (mesh_settings.x2_scaled - mesh_settings.x1_scaled); 
		double b = (-veg_pont*mesh_settings.x1_scaled + kezdeti_pont*mesh_settings.x2_scaled ) / (mesh_settings.x2_scaled - mesh_settings.x1_scaled); 
		
		int j=0;
		for(int i=lepcso_bal; i<=lepcso_jobb; i++){
			vtx[0][i] = a * vtx_scaled[j] + b;
			j++;
		}		

		int current_i = lepcso_jobb; 

		diff_alap=(double)r_veg/(mesh_settings.n_mesh-current_i);
		//Utolso, lepcso utani resz bepakolasa a VTX tombbe
		h=1; 	
		for(int i=current_i+1;i<mesh_settings.n_mesh;i++){
			vtx[0][i]=(double)diff_alap*h+vtx[0][current_i];
			h++;	
		}	
		

		//VTX pontjainak egy reszet kihagyni

		
		vtx_real=new double [1][mesh_settings.n_mesh_real+1];
		vtx_real[0][0]=vtx[0][0];
		vtx_real[0][mesh_settings.n_mesh_real]=vtx[0][mesh_settings.n_mesh];

		int n_scale_factor = mesh_settings.n_mesh / mesh_settings.n_mesh_real;
		//WARNING, mi van, ha ez nem egesz!!!

		for (int i=1; i < mesh_settings.n_mesh_real; i++){
			vtx_real[0][i]=vtx[0][i * n_scale_factor];
		} 



		this.generate_edge();
	}

	//Edge generalas - ez mindig igy mukodik
	void generate_edge(){
		int n_pont=vtx_real[0].length-1;
		edge=new int [2][n_pont];
		for(int i=0;i<edge[0].length;i++){
			for(int j=0;j<edge.length;j++){
				if(j==0) edge[j][i]=i;			
				else edge[j][i]=i+1;			
			}
		}
	}

	

	
	

	//Debugging purposes -> checking mesh_steps


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

	

	void write_mesh_vtx_density_FILE() throws IOException{
		String name="mesh_edge_denstity_1.dat";	
		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f);
		PrintWriter ki = new PrintWriter(ki_stream);

		//HEADER
		ki.print("%Szakasz_index"+"\t");
		ki.print("x_coord"+"\t");
		ki.print("mesh_density"+"\n"); //L=1

		int n=vtx_real[0].length-1;
		
		for (int i=0; i<n; i++){
			double mesh_density=1/(vtx_real[0][i+1] - vtx_real[0][i]); 
			double x_coord=(vtx_real[0][i+1] + vtx_real[0][i]) / 2;
			ki.println(i+"\t"+x_coord+"\t"+mesh_density);

		}	
		


		ki.close();
	}

	void write_mesh_vtx_FILE() throws IOException{
		String name="mesh_coord_denstity_1.dat";	
		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f);
		PrintWriter ki = new PrintWriter(ki_stream);

		//HEADER
		ki.print("%Szakasz_index"+"\t");
		ki.print("VTX"+"\t");
		ki.print("mesh_density"+"\n"); //L=1

		int n=vtx_real[0].length-1;
		
		for (int i=0; i<n; i++){
			double mesh_density=1/(vtx_real[0][i+1] - vtx_real[0][i]); 
			double x_coord=vtx_real[0][i];
			ki.println(i+"\t"+x_coord+"\t"+mesh_density);

		}
		
		ki.println(n+"\t"+vtx_real[0][n]);	
		


		ki.close();
	}


	//Check VTX scaled

	void check_vtx_scaled(){
		int n=vtx_scaled.length-1;
		
		int proba=0;

		for (int i=0; i<n; i++){
			if(vtx_scaled[i+1]<vtx_scaled[i]){proba=1;}	
		}

		if(proba==0){System.out.println("VTX SZIG MON NO");}
		else{System.out.println("VTX NOT GOOD");}
	}

	

	void write_mesh_vtx_scaled_density_FILE() throws IOException{
		String name="mesh_edge_denstity_scaled_1.dat";	
		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f);
		PrintWriter ki = new PrintWriter(ki_stream);

		//HEADER
		ki.print("%Szakasz_index"+"\t");
		ki.print("x_coord"+"\t");
		ki.print("mesh_density"+"\n"); //L=1

		int n=vtx_scaled.length-1;
		
		for (int i=0; i<n; i++){
			double mesh_density=1/(vtx_scaled[i+1] - vtx_scaled[i]); 
			double x_coord=(vtx_scaled[i+1] + vtx_scaled[i]) / 2;
			ki.println(i+"\t"+x_coord+"\t"+mesh_density);

		}	
		


		ki.close();
	}

	void write_mesh_vtx_scaled_FILE() throws IOException{
		String name="mesh_coord_denstity_scaled_1.dat";	
		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f);
		PrintWriter ki = new PrintWriter(ki_stream);

		//HEADER
		ki.print("%Szakasz_index"+"\t");
		ki.print("VTX"+"\t");
		ki.print("mesh_density"+"\n"); //L=1

		int n=vtx_scaled.length-1;
		
		for (int i=0; i<n; i++){
			double mesh_density=1/(vtx_scaled[i+1] - vtx_scaled[i]); 
			double x_coord=vtx_scaled[i];
			ki.println(i+"\t"+x_coord+"\t"+mesh_density);

		}
		
		ki.println(n+"\t"+vtx_scaled[n]);	
		


		ki.close();
	}

	void write_mesh_gridFunction() throws IOException{
		
		String name="mesh_grid_function.dat";	
		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f);
		PrintWriter ki = new PrintWriter(ki_stream);

		//HEADER
		ki.print("%Szakasz_index"+"\t");
		ki.print("VTX_ekvi"+"\t");
		ki.print("VTX_real"+"\t");
		ki.print("mesh_density"+"\n"); //L=1
		
		int n=mesh_settings.n_mesh;
		
		for (int i=0; i<n; i++){
			double mesh_density=1/(vtx[0][i+1] - vtx[0][i]); 
			ki.println(i+"\t"+vtx_ekvi[i]+"\t"+vtx[0][i]+"\t"+mesh_density);
		}
		
		ki.println(n+"\t"+vtx_ekvi[n]+"\t"+vtx[0][n]);	
		


		ki.close();
		
	}

	
}
