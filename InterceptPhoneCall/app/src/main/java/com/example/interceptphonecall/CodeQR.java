package com.example.interceptphonecall;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Getter
@Setter
@Entity(tableName = "codeqr")
public class CodeQR {
    @PrimaryKey
    private int id;
    private String code;
   /* private String address;

    private String hostAddress;*/
   private String name;
    public CodeQR(int id, String code, String name) {
        this.id=id;
        this.code = code;
        this.name=name;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }
    public String getName(){return name;}

   /* public String getAddress(){return address;}



    public String getHostAddress(){return hostAddress;}*/

    public void setCode(String code) {
        this.code = code;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

   /* public void setAddress(String address) {
        this.address = address;
    }



    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }*/
}
