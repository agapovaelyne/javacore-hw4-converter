package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.w3c.dom.*;
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

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) throws CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString(String json, String jsonFileName) {
        try (FileWriter file = new FileWriter(jsonFileName)) {
            file.write(json);
            file.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        NodeList employeeNodeList = doc.getDocumentElement().getElementsByTagName("employee");

        List<Employee> list = new ArrayList<>();
        Employee employee;
        long id = 0;
        String firstName = null;
        String lastName = null;
        String country = null;
        int age = 0;

        for (int i = 0; i < employeeNodeList.getLength(); i++) {
            Node node = employeeNodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NodeList empElementNodeList = node.getChildNodes();
                for (int j = 0; j < empElementNodeList.getLength(); j++) {
                    Node empVarNode = empElementNodeList.item(j);
                    if (empVarNode.getNodeType() == Node.ELEMENT_NODE) {
                        String empVarName = empVarNode.getNodeName();
                        String empVarValue = empVarNode.getTextContent();
                        switch (empVarName) {
                            case ("id"):
                                id = Integer.parseInt(empVarValue);
                                break;
                            case ("firstName"):
                                firstName = empVarValue;
                                break;
                            case ("lastName"):
                                lastName = empVarValue;
                                break;
                            case ("country"):
                                country = empVarValue;
                                break;
                            case ("age"):
                                age = Integer.parseInt(empVarValue);
                        }
                    }
                }
                employee = new Employee(id, firstName, lastName, country, age);
                list.add(employee);
            }
        }
        return list;
    }

    public static void main(String[] args) throws CsvValidationException, ParserConfigurationException, IOException, SAXException {
        //Task1
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, "data.csv");
        String json = listToJson(list);
        writeString(json, "data.json");

        //Task2
        list = parseXML("data.xml");
        json = listToJson(list);
        writeString(json, "data2.json");
    }
}
