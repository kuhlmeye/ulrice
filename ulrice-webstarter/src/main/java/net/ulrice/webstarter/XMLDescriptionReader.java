package net.ulrice.webstarter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import net.ulrice.webstarter.tasks.IFTask;
import net.ulrice.webstarter.tools.CreateDescription;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Class used to parse the application description from an xml file. 
 * 
 * @author christof
 */
public class XMLDescriptionReader extends DefaultHandler {
	private static final Logger LOG = Logger.getLogger(XMLDescriptionReader.class.getName());

	private ApplicationDescription appDescription;

	private Stack<TaskDescription> taskStack;

	private TaskDescription cTask;

	private InputStream input;

	private String imagePath;

	public XMLDescriptionReader(InputStream input, String imagePath) {
		this.cTask = null;
		this.imagePath = imagePath;
		this.input = input;
		this.taskStack = new Stack<TaskDescription>();

	}

	public void parseXML(ApplicationDescription appDescription) throws SAXException, IOException {
		this.appDescription = appDescription;
		XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(this);
		reader.parse(new InputSource(input));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

		if ("task".equalsIgnoreCase(localName)) {
			String type = atts.getValue("type");
			Map<String, String> parameters = new HashMap<String, String>();
			for (int i = 0; i < atts.getLength(); i++) {
				parameters.put(atts.getLocalName(i), atts.getValue(i));
			}

			try {
				Class<? extends IFTask> taskClass = (Class<? extends IFTask>) Class
						.forName("net.ulrice.webstarter.tasks." + type);
				TaskDescription readTask = new TaskDescription(taskClass, parameters);

				String osAttribute = atts.getValue("os");
				if (osAttribute == null || System.getProperty("os.name").matches(osAttribute)) {

					if (cTask == null) {
						appDescription.addTask(readTask);
						cTask = readTask;
					} else {
						taskStack.add(cTask);
						cTask.addSubTask(readTask);
						readTask = cTask;
					}
				}
			} catch (ClassNotFoundException e) {
            	LOG.log(Level.SEVERE, "Class not found.", e);
			}
		} else if ("appparameter".equalsIgnoreCase(localName)) {

			String paramValue = atts.getValue("value");
			List<String> appParameters = appDescription.getAppParameters();
			if (appParameters == null) {
				appParameters = new ArrayList<String>();
				appDescription.setAppParameters(appParameters);
			}
			appParameters.add(paramValue);

		} else if ("application".equalsIgnoreCase(localName)) {
			appDescription.setName(atts.getValue("applicationName"));
			appDescription.setUseProxy(Boolean.valueOf(atts.getValue("useProxy")));

			String localDirString = atts.getValue("localDir");
			if (localDirString != null) {
				localDirString = localDirString.replace("${USER_DIR}", System.getProperty("user.home"));
				appDescription.setLocalDir(localDirString + File.separator);
			}

			String iconName = atts.getValue("applicationIcon");
			if (iconName != null) {
				appDescription.setIcon(new ImageIcon(imagePath + File.separator + iconName));
			}
			appDescription.setNeedsLogin(Boolean.valueOf(atts.getValue("needsLogin")));
		}
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
