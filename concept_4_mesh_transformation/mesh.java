//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Ez fogja tudni a tenyleges mesh-t letrehozni
*/

public class mesh{

	double [][] vtx_ekvi;   //ezek lesznek az ekvidisztans pontok
	double [][] vtx_real;  //ezek a valodi attranszformalt mesh pontok  
	int [][] edge;   

	double A=0;  //ket szelen a ket linearis szakasz
	double B=0; 	

	double a=0;  //eltolashoz stb. szukseges
	double b=0;	
	
	double NEVEZO=0;
	double SZAMLALO=0;
	double d=0;
	double S=0;

	double p = 0;
	double q = 0;
	
	double r = 0;
	double s = 0;
	
	double d_faktor = 0.002;

	mesh_settings mesh_settings;
	parameters par;

	mesh(mesh_settings mesh_settings, parameters par){
		this.mesh_settings=mesh_settings;
		this.par=par;
		this.generate_vtx_ekvi();
	}


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

		A = mesh_settings.x_kezdeti_pont * (1-mesh_settings.ratio) / (1-mesh_settings.x_vegpont + mesh_settings.x_kezdeti_pont);
		B = mesh_settings.ratio + A;	

		System.out.println("A="+A);
		System.out.println("B="+B);

		a = 2 / (mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont);
		b = (mesh_settings.x_vegpont + mesh_settings.x_kezdeti_pont)/(mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont);
		
		d = (1-mesh_settings.ratio) / (1-B+A);
		
		SZAMLALO = mesh_settings.ratio - d*(mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont);
		double NEVEZO_3 = -a*a/3*(Math.pow(mesh_settings.x_vegpont,3)-Math.pow(mesh_settings.x_kezdeti_pont,3));
		double NEVEZO_2 = 2*a*b*(Math.pow(mesh_settings.x_vegpont,2)-Math.pow(mesh_settings.x_kezdeti_pont,2));
		double NEVEZO_1 = (1-b*b)*(mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont);
		NEVEZO = NEVEZO_3 + NEVEZO_2 + NEVEZO_1;
			
		S=SZAMLALO/NEVEZO;
		
		p = 2 / (B-A);
		q = - (A+B) / (B-A);	
		
		//scaling parameters
		double y_A = arctanh( (q+p*A) / Math.sqrt(d_faktor+1)) / Math.sqrt(d_faktor+1);
		double y_B = arctanh( (q+p*B) / Math.sqrt(d_faktor+1)) / Math.sqrt(d_faktor+1);	
		
		r = (mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont) / (y_B - y_A);
		s = (mesh_settings.x_kezdeti_pont * y_B - mesh_settings.x_vegpont * y_A) / (y_B - y_A);
		
		double A_scaled = q+p*A;
		double B_scaled = q+p*B;
		
		System.out.println("A_scaled="+A_scaled);
		System.out.println("B_scaled="+B_scaled);
		
		System.out.println(arctanh((q+p*A) / Math.sqrt(d_faktor+1)));
		System.out.println((q+p*B) / Math.sqrt(d_faktor+1));
		
		System.out.println(arctanh(0.8770580193070292));
		
		System.out.println("y_A="+y_A);
		System.out.println("y_B="+y_B);
		
		System.out.println("r="+r);
		System.out.println("s="+s);
		
		//x2-es
		if(mesh_settings.function_type == 0) {

			for(int i=1; i < mesh_settings.N ; i++){
				vtx_real[0][i] = Grid_Function_x2(vtx_ekvi[0][i]);			
			}

		}

		else if(mesh_settings.function_type == 1) {
				for(int i=1; i < mesh_settings.N ; i++){
				vtx_real[0][i] = Grid_Function_x3(vtx_ekvi[0][i]);			
			}
			
		}

		else {System.out.println("Unknown function type");}

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

	double Grid_Function_x3(double x){
		
		double y =0;
		double y_unscaled=0;	
		
		double r_x3 = (mesh_settings.x_vegpont - mesh_settings.x_kezdeti_pont) / 2; 
		double s_x3 = (mesh_settings.x_vegpont + mesh_settings.x_kezdeti_pont) / 2; 
		
		//segedvaltozok a ket egyenes szakaszhoz
		double m = mesh_settings.x_kezdeti_pont / A;
		double b_e = 1-m;
		
		//3 szakasz van
		if(x <= A) {y = m * x;}
		else if (x > A && x < B){
			//y = - (arctanh(Math.sqrt((S/d+S))*(q+p*x))) / (a*Math.sqrt((S*(d+S))));
			double x_scaled = q+p*x;
			//y_unscaled = 0.5 * (Math.log(x_scaled+1) - Math.log(1-x_scaled));
			y_unscaled = Math.pow(x_scaled,19);
			y = r_x3 * y_unscaled + s_x3;
		}
		else if (x >= B){y = m * x + b_e;}

		return y;
		
	}
	

	double Grid_Function_x2(double x){
		double y =0;
		double y_unscaled=0;	
		
		//segedvaltozok a ket egyenes szakaszhoz
		double m = mesh_settings.x_kezdeti_pont / A;
		double b_e = 1-m;
		
			

		//3 szakasz van
		if(x <= A) {y = m * x;}
		else if (x > A && x < B){
			//y = - (arctanh(Math.sqrt((S/d+S))*(q+p*x))) / (a*Math.sqrt((S*(d+S))));
			double x_scaled = q+p*x;
			//y_unscaled = 0.5 * (Math.log(x_scaled+1) - Math.log(1-x_scaled));
			y_unscaled = arctanh(x_scaled / Math.sqrt(d_faktor+1)) / Math.sqrt(d_faktor+1);
			y = r * y_unscaled + s;
		}
		else if (x >= B){y = m * x + b_e;}

		return y;
	}
	
	double arctanh(double x){
		
		return 0.5*Math.log((x + 1.0) / (1.0 - x));
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
		
		
		
		//double x = A;
		//double y1 = - (arctanh(Math.sqrt((S/d+S))*(b-a*x))) / (a*Math.sqrt((S*(d+S))));
		//System.out.println("y1="+y1);
		//x=B;
		
		//double y2 = - (arctanh(Math.sqrt((S/d+S))*(b-a*x))) / (a*Math.sqrt((S*(d+S))));
		//System.out.println("y2="+y2);
		
		//System.out.println("diff="+(y2-y1));
		
		//double y1 = 0.5 * (Math.log((A*p+q)+1) - Math.log(1-((A*p+q))));
		//double y2 = 0.5 * (Math.log((B*p+q)+1) - Math.log(1-((B*p+q))));
		
		//System.out.println("y1="+y1);
		//System.out.println("y2="+y2);
		
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
