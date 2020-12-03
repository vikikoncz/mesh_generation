//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*
Lekert H*OH es egyeb adatokkal ez talaja ki tulajdonkeppen az uj mesh_settings parametereket

*/

public class mesh_logika{
	
	double ppb1_x;
	double ppb2_x;
	double x_kezdeti_pont;
	double x_vegpont;

	mesh_logika(double ppb1_x, double ppb2_x, double x_kezdeti_pont, double x_vegpont){
		this.ppb1_x=ppb1_x;
		this.ppb2_x=ppb2_x;
		this.x_kezdeti_pont=x_kezdeti_pont;
		this.x_vegpont=x_vegpont;
	}


	void run(){
		System.out.println("MESH_LOGIKA_RUN!!");
		int irany=0;   // 0: balra, 1: jobbra
		if(irany==0){
			x_kezdeti_pont = x_kezdeti_pont - 0.002;
			x_vegpont = x_vegpont - 0.002;
			ppb1_x = ppb1_x - 0.002;
			ppb2_x = ppb2_x - 0.002;
		}		
	}

}

//PPB erteket hogyan lehet lekerni a COMSOL-tol
/*
//Get ppb1 and ppb2 values!!!
	model.result().numerical().create("gev1", "EvalGlobal");
	model.result().numerical("gev1").set("expr", "mod1.ppb1");
	double [][] results_ppb1=model.result().numerical("gev1").getReal();

//ppb2
	model.result().numerical().create("gev2", "EvalGlobal");
	model.result().numerical("gev2").set("expr", "mod1.ppb2");
	double [][] results_ppb2=model.result().numerical("gev2").getReal();


//get new HOH values at ppb1_x and ppb2_x

			results_ppb1=model.result().numerical("gev1").getReal();
			results_ppb2=model.result().numerical("gev2").getReal();
			
			length_y_ppb=results_ppb1[0].length-1;

			mesh_adap.h_oh_ppb1=results_ppb1[0][length_y_ppb];
			mesh_adap.h_oh_ppb2=results_ppb2[0][length_y_ppb];

*/
