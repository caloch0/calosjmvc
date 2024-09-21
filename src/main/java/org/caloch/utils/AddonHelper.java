package org.caloch.utils;


import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddonHelper{
    protected Map<List<String>,List<String>> info;
    protected Map<String,String> consVals;


    public AddonHelper() {
        this.consVals = new HashMap<>();
        this.consVals.put(" and tenantId=%s","sampleTenantId");
        this.consVals.put(" and userId in (%s)","sampleUserid,user1,user2");
    }

    public AddonHelper concat(String con,String val){
        this.consVals.put(con,val);
        return this;
    }

    public void process(){
        this.info=new HashMap<>();

    }

    public String getAddonConditions(boolean trimFirstAnd){
        return "";
    }

    public int prepareIndex(PreparedStatement ps, int curIndex){
        return curIndex;
    }

    public static void main(String[] args) {
        AddonHelper addonHelper=new AddonHelper();
        addonHelper.concat("","");
        System.out.println();
    }

}