package org.zizitop.pshell.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.zizitop.pshell.ShellApplication;
import org.zizitop.pshell.utils.exceptions.FileLoadingException;
import org.zizitop.pshell.utils.exceptions.ParsingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Class for easy working with XML.
 * <br><br>
 * Created 18.03.2018 21:38
 *
 * @author Zizitop
 */
public class XML implements ResourceManager {
	private final Document document;
	private final String path;

	private boolean haveChanges;

	public XML(String path) throws FileLoadingException, ParsingException {
		try {
			this.path = path;

			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(fXmlFile);
		} catch(ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch(SAXException e) {
			throw new ParsingException("Problems with XML file parsing.", e, path);
		} catch(IOException e) {
			throw new FileLoadingException("Can`t load XML file.", e, path);
		}

		ShellApplication.addResourceManager(this);
	}

	public Element getRoot() {
		return document.getDocumentElement();
	}

	public void saveChanges() throws TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(path));
		transformer.transform(source, result);
	}

	public void setChanged(boolean value) {
		haveChanges = value;
	}

	@Override
	public void freeResources() {
		try {
			if(haveChanges) {
				saveChanges();
			}
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
