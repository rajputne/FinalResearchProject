/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.NEU.Business;

import java.util.ArrayList;

/**
 *
 * @author User
 */
public class MyContract {

    String headers;
    String title;
    String tableOfContent;
    ArrayList<Section> sectionList;
    ArrayList<DefinedTerms> definedTermList;

    public MyContract() {
        sectionList = new ArrayList<>();
        definedTermList = new ArrayList<>();
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<DefinedTerms> getDefinedTermList() {
        return definedTermList;
    }

    public void setDefinedTermList(ArrayList<DefinedTerms> definedTermList) {
        this.definedTermList = definedTermList;
    }

    public ArrayList<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(ArrayList<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public String getTableOfContent() {
        return tableOfContent;
    }

    public void setTableOfContent(String tableOfContent) {
        this.tableOfContent = tableOfContent;
    }

}
