package es.viewnext.modelo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import es.viewnext.utils.UtilsFiles;

public class MainPepe {

	public static void main(String[] args) {
		File file = new File("C:\\Users\\0012004\\.Gincol_utiles\\infraestructura.xml");
		
		try {
			UtilsFiles.setVisible(file.toPath());

			JAXBContext context = JAXBContext.newInstance(Infraestructuras.class);
			
			Infraestructuras iw = new Infraestructuras();
//			Infraestructuras inf = new Infraestructuras();
			List<Server> lista = new ArrayList<Server>();
			Server s = new AppServer("ctti1","10.29.67.11","AppServer","WL","1.7","12.1.2");
//			
			lista.add(s);
//			inf.setServerList(lista);
//			iw.setInfraestructuras(inf);
			iw.setServerList(lista);
//			
			Marshaller jaxbMarshaller = context.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(iw, file);
			jaxbMarshaller.marshal(iw, System.out);
			
			
			Unmarshaller um = context.createUnmarshaller();
			UtilsFiles.setVisible(file.toPath());
			Infraestructuras wrapper = (Infraestructuras) um.unmarshal(file);

//			System.out.println(wrapper.getInfraestructuras().getServerList().get(0).ip);
			for (Server server : wrapper.getServerList()) {
				System.out.println(server.getIp());
			}
//			if(wrapper.getInfraestructuras() != null && wrapper.getInfraestructuras().getServerList().size()>0){
//				listaServers.addAll(wrapper.getInfraestructuras().getServerList());
//			}
//			UtilsFiles.setHidden(file.toPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
//		System.out.println(customer);
	}

	
	
}
