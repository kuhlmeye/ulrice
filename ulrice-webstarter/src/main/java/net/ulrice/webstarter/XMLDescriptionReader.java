package net.ulrice.webstarter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import net.ulrice.webstarter.tasks.IFTask;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLDescriptionReader extends DefaultHandler {

	private ProcessThread process;

	private Stack<IFTask> taskStack;

	private IFTask cTask;

	private InputStream input;

	public XMLDescriptionReader(InputStream input) {
		this.cTask = null;
		this.input = input;
		this.taskStack = new Stack<IFTask>();

	}

	public void parseXML(ProcessThread process) throws SAXException, IOException {
		this.process = process;
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(this);
		reader.parse(new InputSource(input));

		process.fireTasksLoadedEvent();

	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if ("task".equalsIgnoreCase(localName)) {
			String type = atts.getValue("type");
			IFTask readTask = createTaskByName(type);
			for (int i = 0; i < atts.getLength(); i++) {
				readTask.addParameter(atts.getLocalName(i), atts.getValue(i));
			}
			if (cTask == null) {
				process.addTask(readTask);
				cTask = readTask;
			} else {
				taskStack.add(cTask);
				cTask.addSubTask(readTask);
				readTask = cTask;
			}
		}
	}

	private IFTask createTaskByName(String type) {
		try {
			Class<?> taskClass = Class.forName("net.ulrice.webstarter.tasks." + type);
			return (IFTask) taskClass.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("task".equalsIgnoreCase(localName)) {
			if (!taskStack.isEmpty()) {
				cTask = taskStack.pop();
			} else {
				cTask = null;
			}
		}
	}

}
