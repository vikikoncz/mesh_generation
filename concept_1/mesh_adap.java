//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;



/*Egesz MESH athelyezes keretet adja, miutan STOP CONDITION miatt megallt a solver
1. ez alapjan logolas
	- mesh_adap_logolas.java : logolas megfelelo adatainak az osszevadaszasa -> tombben atadja mesh_write.java osztalynak
	- mesh_write.java : tenyleges, a tombben megkapott, levadaszott parameterek kiirasat fajlba 
2. H*OH-bol irany-t meg kell hatarozni
3. mesh_logika -> uj parameterek meghatarozasa
4. ezekkel mesh_settings updatelese
5. mesh_settings parameterei alapjan uj MESH generalas : mesh.java

6. Ezen az osztalyon kivul (ugras_prototipus.java) kell az uj parameterekkel a COMSOL Model-t RESET-elni 
*/

/*LOG: valamilyen parameterekkel meddig futott a solver
igy ez kell, h legyen az 1. lepes!!! 
*/

public class mesh_adap{
	
	mesh_adap_logolas mesh_adap_logolas;  //ezekbol csak ez tart fent 1 peldanyt
	mesh_logika mesh_logika;	//ezekbol csak ez tart fent 1 peldanyt	
	mesh_settings mesh_settings;
	mesh mesh;	

//Kontruktor	
mesh_adap(mesh_settings mesh_settings, mesh mesh)
{
	this.mesh_settings=mesh_settings;
	this.mesh=mesh;
	mesh_logika=new mesh_logika();   
	mesh_adap_logolas=new mesh_adap_logolas();
}


void step(){
	//1. LOGOLAS
	mesh_adap_logolas.new_log();   //TODO new_log(); metódus
	//3. uj MESH parametereinek a meghatarozasa
	mesh_logika.run();	//TODO run(); metódus
		//buta parametereket, amiket ez meghataroz, ezt o maga tarolja
	//4. mesh_settings update-elese 
	mesh_settings.reset_mesh_parameters(mesh_logika.ppb1_x, mesh_logika.ppb2_x, mesh_logika.x_kezdeti_pont, mesh_logika.x_vegpont);
	//mesh_settings.proba=1;
	//System.out.println("mesh_settings.proba="+mesh_settings.proba);
	//mesh_settings.reset_mesh_parameters(1, 1, 1, 1);		
	//5. uj mesh settings parameterekkel uj mesh generalas
	mesh.generate();  //TODO mesh-nek legyen generate metodusa
}


}
