//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Ez fogja tudni a tenyleges mesh-t letrehozni
*/

public class mesh_2{

	double [][] vtx_ekvi;   //ezek lesznek az ekvidisztans pontok
	double [][] vtx_real;  //ezek a valodi attranszformalt mesh pontok  
	int [][] edge;   

	mesh_settings mesh_settings;
	parameters par;

	double A=0;  //ket szelen a ket linearis szakasz
	double B=0; 
	
	double m=0;   //segedvaltozok a ket egyenes szakaszhoz
	double b_e=0;	

	double D=0;    //segedvaltozok a 
	double e_k=0;
	double a_cn=0;
	double bc=0;
	

//Konstruktor
	mesh_2(mesh_settings mesh_settings, parameters par){
		this.mesh_settings=mesh_settings;
		this.par=par;
		this.generate_vtx_ekvi();
	}

//Functions
	void generate_vtx_ekvi(){
		
		vtx_ekvi=new double [1][mesh_settings.N+1]; 
		
		vtx_ekvi[0][0]=0;
		vtx_ekvi[0][mesh_settings.N]=par.L; 

		double h = (double) par.L / mesh_settings.N ; 
		
		for(int i=1; i < mesh_settings.N ; i++){
			vtx_ekvi[0][i] = (double) h*i;			

		}

	}


	void transform(){

		vtx_real=new double [1][mesh_settings.N+1]; 
		
		vtx_real[0][0]=0;
		vtx_real[0][mesh_settings.N]=par.L; 

		//Figure out scaling paramteres

		A = mesh_settings.x_kezdeti_pont * (1-mesh_settings.ratio) / (1-mesh_settings.x_vegpont + mesh_settings.x_kezdeti_pont);
		B = mesh_settings.ratio + A;

		//segedvaltozok a ket egyenes szakaszhoz
		m = mesh_settings.x_kezdeti_pont / A;
		b_e = 1-m;

		//xn-es
		// kozepso szakasza a racsfuggvenynek be van skalazva, 
		// f(x) = a * x^n + b*x alaku
		//      = a * [c*(x - e_k)]^n + b * [c*(x - e_k)] + D	 	
		//
		if(mesh_settings.function_type == 0) {

			this.calculate_parameters_xn();

			for(int i=1; i < mesh_settings.N ; i++){

				vtx_real[0][i] = Grid_Function_xn(vtx_ekvi[0][i]);			
			}

		}

		

		else {System.out.println("Unknown function type");}

	}

	double Grid_Function_xn(double x){

		double y = 0;
		int n = mesh_settings.n;
	
		//3 szakasz van
		if(x <= A) {y = m * x;}
		else if (x > A && x < B){

			//y = a * (c * (Math.pow((x-e_k),n))) + b * (c * (x-e_k)) + D;
			y = a_cn * (Math.pow((x-e_k),n)) + bc * (x-e_k) + D;

		}
		else if (x >= B){y = m * x + b_e;}

		return y;


	}


	void calculate_parameters_xn(){

		e_k = (A + B) / 2;
		D = (mesh_settings.x_kezdeti_pont + mesh_settings.x_vegpont) / 2;

		double x2 = mesh_settings.x_vegpont;
		double x1 = mesh_settings.x_kezdeti_pont;
		int n = mesh_settings.n;
		
		//a,b,c, parameterek szamitasa -> ehhez lin. (-ra vezető) egyenletrendszert kell megoldani  
		// Peremfeltetelek: fuggvenyertek megegyezik, ill. derivaltak is megegyeznek
		/* Levezetes a reszletes miki - egeres fuzetemben talalhato*/
		
		// III. equation * (A - e_k)

		a_cn = (m * (A - e_k) - (x1 - x2)/2) / (n-1) / Math.pow((A - e_k),n);

		bc = m - n * a_cn * Math.pow((A - e_k),(n-1));		


		System.out.println("a_cn="+a_cn);
		System.out.println("bc="+bc);
		//ystem.out.println("c="+c);

		System.out.println("m="+m);
		System.out.println("A="+A);
		System.out.println("B="+B);
		
		//System.out.println("m_minusz="+(n * a_cn * c * Math.pow((A-e_k),(n-1))));

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


	void write_mesh_vtx_FILE() throws IOException{
		String name="mesh.dat";	
		File f=new File(name);
		FileWriter ki_stream = new FileWriter(f);
		PrintWriter ki = new PrintWriter(ki_stream);

		//HEADER
		ki.print("%Szakasz_index"+"\t");
		ki.print("VTX_ekvi"+"\t");
		ki.print("VTX_real"+"\t");
		ki.print("mesh_density"+"\n"); //L=1

		int n=vtx_real[0].length-1;
		
		for (int i=0; i<n; i++){
			double mesh_density=1/(vtx_real[0][i+1] - vtx_real[0][i]); 
			double x_coord=vtx_real[0][i];
			double x_ekvi=vtx_ekvi[0][i];
			ki.println(i+"\t"+x_ekvi+"\t"+x_coord+"\t"+mesh_density);

		}
		
		ki.println(n+"\t"+vtx_ekvi[0][n]+"\t"+vtx_real[0][n]);	
		


		ki.close();
		
		
	}
	
	void check_vtx(){
		int n=vtx_real[0].length-1;
		
		int proba=0;

		for (int i=0; i<n; i++){
			if(vtx_real[0][i+1]<vtx_real[0][i]){proba=1;}	
		}

		if(proba==0){System.out.println("VTX SZIG MON NO");}
		else{System.out.println("VTX NOT GOOD");}
	}


}
