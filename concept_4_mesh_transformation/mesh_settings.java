//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Ez fogja tudni a tenyleges mesh-t letrehozni
*/

public class mesh_settings{


	double ppb1_x=0; //jobboldali
	double ppb2_x=0;	//baloldali

	int N = 100000; //pontok szama
	
	int function_type = 0; //x2-es
	double ratio = 0.8;  
	int n = 601;  //hatvany kitevo; csak paratlan lehet

	double x_kezdeti_pont=0.19;    
	//double x_vegpont=0.1905;
	double x_vegpont=0.2;

}
