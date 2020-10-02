import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        String fileNameXML = "data.xml";
        List<Employee> list = parseCSV(columnMapping, fileNameCSV);
        String json = listToJson(list);
        writeString(json, "data.json");
        List<Employee> list2 = parseXML(fileNameXML);
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");

    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileNameCSV) {
        List<Employee> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileNameCSV))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).
                    withMappingStrategy(strategy).
                    build();
            list = csv.parse();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return list;
    }

    private static List<Employee> parseXML(String fileNameXML) {
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileNameXML));
            Element staffNode = doc.getDocumentElement();
            NodeList staffNodeChildren = staffNode.getChildNodes();
            for (int i = 0; i < staffNodeChildren.getLength(); i++) {
                Node employeeNode = staffNodeChildren.item(i);
                if (Node.ELEMENT_NODE == employeeNode.getNodeType()) {
                    Element employeeElement = (Element) employeeNode;
                   String id = employeeElement.getElementsByTagName("id").item(0).getTextContent();
                   String firstName = employeeElement.getElementsByTagName("firstName").item(0).getTextContent();
                   String lastName = employeeElement.getElementsByTagName("lastName").item(0).getTextContent();
                   String country = employeeElement.getElementsByTagName("country").item(0).getTextContent();
                   String age = employeeElement.getElementsByTagName("age").item(0).getTextContent();
                   Employee employee = new Employee(Long.parseLong(id), firstName, lastName, country, Integer.parseInt(age));
                   list.add(employee);
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    private static void writeString(String json, String fileName) {

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
