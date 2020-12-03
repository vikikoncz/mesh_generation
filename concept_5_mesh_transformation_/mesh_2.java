//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Ez fogja tudni a tenyleges mesh-t letrehozni; Racsfuggveny alljon most 5 szakaszbol
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

	double C = 0;  //segedvaltozok a kozepso egyenes szakaszhoz
	double D = 0;

	double M = 0;
	double beta = 0;

	double d=0;    //segedvaltozok a 
	double e_k=0;
	double a=0;
	double b=0;
	double c=0;	

	double ac_n=0;

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

		//segedvaltozok a kozepso egyeneshez
		D = (A + B + mesh_settings.ratio * mesh_settings.R) / 2;
		C = A + B - D; 
		
		M = (mesh_settings.x4 - mesh_settings.x3) / (D - C);
		beta = (mesh_settings.x3 * D - mesh_settings.x4 * C) / (D - C);

	
		System.out.println("A="+A);
		System.out.println("B="+B);
		System.out.println("m="+m);
		System.out.println("b_e="+b_e);
		System.out.println("D="+D);
		System.out.println("C="+C);
		System.out.println("M="+M);
		System.out.println("beta="+beta);
		

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
		
	
		//5 szakasz van
		if(x <= A) {y = m * x;}
		else if (x > A && x < C){

			//y = a * (c * (Math.pow((x-e_k),n))) + b * (c * (x-e_k)) + D;
			y = ac_n * Math.pow((x-e_k),n) + d;

		}
		else if (x >= C && x <= D){
			y = beta + M * x;
		}
		
		else if (x > D && x < B){

			y = ac_n * Math.pow((x-e_k),n) + d;
			
		}
		else if (x >= B){y = m * x + b_e;}

		return y;


	}


	void calculate_parameters_xn(){

		e_k = (A + B) / 2;
		d = (mesh_settings.x_kezdeti_pont + mesh_settings.x_vegpont) / 2;

		double x2 = mesh_settings.x_vegpont;
		double x1 = mesh_settings.x_kezdeti_pont;
		int n = mesh_settings.n;
		double x3 = mesh_settings.x3;
		double x4 = mesh_settings.x4;
		
		//a,b,c, parameterek szamitasa -> ehhez lin. (-ra vezető) egyenletrendszert kell megoldani  
		// Peremfeltetelek: fuggvenyertek megegyezik, ill. derivaltak is megegyeznek
		/* Levezetes a reszletes miki - egeres fuzetemben talalhato*/
		double ac_n_1 = (x3 - x1) / (Math.pow((C-e_k),n) - Math.pow((A-e_k),n));
		double ac_n_2 = m / n / Math.pow((A-e_k),n-1);
		double ac_n_3 = M / n / Math.pow((C-e_k),n-1);

		System.out.println("ac_n_1="+ac_n_1);
		System.out.println("ac_n_2="+ac_n_2);
		System.out.println("ac_n_3="+ac_n_3);	

		System.out.println("x1_calc="+(ac_n_1*Math.pow((A-e_k),n)+d));
		System.out.println("x3_calc="+(ac_n_1*Math.pow((C-e_k),n)+d));
		System.out.println("x_zero_point="+(ac_n_1*Math.pow(((A+B)/2-e_k),3)+d));

		System.out.println("e_k="+e_k);
		
		ac_n = ac_n_1;
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
