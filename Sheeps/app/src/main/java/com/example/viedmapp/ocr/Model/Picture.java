package com.example.viedmapp.ocr.Model;

public class Picture {
    private String datoTittle;
    private String datoInfo;

    public Picture(String datoTittle, String datoInfo){
        this.datoInfo = datoInfo;
        this.datoTittle = datoTittle;
    }

    public String getDatoInfo() {
        return datoInfo;
    }
    public void setDatoInfo(){
        this.datoInfo = datoInfo;
    }

    public String getDatoTittle() {
        return datoTittle;
    }

    public  void setDatoTittle(){
        this.datoTittle = datoTittle;
    }
}
