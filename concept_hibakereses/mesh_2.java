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
	double b_m=0;	

	double M = 0;
	double b_M = 0; 


	//ezeket a calculate_parameters_EXPON() fuggveny szamitja ki
	//A ponthoz tartozo EXPON
	

	double yA_x = 0;  // Y es Z pontok koordinatai  
	//double yA_y = 0;  // kor es a ket erinto szakasz metszespontja
	double zA_x = 0;  // ezek kozott kell a kor fuggvenyebol venni a racsfuggvenyt
	//double zA_y = 0;

	// exp parameterei
	double A_expA = 0;
	double c_expA = 0;
	double d_expA = 0;

	

	//B ponthoz tartozo EXPON
	

	double yB_x = 0;    
	//double yB_y = 0;  
	double zB_x = 0;  
	//double zB_y = 0;
		
	// exp parameterei
	double A_expB = 0;
	double c_expB = 0;
	double d_expB = 0;

	

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
		b_m = 1-m;

		//segedvaltozok a kozepso egyeneshez
		
		M = (mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont) / (B-A);
		b_M = (mesh_settings.x_kezdeti_pont*B - mesh_settings.x_vegpont*A) / (B-A);

		System.out.println("A="+A);
		System.out.println("B="+B);
		System.out.println("m="+m);
		System.out.println("b_m="+b_m);
		System.out.println("M="+M);
		System.out.println("b_M="+b_M);
	
	
		if(mesh_settings.function_type == 100) {

			this.calculate_parameters_EXPON();

			for(int i=1; i < mesh_settings.N ; i++){

				vtx_real[0][i] = Grid_Function_EXPON(vtx_ekvi[0][i]);			
			}

		}

		

		else {System.out.println("Unknown function type");}

	}

	double Grid_Function_EXPON(double x){

		double y = 0;
		int n = mesh_settings.N;

		
		
		
		//5 szakasz van
		if (x <= yA_x){ y = m*x; }
		else if (yA_x < x && x < zA_x){
			
			y = A_expA * Math.exp(c_expA * (x-zA_x)) + d_expA;	

		}
		else if (zA_x <= x && x <= yB_x){ y = M*x + b_M;}
		else if (yB_x < x && x < zB_x){
			
			y = A_expB * Math.exp(c_expB * (x-zB_x)) + d_expB;
		}
		else if (zB_x <= x){ y = m*x + b_m;}
		else {}		



		return y;

}

	void calculate_parameters_EXPON(){
		
		//A oldal
		c_expA = Math.log(M / m) / mesh_settings.delta_x;
		A_expA = M / c_expA;

		yA_x = (A_expA * (Math.exp(c_expA * (-1) * mesh_settings.delta_x) - 1) + M * mesh_settings.delta_x + b_M ) / (m - M);
		zA_x = yA_x + mesh_settings.delta_x;
		
		d_expA = M * zA_x + b_M - A_expA;

		System.out.println("yA_x="+yA_x);
		System.out.println("zA_x="+zA_x);
		System.out.println("c_expA="+c_expA);
		System.out.println("A_expA="+A_expA);
		System.out.println("d_expA="+d_expA);
	

		//B oldal
		c_expB = (-1) * c_expA;
		A_expB = m / c_expB;

		yB_x = (A_expB * (Math.exp(c_expB * (-1) * mesh_settings.delta_x) - 1) + m * mesh_settings.delta_x + b_m - b_M) / (M - m); 
		zB_x = yB_x + mesh_settings.delta_x;
		
		d_expB = m * zB_x + b_m - A_expB;		
		
		System.out.println("yB_x="+yB_x);
		System.out.println("zB_x="+zB_x);
		System.out.println("c_expB="+c_expB);
		System.out.println("A_expB="+A_expB);
		System.out.println("d_expB="+d_expB);
	
		
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
