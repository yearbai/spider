package com.moye.crawler.common.utils;



import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TreeUtil {

    public static List<TreeNode> getNodeList(Map<String, TreeNode> nodelist) {
        List<TreeNode> tnlist=new ArrayList<>();
        try{
            for (String id : nodelist.keySet()) {
                TreeNode node = nodelist.get(id);
                if (StringUtils.isEmpty(node.getParentId()) || StringUtils.equals("0",node.getParentId())) {
                    tnlist.add(node);
                } else {
                    System.out.println(node.getParentId() + "  " + node.getText());
                    if (nodelist.get(node.getParentId()).getNodes() == null){
                        nodelist.get(node.getParentId()).setNodes(new ArrayList<TreeNode>());
                    }else{
                        nodelist.get(node.getParentId()).getNodes().add(node);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return tnlist;
    }

    public static List<TreeNode> getTreeNode(List<TreeNode> nodelist, String pid){
        List<TreeNode> tnlist = new ArrayList<>();
        for(TreeNode node : nodelist){
            if(StringUtils.equals( node.getParentId() , pid )){
                node.setNodes( getTreeNode(nodelist, node.getId()) );
                tnlist.add( node );
            }
        }
        return tnlist;
    }

}
