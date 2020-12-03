//import com.comsol.model.*;
//import com.comsol.model.util.*;
import java.io.*;
import java.lang.Math;


public class mesh_trial {

  public static void main(String[] args) throws IOException {

	System.out.println("Hello New MESH!!!");
	
	mesh_settings mesh_settings=new mesh_settings();
	parameters par=new parameters();
	
	mesh my_mesh=new mesh(mesh_settings, par);
	//my_mesh.generate_vtx_scaled();
	my_mesh.generate();

	mesh_adap mesh_adap=new mesh_adap(mesh_settings, my_mesh);

	my_mesh.write_mesh_vtx_scaled_FILE();
	my_mesh.write_mesh_vtx_scaled_density_FILE();
	my_mesh.check_vtx_scaled();

	

	for(int i=0; i<5; i++){
		mesh_adap.step();  //ezt kell mindig FOR ciklusba rendezni
	}
}
}
