import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.lang.*;
import java.util.HashMap;
import java.util.Map;

public class TreeStr {
    public static HashMap<String, Node> mainFolder = new HashMap<>();

}

class Node{
    String id, url, type;
    int size = 0;
    int sumSize = 0;
    Node parent = null;
    ArrayList<Node> children;
    public Node(String id) {
        this.id = id;
        children = new ArrayList<>();
        TreeStr.mainFolder.put(id, this);
    }
    public void addChildren(Node child){
        this.children.add(child);
    }

    public void addUrl(String url){
        if(url == "None")
            url = null;
        this.url = url;
    }
    public void addType(String type){
        if(type == "None")
            type = null;
        this.type = type;
    }
    public void addSize(String size){
        if (size == "None")
            return;
        else {
            this.size = Integer.parseInt(size);
            this.addSumSize(this.size);
        }

    }
    public void addSumSize(int size){
        this.size += size;
        System.out.println(this.size);
        if(parent != null){
            parent.addSumSize(this.size);
        }
    }

    public void addParent(String parentId){
        if (parentId == "None")
            return;

        if(!TreeStr.mainFolder.containsKey(parentId)){
            Node parent = new Node(parentId);
            TreeStr.mainFolder.put(parentId, parent);
        }
        if(!TreeStr.mainFolder.get(parentId).children.contains(this)) {
            TreeStr.mainFolder.get(parentId).addChildren(this);
            parent = TreeStr.mainFolder.get(parentId);
        }
    }
    public Map getInfo() {
        Map result = new HashMap();
        result.put("type", type);
        result.put("id", id);
        result.put("url", url);
        if (parent != null)
            result.put("parentId", parent.id);
        else
            result.put("parentId", null);

        if (type == "FILE")
            result.put("children", null);
        else {
            JSONArray children_info = new JSONArray();
            for (Node child: children) {
                children_info.add(child.getInfo());
            }
            result.put("children", children_info);
        }
        result.put("size", size);
        return result;
    }

    public void delete()  {
        for (Node child : children) {
            child.delete();
        }
        TreeStr.mainFolder.remove(id);
    }
}

class Request{
    public static JSONObject parse(String x){
        Object obj = JSONValue.parse(x);
        return (JSONObject) obj;
    }

    public static void importR(JSONObject x){
        String id;
        Map item;
        Node file;
        Map dict_x = (Map) x;
        Object items_list = dict_x.get("items");
        for(Object k: (JSONArray) items_list) {
            item = (Map) k;
            id = item.get("id").toString();

            if (!TreeStr.mainFolder.containsKey(id)) {
                file = new Node(id);
                TreeStr.mainFolder.put(id, file);
            }
            file = TreeStr.mainFolder.get(id);
            file.addUrl(item.getOrDefault("url", "None").toString());
            file.addType(item.getOrDefault("type", "None").toString());
            file.addParent(item.getOrDefault("parentId", "None").toString());
            file.addSize(item.getOrDefault("size", "None").toString());
        }

    }

    public static JSONObject nodesR(String id){
        Node item = TreeStr.mainFolder.get(id);
        Map newFolder = item.getInfo();
        return(new JSONObject(newFolder));
    }
    public static void deleteR(String id){
        Node cur = TreeStr.mainFolder.get(id);
        cur.delete();
        if (cur.parent != null)
            cur.parent.children.remove(cur);
    }
}