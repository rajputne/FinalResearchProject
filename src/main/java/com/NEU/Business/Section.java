/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.NEU.Business;


import java.util.HashMap;

/**
 *
 * @author User
 */
public class Section {

    String sectionNumber;
    String sectionContent;

   

    public Section() {

    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getSectionContent() {
        return sectionContent;
    }

    public void setSectionContent(String sectionContent) {
        this.sectionContent = sectionContent;
    }

    @Override
    public String toString() {
        return sectionNumber;
    }

}
