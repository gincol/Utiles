package es.viewnext.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Port;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import com.predic8.wstool.creator.RequestTemplateCreator;
import com.predic8.wstool.creator.SOARequestCreator;

import es.viewnext.Main;
import es.viewnext.principal.RootController;
import es.viewnext.utils.Utils;
import groovy.xml.MarkupBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WebServiceController {
	private Main main;
	private RootController rootController;

	@FXML
	private TextField urlTextField;
	@FXML
	private TextField sizeRequestTextField;
	@FXML
	private TextField sizeResponseTextField;
	@FXML
	private TextField sizeRequestBytesTextField;
	@FXML
	private TextField sizeResponseBytesTextField;
	@FXML
	private TextField sizeResponseKbTextField;
	@FXML
	private TextField sizeResponseMbTextField;
	@FXML
	private TextField namespaceTextField;
	@FXML
	private TextField endopointTextField;
	@FXML
	private ComboBox<String> servicesCombo;
	@FXML
	private ComboBox<String> operationsCombo;
	@FXML
	private ComboBox<String> soapCombo;

	@FXML
	private TextArea descriptionArea;
	@FXML
	private TextArea xmlRequestArea;
	@FXML
	private TextArea xmlResponseArea;

	private HashSet<String> listServices;
	private HashSet<String> listOperations;
	private File wsdlFile;

	@FXML
	private void initialize() {
		List<String> unique = new ArrayList<String>();
		unique.add("Soap11");
		unique.add("Soap12");
		this.soapCombo.getItems().addAll(unique);
		this.soapCombo.getSelectionModel().select(0);

		this.listServices = new HashSet<String>();
		this.listOperations = new HashSet<String>();
		this.wsdlFile = null;
	}

	@FXML
	private void handleServices() {
		if (this.urlTextField.getText().trim().length() == 0
				|| !this.urlTextField.getText().trim().startsWith("http")) {
			this.handleClear();
			return;
		}
		this.servicesCombo.getSelectionModel().clearSelection();
		this.servicesCombo.getItems().clear();

		try {
			creaTmpFile(this.urlTextField.getText());
			WSDLParser parser = new WSDLParser();
			Definitions wsdl = parser.parse(new FileInputStream(this.wsdlFile));
			StringWriter writer = new StringWriter();
			SOARequestCreator creator = new SOARequestCreator(null, null, null);
			creator.setBuilder(new MarkupBuilder(writer));
			creator.setDefinitions(wsdl);
			for (com.predic8.wsdl.Service service : wsdl.getServices()) {
				this.listServices.add(service.getName());
				this.endopointTextField.setText(service.getPorts().get(0).getAddress().getLocation());

				for (Port port : service.getPorts()) {
					Binding binding = port.getBinding();
					PortType portType = binding.getPortType();
					for (Operation op : portType.getOperations()) {
						this.listOperations.add(op.getName());
					}
				}
			}
			this.namespaceTextField.setText(wsdl.getTargetNamespace());
			this.servicesCombo.getItems().addAll(this.listServices);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@FXML
	private void handleOperations() {
		if (this.urlTextField.getText().trim().length() == 0) {
			this.handleClear();
			return;
		}
		this.operationsCombo.getItems().addAll(this.listOperations);
	}

	@FXML
	private void handleFillRequest() {
		if ("".equals(this.soapCombo.getSelectionModel().getSelectedItem())) {
			return;
		}
		this.xmlRequestArea.clear();
		System.out.println("handleFillRequest");
		String texto = "";
		WSDLParser parser = new WSDLParser();
		Definitions wsdl;
		StringWriter writer = new StringWriter();
		SOARequestCreator creator = new SOARequestCreator(null, null, null);
		try {
			wsdl = parser.parse(new FileInputStream(this.wsdlFile));
			creator.setBuilder(new MarkupBuilder(writer));
			creator.setDefinitions(wsdl);
			groovy.xml.QName qname = new groovy.xml.QName(this.namespaceTextField.getText(),
					this.servicesCombo.getSelectionModel().getSelectedItem());
			com.predic8.wsdl.Service service = (com.predic8.wsdl.Service) wsdl.getService(qname);
			Operation op = null;
			int opcion = this.soapCombo.getSelectionModel().getSelectedIndex();
			int vueltas = 0;
			System.out.println("opcion: " + opcion);
			for (Port port : service.getPorts()) {
				if (opcion == 0) {
					Binding binding = port.getBinding();
					PortType portType = binding.getPortType();
					op = portType.getOperation(this.operationsCombo.getSelectionModel().getSelectedItem());
					creator.setCreator(new RequestTemplateCreator());
					creator.createRequest(port.getName(), op.getName(), binding.getName());
					break;
				} else if (opcion == 1 && vueltas == 1) {
					Binding binding = port.getBinding();
					PortType portType = binding.getPortType();
					op = portType.getOperation(this.operationsCombo.getSelectionModel().getSelectedItem());
					creator.setCreator(new RequestTemplateCreator());
					creator.createRequest(port.getName(), op.getName(), binding.getName());
				}
				vueltas++;
			}
			if (op.getDocumentation() != null) {
				this.descriptionArea.setText(op.getDocumentation().getContent());
			} else {
				this.descriptionArea.setText("No hay documentación sobre esta operación");
			}
			texto = writer.toString();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  

		this.xmlRequestArea.setText(texto);
	}

	/**
	 * Envía la request al WS y recibe la respuesta
	 */
	@FXML
	private void handleSend() {
		if (this.xmlRequestArea.getText().trim().length() == 0) {
			return;
		}
		URL wsdlLocation = null;
		QName serviceName = null;
		Service service = null;
		Dispatch<SOAPMessage> dispatch = null;
		String message = "";
		ByteArrayOutputStream stream = null;
		MessageFactory factory = null;
		String xml = this.xmlRequestArea.getText();
		try {
			wsdlLocation = new URL(this.urlTextField.getText());
			String endpointAddress = this.endopointTextField.getText();
			serviceName = new QName(this.namespaceTextField.getText(),
					this.servicesCombo.getSelectionModel().getSelectedItem());
			service = Service.create(wsdlLocation, serviceName);
			if (this.soapCombo.getSelectionModel().getSelectedIndex() == 0) {
				service.addPort(serviceName, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
				factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			} else {
				service.addPort(serviceName, SOAPBinding.SOAP12HTTP_BINDING, endpointAddress);
				factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
			}
			dispatch = service.createDispatch(serviceName, SOAPMessage.class, Service.Mode.MESSAGE);
			SOAPMessage request = factory.createMessage(new MimeHeaders(),
					new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8"))));
			SOAPMessage response = dispatch.invoke(request);

			stream = new ByteArrayOutputStream();
			response.writeTo(stream);
			message = new String(stream.toByteArray(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.xmlResponseArea.setText(toPrettyString(message, 4));

		this.sizeRequestTextField.setText(Utils.integerToString(this.xmlRequestArea.getText().length()));
		this.sizeResponseTextField.setText(Utils.integerToString(this.xmlResponseArea.getText().length()));
		this.sizeRequestBytesTextField.setText(Utils.integerToString(this.xmlRequestArea.getText().getBytes().length));
		this.sizeResponseBytesTextField
				.setText(Utils.integerToString(this.xmlResponseArea.getText().getBytes().length));
		this.sizeResponseKbTextField
				.setText(Utils.doubleToString(Double.valueOf(this.xmlResponseArea.getText().getBytes().length) / 1024));
		this.sizeResponseMbTextField.setText(
				Utils.doubleToString(Double.valueOf(this.xmlResponseArea.getText().getBytes().length) / (1024 * 1024)));
	}

	/**
	 * Limpia de atributos
	 */
	@FXML
	private void handleClear() {
		this.urlTextField.clear();
		this.xmlRequestArea.clear();
		this.xmlResponseArea.clear();
		this.descriptionArea.clear();
		this.namespaceTextField.clear();
		this.endopointTextField.clear();
		this.sizeRequestTextField.setText("0");
		this.sizeResponseTextField.setText("0");
		this.sizeRequestBytesTextField.setText("0");
		this.sizeResponseBytesTextField.setText("0");
		this.sizeResponseKbTextField.setText("0");
		this.sizeResponseMbTextField.setText("0");
		this.servicesCombo.getItems().clear();
		this.operationsCombo.getItems().clear();
		this.wsdlFile.delete();
	}

	/**
	 * Formatea la respuesta xml del WS
	 * @param xml
	 * @param indent
	 * @return
	 */
	public static String toPrettyString(String xml, int indent) {
		try {
			// Turn xml string into a document
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

			// Remove whitespaces outside tags
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document,
					XPathConstants.NODESET);

			for (int i = 0; i < nodeList.getLength(); ++i) {
				Node node = nodeList.item(i);
				node.getParentNode().removeChild(node);
			}

			// Setup pretty print options
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			// Return pretty print xml string
			StringWriter stringWriter = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
			return stringWriter.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Crea un fichero con el wsdl descargado de la url inicial.
	 * De esta forma es accesible para su uso posterior
	 * @param urlStr
	 * @throws Exception
	 */
	private void creaTmpFile(String urlStr) throws Exception {
		String nameFile = System.getProperty("java.io.tmpdir") + "file.wsdl";
		
		URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(nameFile);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        
        this.wsdlFile = new File(nameFile);
	}

	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public RootController getRootController() {
		return rootController;
	}

	public void setRootController(RootController rootController) {
		this.rootController = rootController;
	}
}
