package es.viewnext.modelo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "infraestructuras")
@XmlAccessorType(XmlAccessType.FIELD)
public class Infraestructuras {

	@XmlElement(name = "server")
	private List<Server> serverList;

	public Infraestructuras() {
		super();
	}

	public Infraestructuras(List<Server> serverList) {
		super();
		this.serverList = serverList;
	}

	public List<Server> getServerList() {
		return serverList;
	}

	public void setServerList(List<Server> serverList) {
		this.serverList = serverList;
	}

}
