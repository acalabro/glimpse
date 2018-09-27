package it.cnr.isti.labsedc.glimpse.BPMN;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ModelLoader {
	
	public static Document readModel(String modelURI) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document dom = null;
			
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			dom = docBuilder.parse(modelURI);
		return dom;
	}
}
