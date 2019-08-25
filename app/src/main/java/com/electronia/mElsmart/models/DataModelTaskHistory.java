package com.electronia.mElsmart.models;

/**
 * Created by Pradeepn on 4/2/2019.
 */

public class DataModelTaskHistory {

    String Place;
    String status;
    String datetime;
     String BadgeNo;


    public DataModelTaskHistory(String Place, String status,String datetime,String BadgeNo) {
        this.Place=Place;
        this.status=status;
        this.datetime=datetime;
        this.BadgeNo=BadgeNo;


    }


    public String getPlace() {
        return Place;
    }


    public String getstatus() {
        return status;
    }


     public String getdatetime() {
          return datetime;
     }
     public String getBadgeNo() {
        return BadgeNo;
    }


    //  public String getFeature() {
    //   return feature;
    //}

}
