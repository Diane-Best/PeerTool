package PetriNet;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class PetriNet {
	private Document document;
	private String filename;
	private ArrayList<String> places = new ArrayList<String>();
	private ArrayList<String> transitions = new ArrayList<String>();
	private ArrayList<Integer> transNum = new ArrayList<Integer>();
	private int[][] matrix;
	private double[] range = new double[] { 0, 0, 0, 0 };

	public PetriNet() {
		this.filename = "src/net.xml";
	}

	// 读一个文件，初始化一个petri网，文件不存在就创建一个空的petri网
	public PetriNet(String filename) {
		this.filename = filename;
		File file = new File(filename);
		if (!file.exists()) {
			createXML(filename);
			this.filename = filename;
		}
		SAXReader reader = new SAXReader();
		try {
			this.document = reader.read(file);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.parserXML();
	}

	// 创建一个XML文件
	public void createXML(String filename) {
		document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("pnml");// 根节点
		document.setRootElement(rootElement);
		Element netElement = rootElement.addElement("net");
		netElement.addAttribute("id", "Net-One");
		netElement.addAttribute("type", "P/T net");
		File file = new File(filename);
		writeXML(document, file);
	}

	// 写入XML文件
	private int writeXML(Document document, File file) {
		int value = 0;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("utf-8");
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileOutputStream(file), format);
			writer.write(document);
			writer.close();
			value = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	// 添加token
	public int addToken() {
		int value = 0;
		Element rootElement = document.getRootElement().element("net");
		Element tokenElement = rootElement.addElement("token");
		tokenElement.addAttribute("id", "Default");
		tokenElement.addAttribute("enabled", "true");
		tokenElement.addAttribute("red", "0");
		tokenElement.addAttribute("green", "0");
		tokenElement.addAttribute("blue", "0");
		value = writeXML(document, new File(this.filename));
		return value;
	}

	// 添加place
	public int addPlace(String id, String posx, String posy, String offset1x, String offset1y, String offset2x,
			String offset2y, String capacity) {
		int value = 0;
		Element rootElement = document.getRootElement().element("net");
		Element placeElement = rootElement.addElement("place");
		placeElement.addAttribute("id", id);

		Element graphicsElement1 = placeElement.addElement("graphics");
		Element positionElement = graphicsElement1.addElement("position");
		positionElement.addAttribute("x", posx);
		positionElement.addAttribute("y", posy);

		Element nameElement = placeElement.addElement("name");
		Element valueElement1 = nameElement.addElement("value");
		valueElement1.setText(id);
		Element graphicsElement2 = nameElement.addElement("graphics");
		Element offsetElement1 = graphicsElement2.addElement("offset");
		offsetElement1.addAttribute("x", offset1x);
		offsetElement1.addAttribute("y", offset1y);

		Element initialMarkingElement = placeElement.addElement("initialMarking");
		Element valueElement2 = initialMarkingElement.addElement("value");
		valueElement2.setText("Default,0");
		Element graphicsElement3 = initialMarkingElement.addElement("graphics");
		Element offsetElement2 = graphicsElement3.addElement("offset");
		offsetElement2.addAttribute("x", offset2x);
		offsetElement2.addAttribute("y", offset2y);

		Element capacityElement = placeElement.addElement("capacity");
		Element valueElement3 = capacityElement.addElement("value");
		valueElement3.setText(capacity);

		value = writeXML(document, new File(this.filename));
		return value;
	}

	// 删除place
	public int deletePlace(String placeid) {
		int value = 0;
		if (this.transitions.size() == 0) {
			System.out.println("Current Petri-Net has no place!");
			return 999;
		}
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("place");
		boolean flag = false;
		for (Element element : elements) {
			if (element.attributeValue("id").equals(placeid)) {
				rootElement.remove(element);
				value = writeXML(document, new File(this.filename));
				System.out.println("Successfully delete the " + placeid + " place!");
				deleteArcByPlace(placeid);
				flag = true;
				break;
			}
		}
		if (!flag)
			System.out.println("Current Petri-Net doesn't contain the " + placeid + " place!");
		return value;
	}

	// 添加transition
	public int addTransition(String id, String posx, String posy) {
		int value = 0;
		Element rootElement = document.getRootElement().element("net");
		Element transitionElement = rootElement.addElement("transition");
		transitionElement.addAttribute("id", id);

		Element graphicsElement1 = transitionElement.addElement("graphics");
		Element positionElement = graphicsElement1.addElement("position");
		positionElement.addAttribute("x", posx);
		positionElement.addAttribute("y", posy);

		Element nameElement = transitionElement.addElement("name");
		Element valueElement1 = nameElement.addElement("value");
		valueElement1.setText(id);
		Element graphicsElement2 = nameElement.addElement("graphics");
		Element offsetElement = graphicsElement2.addElement("offset");
		offsetElement.addAttribute("x", "0");
		offsetElement.addAttribute("y", "0");

		Element orientationElement = transitionElement.addElement("orientation");
		Element valueElement2 = orientationElement.addElement("value");
		valueElement2.setText("0");

		Element rateElement = transitionElement.addElement("rate");
		Element valueElement3 = rateElement.addElement("value");
		valueElement3.setText("1.0");

		Element timedElement = transitionElement.addElement("timed");
		Element valueElement4 = timedElement.addElement("value");
		valueElement4.setText("false");

		Element infiniteServerElement = transitionElement.addElement("infiniteServer");
		Element valueElement5 = infiniteServerElement.addElement("value");
		valueElement5.setText("false");

		Element priorityElement = transitionElement.addElement("priority");
		Element valueElement6 = priorityElement.addElement("value");
		valueElement6.setText("1");
		value = writeXML(document, new File(this.filename));
		return value;
	}

	// 更新transition
	public int modifyTransition(String oldtransid, String newtransid) {
		int value = 0;
		Element rootElement = this.document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("transition");
		boolean find = false;
		for (Element element : elements) {
			if (element.attributeValue("id").equals(oldtransid)) {
				element.setAttributeValue("id", newtransid);
				element.element("name").element("value").setText(newtransid);
				System.out.println("Successfully modify the " + oldtransid + " transition!");				
				find = true;
				deleteArcByTransition(oldtransid);
				value = writeXML(this.document, new File(this.filename));
				break;
			}
		}
		if (!find)
			System.out.println("Can't find the " + oldtransid + " transition.");
		return value;
	}

	// 删除transition
	public int deleteTransition(String transitionid) {
		int value = 0;
		if (this.transitions.size() == 0) {
			System.out.println("Current Petri-Net has no transition!");
			return 999;
		}
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("transition");
		boolean flag = false;
		for (Element element : elements) {
			if (element.attributeValue("id").equals(transitionid)) {
				rootElement.remove(element);
				value = writeXML(document, new File(this.filename));
				System.out.println("Successfully delete the " + transitionid + " transition!");
				deleteArcByTransition(transitionid);
				flag = true;
				break;
			}
		}
		if (!flag)
			System.out.println("Current Petri-Net doesn't contain the " + transitionid + " transition!");
		return value;
	}

	// 添加arc
	public int addArc(String source, String target, String[] point1, String[] point2) {
		int value = 0;
		Element rootElement = document.getRootElement().element("net");
		Element arcElement = rootElement.addElement("arc");
		arcElement.addAttribute("id", source + " to " + target);
		arcElement.addAttribute("source", source);
		arcElement.addAttribute("target", target);

		Element graphicsElement1 = arcElement.addElement("graphics");

		Element inscriptionElement = arcElement.addElement("inscription");
		Element valueElement1 = inscriptionElement.addElement("value");
		valueElement1.setText("Default,1");
		Element graphicsElement2 = inscriptionElement.addElement("graphics");

		Element taggedElement = arcElement.addElement("tagged");
		Element valueElement2 = taggedElement.addElement("value");
		valueElement2.setText("false");

		Element arcpathElement1 = arcElement.addElement("arcpath");
		arcpathElement1.addAttribute("id", "000");
		arcpathElement1.addAttribute("x", point1[0]);
		arcpathElement1.addAttribute("y", point1[1]);
		arcpathElement1.addAttribute("curvePoint", "false");

		Element arcpathElement2 = arcElement.addElement("arcpath");
		arcpathElement2.addAttribute("id", "001");
		arcpathElement2.addAttribute("x", point2[0]);
		arcpathElement2.addAttribute("y", point2[1]);
		arcpathElement2.addAttribute("curvePoint", "false");

		Element typeElement = arcElement.addElement("type");
		typeElement.addAttribute("value", "normal");
		value = writeXML(document, new File(this.filename));
		return value;
	}

	// 删除arc通过transitionid
	public int deleteArcByTransition(String transitionid) {
		int value = 0;
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("arc");
		boolean flag = false;
		for (Element element : elements) {
			if (element.attributeValue("source").equals(transitionid)
					|| element.attributeValue("target").equals(transitionid)) {
				rootElement.remove(element);
				value = writeXML(document, new File(this.filename));
				flag = true;
			}
		}
		if (!flag)
			System.out.println("Current Petri-Net doesn't contain the arc with" + transitionid + " transition!");
		return value;
	}

	// 删除arc通过placeid
	public int deleteArcByPlace(String placeid) {
		int value = 0;
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("arc");
		boolean flag = false;
		for (Element element : elements) {
			if (element.attributeValue("source").equals(placeid) || element.attributeValue("target").equals(placeid)) {
				rootElement.remove(element);
				value = writeXML(document, new File(this.filename));
				flag = true;
			}
		}
		if (!flag)
			System.out.println("Current Petri-Net doesn't contain the arc with" + placeid + " place!");
		return value;
	}

	// 更新arc
	public int modifyArcByPlace(String placeid, String oldtransid, String newtransid) {
		int value = 0;
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("arc");
		boolean find = false;
		for (Element element : elements) {
			String source = element.attributeValue("source");
			String target = element.attributeValue("target");
			System.out.println(placeid.equals(source));
			System.out.println(oldtransid.equals(target));
			if (placeid.equals(source) && oldtransid.equals(target)) {
				System.out.println("1111111111111111111111");
				element.setAttributeValue("id", placeid + " to " + newtransid);
				element.setAttributeValue("target", newtransid);
				List<Element> arcpaths = rootElement.elements("arcpath");
				for (Element arcpath : arcpaths) {
					if (arcpath.attributeValue("id").equals("001")) {
						arcpath.setAttributeValue("x", getTransitionLocation(placeid)[0]);
						arcpath.setAttributeValue("y", getTransitionLocation(placeid)[1]);
					}
				}
				find = true;
				System.out.println("Successfully modify the arc!");
				value = writeXML(document, new File(this.filename));
				break;
			} else if (placeid.equals(target) && oldtransid.equals(source)) {
				System.out.println("22222222222222222222222");
				element.setAttributeValue("id", placeid + " to " + newtransid);
				element.setAttributeValue("source", newtransid);
				List<Element> arcpaths = rootElement.elements("arcpath");
				for (Element arcpath : arcpaths) {
					if (arcpath.attributeValue("id").equals("000")) {
						arcpath.setAttributeValue("x", getTransitionLocation(placeid)[0]);
						arcpath.setAttributeValue("y", getTransitionLocation(placeid)[1]);
					}
				}
				find = true;
				value = writeXML(document, new File(this.filename));
				break;
			}
		}
		if (!find)
			System.out.println("Can't find the --" + placeid + "," + oldtransid + "-- arc.");
		return value;
	}

	// 更新arc id，name通过transition
	public int modifyArcByTransition(String oldtransid, String newtransid) {
		int value = 0;
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("arc");
		boolean find = false;
		for (Element element : elements) {
			String source = element.attributeValue("source");
			String target = element.attributeValue("target");
			if (element.attributeValue("source").equals(oldtransid)) {
				element.setAttributeValue("id", newtransid + " to " + element.attributeValue("target"));
				element.setAttributeValue("source", newtransid);
				find = true;
				value = writeXML(document, new File(this.filename));
			} else if (element.attributeValue("target").equals(oldtransid)) {
				element.setAttributeValue("id", element.attributeValue("source") + " to " + newtransid);
				element.setAttributeValue("target", newtransid);
				find = true;
				value = writeXML(document, new File(this.filename));
			}
		}
		if (!find)
			System.out.println("Can't find the arc with " + oldtransid + "!");
		return value;
	}

	// 获取place坐标
	public String[] getPlaceLocation(String placeid) {
		String[] location = { "", "" };
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("place");
		boolean find = false;
		for (Element element : elements) {
			if (element.attributeValue("id").equals(placeid)) {
				String x = element.element("graphics").element("position").attributeValue("x");
				// System.out.println(x);
				String y = element.element("graphics").element("position").attributeValue("y");
				// System.out.println(y);
				location[0] = x;
				location[1] = y;
				find = true;
				break;
			}
		}
		if (!find)
			System.out.println("Can't find the " + placeid + " place.");
		return location;
	}

	// 获取transition坐标
	public String[] getTransitionLocation(String transitionid) {
		String[] location = { "", "" };
		Element rootElement = document.getRootElement();
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("transition");
		boolean find = false;
		for (Element element : elements) {
			if (element.attributeValue("id").equals(transitionid)) {
				String x = element.element("graphics").element("position").attributeValue("x");
				// System.out.println(x);
				String y = element.element("graphics").element("position").attributeValue("y");
				// System.out.println(y);
				location[0] = x;
				location[1] = y;
				find = true;
				break;
			}
		}
		if (!find)
			System.out.println("Can't find the " + transitionid + " transition.");
		return location;
	}

	// 解析XML
	private void parserXML() {
		int count = 0;
		this.places.clear();
		this.transitions.clear();
		this.transNum.clear();
		// 获取根节点对象
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements();
		for (Element element : elements) {
			if (element.getName() == "place") {
				this.places.add(element.attributeValue("id"));
			} else if (element.getName() == "transition") {
				count++;
				this.transitions.add(element.attributeValue("id"));
			}
		}
		this.transNum.add(count);
	}

	// 解析XML并进行预处理
	@SuppressWarnings("deprecation")
	private void parserXML(Document document, String mark, double offsetX, double offsetY) {
		double initX = 80;
		double initY = 30;
		int count = 0;
		// 获取根节点对象
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements();
		for (Element element : elements) {
			if (element.getName() == "place") {
				element.setAttributeValue("id", mark + element.attributeValue("id"));
				element.element("name").element("value")
						.setText(mark + element.element("name").element("value").getText());
				Element position = element.element("graphics").element("position");
				Double x = Double.parseDouble(position.attributeValue("x"));
				Double y = Double.parseDouble(position.attributeValue("y"));
				x = x - this.range[0] + initX + offsetX;
				y = y - this.range[2] + initY + offsetY;
				position.setAttributeValue("x", x.toString());
				position.setAttributeValue("y", y.toString());
				this.places.add(element.attributeValue("id"));
			} else if (element.getName() == "transition") {
				element.setAttributeValue("id", mark + element.attributeValue("id"));
				element.element("name").element("value")
						.setText(mark + element.element("name").element("value").getText());
				Element position = element.element("graphics").element("position");
				Double x = Double.parseDouble(position.attributeValue("x"));
				Double y = Double.parseDouble(position.attributeValue("y"));
				x = x - this.range[0] + initX + offsetX;
				y = y - this.range[2] + initY + offsetY;
				position.setAttributeValue("x", x.toString());
				position.setAttributeValue("y", y.toString());
				count++;
				this.transitions.add(element.attributeValue("id"));
				System.out.println("transid "+element.attributeValue("id"));
			} else if (element.getName() == "arc") {
				String src = element.attributeValue("source");
				String tar = element.attributeValue("target");
				element.setAttributeValue("source", mark + src);
				element.setAttributeValue("target", mark + tar);
				src = element.attributeValue("source");
				tar = element.attributeValue("target");
				element.setAttributeValue("id", src + " to " + tar);
				List<Element> arcpaths = element.elements("arcpath");
				for (Element arcpath : arcpaths) {
					Double x = Double.parseDouble(arcpath.attributeValue("x"));
					Double y = Double.parseDouble(arcpath.attributeValue("y"));
					x = x - this.range[0] + initX + offsetX;
					y = y - this.range[2] + initY + offsetY;
					arcpath.setAttributeValue("x", x.toString());
					arcpath.setAttributeValue("y", y.toString());
				}
			}
		}
		if (this.transNum.size() == 0)
			this.transNum.add(count);
		else if (this.transNum.size() == 1 && this.transNum.get(0) == 0) {
			this.transNum.set(0, Integer.valueOf(count));
		} else
			transNum.add(count + transNum.get(transNum.size() - 1));
	}

	private void computRange(Document document) {
		this.range = new double[] { 0, 0, 0, 0 };
		double minX = 999999999;
		double maxX = -999999999;
		double minY = 999999999;
		double maxY = -999999999;
		// 获取根节点对象
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements();
		// 获取图的坐标范围
		for (Element element : elements) {
			if (element.getName() == "place" || element.getName() == "transition") {
				Element position = element.element("graphics").element("position");
				Double x = Double.parseDouble(position.attributeValue("x"));
				Double y = Double.parseDouble(position.attributeValue("y"));
				if (x < minX) {
					minX = x;
				} else if (x > maxX) {
					maxX = x;
				}
				if (y < minY) {
					minY = y;
				} else if (y > maxY) {
					maxY = y;
				}
			}
		}
		this.range[0] = minX;
		this.range[1] = maxX;
		this.range[2] = minY;
		this.range[3] = maxY;
	}

	// 生成关联矩阵
	public int[][] generateIncidenceMatrix() {
		parserXML();
		int row = this.places.size();
		int col = this.transitions.size();
		matrix = new int[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				this.matrix[i][j] = 0;
			}
		}
		Element rootElement = document.getRootElement();
		// 获取子节点
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements("arc");
		for (Element element : elements) {
			String source = element.attributeValue("source");
			String target = element.attributeValue("target");
			if (this.places.contains(source) && this.transitions.contains(target)) {
				int i = this.places.indexOf(source);
				int j = this.transitions.indexOf(target);
				if (this.matrix[i][j] == 0)
					this.matrix[i][j] = -1;
				else if (this.matrix[i][j] == 1)
					this.matrix[i][j] = 2;
			} else if (this.transitions.contains(source) && this.places.contains(target)) {
				int i = this.places.indexOf(target);
				int j = this.transitions.indexOf(source);
				if (this.matrix[i][j] == 0)
					matrix[i][j] = 1;
				else if (this.matrix[i][j] == -1)
					this.matrix[i][j] = 2;
			}
		}
		return this.matrix;
	}

	// petri网简化
	public void simplyfy() {
		int value = 0;
		this.matrix = generateIncidenceMatrix();
		int row = this.places.size();
		int col = this.transitions.size();
		HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
		for (int i = 0; i < row; i++) {
			String str = "";
			for (int j = 0; j < col; j++) {
				str += this.matrix[i][j];
			}
			if (!hashmap.containsKey(str))
				hashmap.put(str, i);
			else
				deletePlace(this.places.get(i));
		}
		this.matrix = generateIncidenceMatrix();
	}

	// 合并几个文件到新的文件
	public int mergeXML(String filename, ArrayList<String> filenamearray) throws DocumentException {
		int value = 0;
		this.places.clear();
		this.transitions.clear();
		this.transNum.clear();
		this.filename = filename;
		createXML(this.filename);
		SAXReader saxReader = new SAXReader();
		File file = new File(this.filename);
		this.document = saxReader.read(file);
		Element parentElement = document.getRootElement();
		parentElement = parentElement.element("net");
		double[] newrange = this.range;
		double unit = 150;
		double offsetX = 0;
		double offsetY = 0;
		double width = 0;
		double height = 0;
		for (int i = 0; i < filenamearray.size(); i++) {
			Document other = saxReader.read(new File(filenamearray.get(i)));
			this.computRange(other);
			width += this.range[1] - this.range[0] + unit;
			if (height < this.range[3] - this.range[2])
				height = this.range[3] - this.range[2];
			if (width > 1000+unit) {
				if (i == 0) {
					newrange[1] = this.range[1];
				} else {
					newrange[1] = 1000;
				}
				if (i != 0) {
					newrange[3] += height + unit;
					offsetX = 0;
					offsetY = newrange[3];
				}
			} else {
				offsetX = width - (this.range[1] - this.range[0]) - unit;
			}
			parserXML(other, Integer.toString(i), offsetX, offsetY);
			Element rootElement = other.getRootElement();
			rootElement = rootElement.element("net");
			List<Element> elements = rootElement.elements();
			for (Element element : elements) {
				parentElement.add(element.detach());
			}
		}
		value = writeXML(document, new File(filename));
		for(int i=0; i<this.transitions.size(); i++)
			System.out.println("trans " + this.transitions.get(i));
		System.out.println("Successfully merge these file to current Petri-Net!");
		return value;
	}

	// 拷贝XML
	public int copyXML(String filename) {
		int value = 0;
		File otherfile = new File(filename);
		if (!otherfile.exists()) {
			System.out.println("The file doesn't exist!");
			return 999;
		}
		this.places.clear();
		this.transitions.clear();
		this.transNum.clear();
		createXML(this.filename);
		SAXReader saxReader = new SAXReader();
		File file = new File(this.filename);
		try {
			this.document = saxReader.read(file);
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Element parentElement = document.getRootElement();
		parentElement = parentElement.element("net");

		SAXReader reader = new SAXReader();
		Document other = null;
		try {
			other = reader.read(otherfile);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element rootElement = other.getRootElement();
		rootElement = rootElement.element("net");
		List<Element> elements = rootElement.elements();
		for (Element element : elements) {
			parentElement.add(element.detach());
		}
		value = writeXML(document, new File(this.filename));
		parserXML();
		System.out.println("Successfully copy file!");
		return value;
	}

	public String getFileName() {
		return filename;
	}

	public void setFileName(String filename) {
		this.filename = filename;
		SAXReader reader = new SAXReader();
		try {
			this.document = reader.read(new File(this.filename));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parserXML();
	}

	public ArrayList<String> getPlaces() {
		return places;
	}

	public ArrayList<String> getTransitions() {
		return transitions;
	}

	public ArrayList<Integer> getTransNum() {
		return transNum;
	}

}
