/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.NEU.Business;

import java.awt.List;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public class Contract {

    ArrayList<String> topicList;
    String headers;
    String title;
    String tableOfContent;
    ArrayList<Section> sectionList;
    ArrayList<DefinedTerms> definedTermList;

    public Contract() {
        sectionList = new ArrayList<>();
        definedTermList = new ArrayList<>();
        topicList = new ArrayList<>();
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

    public ArrayList<String> getTopicList() {
        return topicList;
    }

    public void setTopicList(ArrayList<String> topicList) {
        this.topicList = topicList;
    }

   

}
