/**
 * Created by ashkumar on 2/26/17.
 * Given a list of a company's employees in the form name,boss,title,year produce an organizational chart
 * by printing out each employee followed by everyone that employee manages indented by dashes.
 */
import java.io.*;
import java.util.*;

public class orgChart {
    private static final String ipFileName = "/home/ashkumar/dev/hulu-ash/resources/org_chart.in";
    private static final String opFileName = "/home/ashkumar/dev/hulu-ash/resources/org_chart.out";
    private static TreeMap<String, empStrut> data = new TreeMap<String, empStrut>();
    private static String ceoName;
    public static void main(String[] args) {

        int iteration = 0;
        orgChart orgChart = new orgChart();
        empStrut emp = new empStrut();
        try {
            BufferedReader buffRead = new BufferedReader(new FileReader(ipFileName));

            String isCurrentLine;
            while ((isCurrentLine = buffRead.readLine()) != null) {
                //Skipping line number one
                if(iteration == 0) {
                    iteration++;
                    continue;
                }
                writeToFile("Case #" + iteration);
                //Store each unique k=employee name, v=employee object in a TreeMap such that the name are sorted
                storeData(isCurrentLine);
                //Create a list of bosses for each employee
                for(String eachEmp:data.keySet())
                {
                    storeBossNames(eachEmp);

                }
                writeToFile(ceoName + " (CEO) "+data.get(ceoName).getYear() );
                getReportedEmployees(ceoName, data);

                iteration++;
                //clearing out for next test case
                ceoName="";
                data.clear();
            }
            buffRead.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static TreeMap<String, empStrut> storeData(String s) {
        String[] fullString = s.split("--");

        for(String value: fullString) {
            String[] splitVal = value.split(",");
            empStrut emp = new empStrut();
            emp.setName(splitVal[0]);
            emp.setBossName(splitVal[1]);
            emp.setTitle(splitVal[2]);
            emp.setYear(splitVal[3]);
            emp.setReportedEmployees(new ArrayList<empStrut>()); //intialize an empty list
            data.put(splitVal[0], emp);
            if(splitVal[2].equalsIgnoreCase("CEO"))
            {
                ceoName=splitVal[0];
            }
        }
        return data;
    }

    private static void storeBossNames( String name) {
        empStrut emp = data.get(name);
        String boss= data.get(name).getBossName();
        empStrut employee = data.get(boss);
        if (employee != null) {
            employee.getReportedEmployees().add(emp);
        }
    }

    //search recursively through boss list to get desired org chart
    private static void getReportedEmployees(String id,
                                                      TreeMap<String, empStrut> data) {
        empStrut employee = data.get(id);
        int depth=0;
        if (employee != null) {
            dfs(data, employee.getReportedEmployees(), depth);
        }
    }

    private static void dfs(TreeMap<String, empStrut> data, List<empStrut> listEmp, int depth) {
        depth++;
        for (empStrut emp : listEmp) {
            String dashes = "-";
            int dashDepth = depth;
            while(dashDepth!=1)
            {
                dashes+="-";
                dashDepth--;
            }
            writeToFile(dashes+emp.getName()+" ("+emp.getTitle()+") "+emp.getYear());
            empStrut temp = data.get(emp.getName());
            if (temp != null) {
                dfs(data, temp.getReportedEmployees(), depth);
            }
        }
    }
    //Method to print output file - Using writer in append mode
    private static void writeToFile(String s)
    {
        try
        {
             BufferedWriter buffWrite = null;
            FileWriter fw = null;

            File file = new File(opFileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            buffWrite = new BufferedWriter(fw);

            buffWrite.write(s);
            buffWrite.newLine();
            buffWrite.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
//modelling the employee structure
 class empStrut
{
    String name;
    String bossName;
    String title;
    String year;
    //List holds all the employees that work under a particular employee (CEO in our case)
    private List<empStrut> reportedEmployees;

    public String getName() { return name; }
    public String getBossName() { return bossName; }
    public String getTitle() { return title; }
    public String getYear() { return year; }
    public List<empStrut> getReportedEmployees() {
        return reportedEmployees;
    }

    public void setName(String name) {  this.name = name;}
    public void setBossName(String bossName) { this.bossName = bossName; }
    public void setTitle(String title) { this.title =title; }
    public void setYear(String year) { this.year = year; }
    public void setReportedEmployees(List<empStrut> reportedEmployees) {
        this.reportedEmployees = reportedEmployees;
    }

}
