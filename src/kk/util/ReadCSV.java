package kk.util;


import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Value;
import com.bmc.arsys.pluginsvr.plugins.ARFilterAPIPluggable;
import com.bmc.arsys.pluginsvr.plugins.ARPluginContext;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/*
server IP:port number 
parent form name having the attachment field
request id of the form name having the attachment field
attachment field db id
child form name where you need the record
field id of the child form name, where the child form should be inserted

*/


public class ReadCSV implements ARFilterAPIPluggable{
    
        List<Value> returnList=new ArrayList<>();
    @Override
    public List<Value> filterAPICall(ARPluginContext arg0, List<Value> arg1) throws ARException {
        
        try {
            
            String serverIP_PORT=arg1.get(0).toString();
            String formName=arg1.get(1).toString();
            String requestID=arg1.get(2).toString();
            int attachmentFieldID=Integer.parseInt(arg1.get(3).toString());
            String childFormName=arg1.get(4).toString();
            int childFieldID1=Integer.parseInt(arg1.get(5).toString());

            ARServerUser asu=new ARServerUser(arg0, arg0.getLocale(), serverIP_PORT.split(":")[0]);
            asu.setPort(Integer.parseInt(serverIP_PORT.split(":")[1]));
            
            byte[] attachmentInBytes=asu.getEntryBlob(formName, requestID, attachmentFieldID);

            ByteArrayInputStream bis = new ByteArrayInputStream(attachmentInBytes);
            BufferedReader br=new BufferedReader(new InputStreamReader(bis));

            String line=br.readLine(); //also skips the first line
            while((line=br.readLine())!=null)
            {
                
                Entry entry=new Entry();
                entry.put(childFieldID1, new Value(line.split(",")[0]));
                //entry.put(childFieldID2, new Value(line.split(",")[1]));
                //entry.put(childFieldID3, new Value(line.split(",")[2]));
                asu.createEntry(childFormName, entry);
            }
            returnList.add(new Value(requestID));
        } 
        catch (Exception e) {
            returnList.add(new Value(e.toString()));
        }
        return returnList;
    }

    @Override
    public void onEvent(ARPluginContext arg0, int arg1) throws ARException {
      
    }

    @Override
    public void initialize(ARPluginContext arg0) throws ARException {
        
    }

    @Override
    public void terminate(ARPluginContext arg0) throws ARException {
        
    }
    
}
