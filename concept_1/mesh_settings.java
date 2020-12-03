//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;

/*Mesh parametereit tartalmazza, ezeket kell majd mindig felulirni
Parametereket 1x eleg ebben modositani
*/

public class mesh_settings{

//Ket point probe expression helye
	double ppb1_x; //jobboldali
	double ppb2_x;	//baloldali
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
	double x_vegpont=0.205;
	int n_mesh=2000;   
	int n_lepcsok_szama=21;      
	double n_lepcso_ossz_arany=0.8; 
	double n_step_decrease_factor=0.98;  
	double lepcso_szelesseg_factor=1;  

//Segedvaltozok lathatosag ellenorzesere, tesztelesre stb. 	
	int proba=0;	
	

void reset_mesh_parameters(double ppb1_x, double ppb2_x, double x_kezdeti_pont, double x_vegpont)
{	this.ppb1_x=ppb1_x;
	this.ppb2_x=ppb2_x;
	this.x_kezdeti_pont=x_kezdeti_pont;
	this.x_vegpont=x_vegpont;
}


}
