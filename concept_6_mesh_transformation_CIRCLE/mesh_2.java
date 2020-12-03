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

	double M = 0;
	double b_M = 0; 


	//ezeket a calculate_parameters_CIRCLE() fuggveny szamitja ki
	//A ponthoz tartozo kor
	double xA_c = 0;   //kozeppont - C koordinatai
	double yA_c = 0;

	double yA_x = 0;  // Y es Z pontok koordinatai  
	double yA_y = 0;  // kor es a ket erinto szakasz metszespontja
	double zA_x = 0;  // ezek kozott kell a kor fuggvenyebol venni a racsfuggvenyt
	double zA_y = 0;

	//B ponthoz tartozo kor
	double xB_c = 0;  //ugyanez reverse
	double yB_c = 0;

	double yB_x = 0;    
	double yB_y = 0;  
	double zB_x = 0;  
	double zB_y = 0;
	
	

	

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
		
		M = (mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont) / (B-A);
		b_M = (mesh_settings.x_kezdeti_pont*B - mesh_settings.x_vegpont*A) / (B-A);

		System.out.println("A="+A);
		System.out.println("B="+B);
		System.out.println("m="+m);
		System.out.println("b_e="+b_e);
		System.out.println("M="+M);
		System.out.println("b_M="+b_M);
	
	
		if(mesh_settings.function_type == 30) {

			this.calculate_parameters_CIRCLE();

			for(int i=1; i < mesh_settings.N ; i++){

				vtx_real[0][i] = Grid_Function_CIRCLE(vtx_ekvi[0][i]);			
			}

		}

		

		else {System.out.println("Unknown function type");}

	}

	double Grid_Function_CIRCLE(double x){

		double y = 0;
		int n = mesh_settings.N;

		
		
		
		//5 szakasz van
		if (x <= yA_x){ y = m*x; }
		else if (yA_x < x && x < zA_x){
			double b_cA = -2*yA_c;
			double c_cA = yA_c*yA_c - Math.pow(mesh_settings.r,2) + Math.pow((x-xA_c),2);

			y = (- b_cA + Math.sqrt(b_cA*b_cA - 4*c_cA) ) / 2;

		}
		else if (zA_x <= x && x <= yB_x){ y = M*x + b_M;}
		else if (yB_x < x && x < zB_x){
			double b_cB = -2*yB_c;
			double c_cB = yB_c*yB_c - Math.pow(mesh_settings.r,2) + Math.pow((x-xB_c),2);

			y = (- b_cB - Math.sqrt(b_cB*b_cB - 4*c_cB) ) / 2;

		}
		else if (zB_x <= x){ y = m*x + b_e;}
		else {}		



		return y;

}

	void calculate_parameters_CIRCLE(){
		
		double beta = Math.atan(m); // ennek elvileg 0 - pi/2 kozott kell lennie
		double gamma = Math.atan(M);  // 0 - pi/2 kozott van

		double alpha = Math.PI - beta + gamma;
		double d = mesh_settings.r / Math.sin(alpha/2);
		double l = Math.cos(alpha/2) * d;

		System.out.println("beta="+beta);
		System.out.println("gamma="+gamma);

		System.out.println("alpha="+alpha);
		System.out.println("d="+d);
		System.out.println("l="+l);

		// A ponthoz tartozo kor
		// x_coordinate(double mer, double b_e, double l, double x, double y, int tavol)
		yA_x = x_coordinate(m, 0, l, A, mesh_settings.x_kezdeti_pont, 0);
		yA_y = yA_x * m;

		zA_x = x_coordinate(M, b_M, l, A, mesh_settings.x_kezdeti_pont, 1);
		zA_y = zA_x * M + b_M;		

		System.out.println("yA_x="+yA_x);
		System.out.println("yA_y="+yA_y);

		System.out.println("zA_x="+zA_x);
		System.out.println("zA_y="+zA_y);			

		// B ponthoz tartozo kor
		yB_x = x_coordinate(M, b_M, l, B, mesh_settings.x_vegpont, 0);
		yB_y = yB_x * M + b_M;

		zB_x = x_coordinate(m, b_e, l, B, mesh_settings.x_vegpont, 1);
		zB_y = zB_x * m + b_e;

		System.out.println("yB_x="+yB_x);
		System.out.println("yB_y="+yB_y);

		System.out.println("zB_x="+zB_x);
		System.out.println("zB_y="+zB_y);	

		//kor kozeppontjanak a kiszamitasa
		//A-hoz tartozo szogfelezo egyenlete: sz1 -> y = m_sz1 * x + b_sz1

		double m_sz1 = Math.tan(beta + alpha/2);
		double b_sz1 = mesh_settings.x_kezdeti_pont - m_sz1 * A;

		xA_c = x_coordinate(m_sz1, b_sz1, d, A, mesh_settings.x_kezdeti_pont, 1);
		yA_c = m_sz1 * xA_c + b_sz1;

		System.out.println("xA_c="+xA_c);
		System.out.println("yA_c="+yA_c);	


		//B-hez tartozo szogfelezo egyenlete: sz2 -> y = m_sz2 * x + b_sz2
		
		double m_sz2 = Math.tan(Math.PI - alpha/2 + gamma);
		double b_sz2 = mesh_settings.x_vegpont - m_sz2 * B;

		xB_c = x_coordinate(m_sz2, b_sz2, d, B, mesh_settings.x_vegpont, 0);
		yB_c = m_sz2 * xB_c + b_sz2;

		System.out.println("xB_c="+xB_c);
		System.out.println("yB_c="+yB_c);
			
	
		
	}

	//Adott ponttol P, l tavolsagra elhelyezkedo , e egyenesen talalhato Z pont x koordinataja, int tavol fuggvenyeben jobbra vagy 		balra esik 
	// P: (x,y)
	// e: y=mer*x + b_mer
	// Z: (Zx,Zy)	
	double x_coordinate(double mer, double b_mer, double l, double x, double y, int tavol){
		
		double a = mer*mer +1;
		double b = -2*x + 2*(b_mer-y)*mer;
		double c = x*x + Math.pow((b_mer-y),2) - l*l;

		double x_coord = 0;
		
		//kozelebbi
		if(tavol==0){
			x_coord = (-b - Math.sqrt(b*b - 4*a*c)) / 2 / a;
		}
		//tavolabbi	
		else if(tavol==1){
			x_coord = (-b + Math.sqrt(b*b - 4*a*c)) / 2 / a;

		}


		return x_coord;		
	

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
