//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Ez fogja tudni a tenyleges mesh-t letrehozni

5 szakasz : kozepen ket egyenes m, M a meredekseg


*/

public class mesh_settings{


	double ppb1_x=0; //jobboldali
	double ppb2_x=0;	//baloldali

	int N = 100000; //pontok szama
	
	int function_type = 30; //CIRCLE -> mert kozeledik a 30. szulinapom
	double ratio = 0.8;  
	
	//int n = 3;  //hatvany kitevo; csak paratlan lehet

	double x_kezdeti_pont=0.19;    
	//double x_vegpont=0.1905;
	double x_vegpont=0.2;
	
	double R = 0.8;   //ebbol lehet m, es M-et kiszamitani; pontok hany szazaleka keruljon be a suru mesh-es zonaba kb.

	double r = 0.01; //ici-pici kor sugara, amivel lekerekul

}
