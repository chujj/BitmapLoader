package ds.android.utils;

import java.io.BufferedReader;  
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;  
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;  
import java.util.Iterator;
import java.util.Properties; 

//TODO
public class IniReader {
	protected HashMap<String, Properties> sections = new HashMap<String, Properties>();  
    private transient String currentSecion;  
    private transient Properties current;  
    private String filename = null;
  
    public IniReader(String filename) throws IOException {  
    	this.filename = filename;
        BufferedReader reader = new BufferedReader(new InputStreamReader(
        	    new FileInputStream(filename), "UTF-8"));
        read(reader);  
        reader.close();  
    }  
  
    protected void read(BufferedReader reader) throws IOException {  
        String line;  
        while ((line = reader.readLine()) != null) {  
            parseLine(line);  
        }  
    }  
  
    private void parseLine(String line) {  
        line = line.trim();  
        if (line.matches("//[.*//]") == true) {  
        	currentSecion = line.replaceFirst("//[(.*)//]", "$1");  
        	current = new Properties();  
            sections.put(currentSecion, current);  
        } else if (line.matches(".*=.*") == true) {  
            if (current != null) {  
                int i = line.indexOf('=');  
                String name = line.substring(0, i);  
                String value = line.substring(i + 1);  
                current.setProperty(name, value);  
            }  
        }  
    }  
  
    public String getValue(String section, String name, String defValue) {  
        Properties p = (Properties) sections.get(section);  
  
        if (p == null) {  
            return defValue;  
        }  
  
        return p.getProperty(name, defValue);  
    }  
    
    
    /*
    * Description: update value.
    * Parameter: section, key and value.
    *            flagAdd: true: add it if the data not exist; 
    *                     false: return false if the data not exist.
    * Return: true: change success, 
    *         false: change fail.
    * Notice: we don't write data into file in this function.
    */
    public boolean setValue(String sectionName,String key,String value) {
    	return setValue(sectionName, key, value, true);
    }

    public boolean setValue(String sectionName,String key,String value,boolean flagAdd) {
       if (key.length() == 0){
    	   return false;
       }
       sectionName = sectionName.trim();
       Properties p = (Properties)sections.get(sectionName);
       if (null == p) {
    	   if (flagAdd == true) {
    		   // not find the section, we should create it.
    		   current = new Properties();
    		   current.setProperty(key.trim(), value.trim());
    		   sections.put(sectionName, current);
    		   return true;
    	   } else {
    		   //Log.e("EncryptBox", "INIReader: not find section.");
    		   return false;
    	   }
         //throw new IllegalArgumentException("sectionName参数出错");
       }
       p.setProperty(key, value);
       return true;
    }
    /*
    * Description: write the data to file.
    * Parameter: NONE.
    * Return: true: success.
    */
    public boolean write() {
       return write(filename);
    }

    public boolean write(String argFileName) {
       if (argFileName == null) {
    	   return false;
       }
       PrintWriter writer = null;
       try {
    	   File f = new File(argFileName);
    	   if (!f.exists()) {
    		   // create file.
    		   f.createNewFile();
    	   }
    	   writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f)));
    	   StringBuilder builder = new StringBuilder();
    	   Iterator<String> iterator = sections.keySet().iterator();
    	   while (iterator.hasNext()) {
    		   String sectionName = iterator.next();
    		   Properties p = (Properties)sections.get(sectionName);
    		   builder.append("\n").append("\n").append("[").append(sectionName).append("]");
    		   builder.append(getPropertiesString(p));
    	   }
    	   writer.write(builder.toString().trim());
    	   writer.flush();
    	   writer.close();
    	   return true;
       	} catch (Exception e) { 
       		e.printStackTrace();
       		//Log.e("EncryptBox", "INIReader: write error(" + argFileName + ").");
       	} 
       	if (writer != null) {
       		writer.close();
       	}
       	return false;
    }
    /*
    * Description: get the string of Properties.
    * Parameter: Properties.
    * Return: string.
    */
    private String getPropertiesString(Properties p) {
       StringBuilder builder = new StringBuilder();
       Iterator<Object> iterator = p.keySet().iterator();
       while (iterator.hasNext()) {
         String key = iterator.next().toString();
         builder.append("\n").append(key).append("=").append(p.get(key));
       }
       return builder.toString();
    }

}
