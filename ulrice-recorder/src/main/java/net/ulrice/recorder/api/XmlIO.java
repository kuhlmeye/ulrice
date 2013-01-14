package net.ulrice.recorder.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ulrice.recorder.domain.RecordedScreen;
import net.ulrice.recorder.domain.Recording;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class XmlIO {
	

	static void storeRecordingAsXML(Recording recording, List<File> files, File xmlFile) throws FileNotFoundException, IOException {
		PrintWriter pw = new PrintWriter(xmlFile);
		pw.println("<RecordedScreens>");
		pw.println(String.format("  <Title>%s</Title>", recording.getTitle()));
		pw.println(String.format("  <Category>%s</Category>", recording.getCategory()));
		pw.println(String.format("  <Description>%s</Description>", recording.getDescription()));
		pw.println("  <Screens>");
		try {
			for (int i = 0; i < recording.getScreens().size(); i++) {
				RecordedScreen screen = recording.getScreens().get(i);
				File screenshot = File.createTempFile("screen-" + i, "jpg");
				files.add(screenshot);
				File screenshot_small = File.createTempFile("screen-" + i + "_small", "jpg");
				files.add(screenshot_small);

				ImageIO.write(screen.getFullImage(), "JPG", screenshot);
				ImageIO.write(screen.getSmallImage(), "JPG", screenshot_small);
				pw.println("    <Screen>");
				pw.println(String.format("      <ScreenTitle>%s</ScreenTitle>", screen.getTitle()));
				pw.println(String.format("      <ScreenDescription>%s</ScreenDescription>", screen.getDescription()));
				pw.println(String.format("      <FullImage>%s</FullImage>", screenshot.getName()));
				pw.println(String.format("      <SmallImage>%s</SmallImage>", screenshot_small.getName()));
				pw.println("    </Screen>");
			}
			pw.println("  </Screens>");
			pw.println("</RecordedScreens>");
		} finally {
			pw.close();
		}
	}

	
	static Recording parseRecording(File xmlFile, boolean loadScreens) throws ParserConfigurationException, IOException, SAXException {		
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(xmlFile);
		Element root = doc.getDocumentElement();
		if(!"RecordedScreens".equals(root.getNodeName())) {
			throw new IOException("RecordedScreens-Tag not found. Not a valid recording.");
		}
		
		Recording result = new Recording();
		NodeList childs = root.getChildNodes();
		for(int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if("Title".equals(child.getNodeName())) {
				result.setTitle(child.getTextContent());
			} else if("Category".equals(child.getNodeName())) {
				result.setCategory(child.getTextContent());
			} else if("Description".equals(child.getNodeName())) {
				result.setDescription(child.getTextContent());
			} else if(loadScreens && "Screens".equals(child.getNodeName())) {
				result.setScreens(parseScreens(xmlFile.getParentFile(), child));
			} 
		}
		
		return result;
	}

	private static List<RecordedScreen> parseScreens(File directory, Node root) throws DOMException, IOException {
		List<RecordedScreen> result = new ArrayList<RecordedScreen>();
		
		NodeList childs = root.getChildNodes();
		for(int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if("Screen".equals(child.getNodeName())) {
				result.add(parseScreen(directory, child));
			} 
		}
		
		return result;
	}

	private static RecordedScreen parseScreen(File directory, Node root) throws DOMException, IOException {
		RecordedScreen result = new RecordedScreen();
		NodeList childs = root.getChildNodes();
		for(int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if("ScreenTitle".equals(child.getNodeName())) {
				result.setTitle(child.getTextContent());
			} else if("ScreenDescription".equals(child.getNodeName())) {
				result.setDescription(child.getTextContent());
			} else if("FullImage".equals(child.getNodeName())) {
				result.setFullImage(ImageIO.read(new File(directory, child.getTextContent())));
			} else if("SmallImage".equals(child.getNodeName())) {
				result.setSmallImage(ImageIO.read(new File(directory, child.getTextContent())));
			} 
		}
		return result;
	}
}
