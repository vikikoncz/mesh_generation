//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Mesh parametereit tartalmazza, ezeket kell majd mindig felulirni
Parametereket 1x eleg ebben modositani
*/

public class mesh_settings{

//Ket point probe expression helye
	double ppb1_x=0; //jobboldali
	double ppb2_x=0;	//baloldali
//MESH tulajdonsagai, ami alapjan kesziteni kell	
/*
	int n_mesh=2000;   //MESH pontjainak az osszes szama
	int n_lepcsok_szama=9;   //MESH lepcsoinek a szama;   PARATLAN-t varunk el
	double x_kezdeti_pont=0.19;  //MESH lepcsoinek a baloldali kezdopontja
	double x_vegpont=0.205;    //MESH lepcsoinek a jobboldali kezdopontja
	double n_lepcso_ossz_arany=0.8; //Osszes pont hany szazaleka megy majd a lepcsobe, ill. az alap-ba :)
	double n_step_decrease_factor=0.9;   //1-gyel kijjebb elhelyezkedo lepcsoben a MESH pontok szama ennyied resze az elozo (beljebb levo lepcsonek)
	double lepcso_szelesseg_factor=1;  //Ugyanez a 'dobrogi faktor', csak a lepcsoszelesseg valtozast irja lepcso_szelesseg_factor
*/

	double x_kezdeti_pont=0.19;  
	//double x_vegpont=0.1905;
	double x_vegpont=0.2;

	double x_kezdeti_pont_scaled = 0.1;
	double x_vegpont_scaled = 0.4;

	double x1_scaled=-(x_vegpont_scaled-x_kezdeti_pont_scaled) / 2 ;
	double x2_scaled=(x_vegpont_scaled-x_kezdeti_pont_scaled) / 2 ;
		

	int n_mesh=200000;   //nagy MESH szamnal kisebb a numerikus hiba
	int n_mesh_real=2000;      
	double n_lepcso_ossz_arany=0.8;
	double scaling_factor=1;   //1-es scaling factor kb. 0.1-es nagysagu intervallumra lesz jo	 
	
	int function_type=2; 
	double A_szorzo;
	double ro_alap;
	double ro_alap_scaled;

	int n_kritikus_zona;
	int n_alap;
	int n_function;
	int n_lin_tag;
	int n_function_scaled;
	int n_lin_tag_scaled;
	

/**********************************
Function type lehetseges ertekei:
	function_type=0   x2
	function_type=1	  x4
	function_type=2   x6
	function_type=3   sin(x)
	function_type=4	  sin2(x)	
***********************************/
	
	mesh_settings(){
		this.calculate_mesh_numbers();		
		this.calculate_A_szorzo();
	}


	void reset_mesh_parameters(double ppb1_x, double ppb2_x, double x_kezdeti_pont, double x_vegpont)
	{	this.ppb1_x=ppb1_x;
		this.ppb2_x=ppb2_x;
		this.x_kezdeti_pont=x_kezdeti_pont;
		this.x_vegpont=x_vegpont;
		this.calculate_mesh_numbers();	
		this.calculate_A_szorzo();
	}

	void calculate_mesh_numbers(){
		this.n_kritikus_zona = (int)Math.round(n_mesh * n_lepcso_ossz_arany);
		this.n_alap = n_mesh - n_kritikus_zona;
		this.ro_alap = (double)n_alap / (1-(x_vegpont-x_kezdeti_pont)); 
		this.ro_alap_scaled = (double)n_alap / (1-(x_vegpont_scaled-x_kezdeti_pont_scaled));		
		this.n_lin_tag = (int)Math.round(ro_alap * (x_vegpont-x_kezdeti_pont));
		this.n_function = n_kritikus_zona - n_lin_tag;		

		this.n_lin_tag_scaled = (int)Math.round(ro_alap_scaled * (x_vegpont_scaled-x_kezdeti_pont_scaled));
		this.n_function_scaled = n_kritikus_zona - n_lin_tag_scaled;
			
		int N_szum = n_alap + n_lin_tag + n_function;
		if(N_szum!=n_mesh){
			int difference = n_mesh-N_szum;
			System.out.println("MESH difference="+difference);
			n_alap=n_alap + difference;			
		}


		

		System.out.println("n_kritikus_zona="+n_kritikus_zona);
		System.out.println("n_alap="+n_alap); 
		System.out.println("ro_alap="+ro_alap); 
		System.out.println("ro_alap_scaled="+ro_alap_scaled); 
		System.out.println("n_lin_tag="+n_lin_tag); 
		System.out.println("n_function="+n_function);  
		System.out.println("n_lin_tag_scaled="+n_lin_tag_scaled); 
		System.out.println("n_function_scaled="+n_function_scaled);  


	}

	void calculate_A_szorzo(){
		

		if(function_type==0){   //X2
			//double x1=x_kezdeti_pont * scaling_factor;
			//double x2=x_vegpont * scaling_factor; 
		
			double integrated_function_x3 = (Math.pow(x1_scaled,3) - Math.pow(x2_scaled,3)) / 3;
			double integrated_function_x2 = (Math.pow(x2_scaled,2) - Math.pow(x1_scaled,2)) / 2 * (x1_scaled+x2_scaled);
			double integrated_function_x1 = -(x2_scaled-x1_scaled)*x1_scaled*x2_scaled;

			double integrated_function = integrated_function_x1 + integrated_function_x2 + integrated_function_x3; 
			this.A_szorzo = n_function_scaled / integrated_function;
		}
		else if(function_type==1){  //X4
			//double x1=x_kezdeti_pont * scaling_factor;
			//double x2=x_vegpont * scaling_factor; 
			
			double a=2/(x2_scaled-x1_scaled);
			double b=(x1_scaled+x2_scaled) / (x2_scaled-x1_scaled);

			double integrated_function_x5 = (Math.pow(x2_scaled,5) - Math.pow(x1_scaled,5)) / 5 * Math.pow(a,4) * (-1);
			double integrated_function_x4 = (Math.pow(x2_scaled,4) - Math.pow(x1_scaled,4)) / 4 * Math.pow(a,3) * b * 4;
			double integrated_function_x3 = (Math.pow(x2_scaled,3) - Math.pow(x1_scaled,3)) / 3 * Math.pow(a,2) * Math.pow(b,2) * (-6);
			double integrated_function_x2 = (Math.pow(x2_scaled,2) - Math.pow(x1_scaled,2)) / 2 * a * Math.pow(b,3) * 4;
			double integrated_function_x1 = (x2_scaled-x1_scaled) * (1-Math.pow(b,4));

			double integrated_function = integrated_function_x1 + integrated_function_x2 + integrated_function_x3; 
			integrated_function = integrated_function + integrated_function_x4 + integrated_function_x5; 

			this.A_szorzo = n_function_scaled / integrated_function;
			
		}
		else if(function_type==2){  //X6
			//double x1=x_kezdeti_pont * scaling_factor;
			//double x2=x_vegpont * scaling_factor; 
			double a=2/(x2_scaled-x1_scaled);
			double b=(x1_scaled+x2_scaled) / (x2_scaled-x1_scaled);

			double integrated_function_x7 =	(Math.pow(x2_scaled,7) - Math.pow(x1_scaled,7)) / 7 * Math.pow(a,6) * (-1);		
			double integrated_function_x6 =	(Math.pow(x2_scaled,6) - Math.pow(x1_scaled,6)) / 6 * Math.pow(a,5) * b * 6;
			double integrated_function_x5 =	(Math.pow(x2_scaled,5) - Math.pow(x1_scaled,5)) / 5 * Math.pow(a,4) * Math.pow(b,2) * (-15);
			double integrated_function_x4 =	(Math.pow(x2_scaled,4) - Math.pow(x1_scaled,4)) / 4 * Math.pow(a,3) * Math.pow(b,3) * (20);
			double integrated_function_x3 =	(Math.pow(x2_scaled,3) - Math.pow(x1_scaled,3)) / 3 * Math.pow(a,2) * Math.pow(b,4) * (-15);
			double integrated_function_x2 =	(Math.pow(x2_scaled,2) - Math.pow(x1_scaled,2)) / 2 * a * Math.pow(b,5) * (6);
			double integrated_function_x1 =	(x2_scaled-x1_scaled) * (1-Math.pow(b,6));

			double integrated_function = integrated_function_x1 + integrated_function_x2 + integrated_function_x3; 
			integrated_function = integrated_function + integrated_function_x4 + integrated_function_x5; 
			integrated_function = integrated_function + integrated_function_x6 + integrated_function_x7; 

			this.A_szorzo = n_function_scaled / integrated_function;
		}
		else if(function_type==3){	//SIN(x)		
			//double x1=x_kezdeti_pont * scaling_factor;
			//double x2=x_vegpont * scaling_factor; 
			double A = Math.PI / (x2_scaled-x1_scaled);
			double B = (x1_scaled * Math.PI) / (x2_scaled-x1_scaled);
			double integrated_function = (-Math.cos(A*x2_scaled-B)+Math.cos(A*x1_scaled-B)) / A;
				
			this.A_szorzo = n_function_scaled / integrated_function;
		}
		else if(function_type==4){   //SIN2(x)
			//double x1=x_kezdeti_pont * scaling_factor;
			//double x2=x_vegpont * scaling_factor; 
			double A = Math.PI / (x2_scaled-x1_scaled);
			double B = (x1_scaled * Math.PI) / (x2_scaled-x1_scaled);

			double integrated_function = 0.5*(x2_scaled-x1_scaled) - Math.sin(2*A*x2_scaled-2*B) / (4*A) + Math.sin(2*A*x1_scaled-2*B) / (4*A);
			this.A_szorzo = n_function_scaled / integrated_function;
		}

		else if(function_type==10){   //c konstans
			//double x1=x_kezdeti_pont * scaling_factor;
			//double x2=x_vegpont * scaling_factor; 

			double integrated_function=x2_scaled-x1_scaled;
			this.A_szorzo = n_function_scaled / integrated_function;
		}

		else if(function_type==11){  //X - linearis
			//double x1=x_kezdeti_pont * scaling_factor;
			//double x2=x_vegpont * scaling_factor; 

			//double integrated_function=1/(x2-x1)*(x2*x2/2-x1*x1/2-x2*x1+x1*x1);
			double integrated_function=(x2_scaled-x1_scaled)/2;
			this.A_szorzo = n_function_scaled / integrated_function;

		}

		else{System.out.println("UNKNOWN FUNCTION TYPE!!");}
		System.out.println("A_szorzo="+A_szorzo);
		
	}


	double get_mesh_density(double x_i){
		
		//double x1=x_kezdeti_pont * scaling_factor;
		//double x2=x_vegpont * scaling_factor;
		double m_x=0;
		//ro_alap=0;

		if(function_type==0){   //X2
			
			m_x=-(x_i-x1_scaled)*(x_i-x2_scaled)*A_szorzo + ro_alap_scaled;
			return m_x;

		} 
		else if(function_type==1){  //X4
			
			double a=2/(x2_scaled-x1_scaled);
			double b=(x2_scaled+x1_scaled)/(x2_scaled-x1_scaled);
			m_x= (1 - Math.pow((a*x_i-b),4))*A_szorzo + ro_alap_scaled; 
			return m_x;

		}
		else if(function_type==2){  //X6

			double a=2/(x2_scaled-x1_scaled);
			double b=(x2_scaled+x1_scaled)/(x2_scaled-x1_scaled);
			m_x= (1 - Math.pow((a*x_i-b),6))*A_szorzo + ro_alap_scaled;
			return m_x; 

		}
		else if(function_type==3){   //SIN(X)

			double A = Math.PI/(x2_scaled-x1_scaled);
			double B = (x1_scaled*Math.PI)/(x2_scaled-x1_scaled);
			m_x=Math.sin(A*x_i-B)*A_szorzo + ro_alap_scaled;
			return m_x;
		}
		else if(function_type==4){    //SIN2(X)		

			double A = Math.PI/(x2_scaled-x1_scaled);
			double B = (x1_scaled*Math.PI)/(x2_scaled-x1_scaled);
			m_x=Math.pow(Math.sin(A*x_i-B),2)*A_szorzo + ro_alap_scaled;
			return m_x;
		}
		else if(function_type==10){  //konstans

			m_x=A_szorzo + ro_alap_scaled;
			return m_x;
		}

		else if(function_type==11){	//X linearis

			m_x=(x_i-x1_scaled)/(x2_scaled-x1_scaled)*A_szorzo+ro_alap_scaled;
			return m_x;
		}
		

		else{System.out.println("UNKNOWN FUNCTION TYPE!!"); return m_x;}
		
	}	


}
