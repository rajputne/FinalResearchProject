/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.htmlsmartcontract;

import java.util.ArrayList;
import com.NEU.Business.Articles;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import com.NEU.Business.Contract;
import com.NEU.Business.DefinedTerms;
import com.NEU.Business.MyContract;
import com.NEU.Business.Section;
import com.NEU.Utilities.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author User
 */
public class FinalProgram {

    /**
     * @param args the command line arguments
     */
    static String contents;
    public static Contract contract = new Contract();
    static TransportClient client;
    static IndexResponse response;
    public static ArrayList<String> contractLinks;

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        String selectedFile = "input/TwentyUrls.txt";
        contractLinks = new ArrayList<>();

        try {
            File file = new File(selectedFile.toString());
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            int ii = 0;
            String temp[] = {"1", "2"};
            while ((line = bufferedReader.readLine()) != null) {
                //line means link
                stringBuffer.append(line);
                Document doc;
                String plainText;
                String lowerCaseText;
                String removedSpace;
                String title;
                ii++;
                contract = Utility.doTopicModelling(contract);
                try {
                    doc = Jsoup.connect(line).get();

                    List<Element> title1 = doc.getElementsByTag("title");
                    String myTitle = "";
                    for (Element t : title1) {
                        myTitle += t.text();
                    }
                    System.out.println(myTitle);
                    // get title of the page
                    title = myTitle;
                    String textWithoutHr = doc.toString().replaceAll("<hr>", "");
                    plainText = Jsoup.parse(doc.toString()).text();
                    Document doc1 = Jsoup.parse(new URL(line).openStream(), "UTF-8", line);
                    String splitContent[] = plainText.split("SCHEDULES");

                    try {
                        contract.setDefinedTermList(getDefinedTermsFromText(splitContent[1].toLowerCase()));
                        String d = doc.body().text();

                        d = doc.toString().replaceAll("<hr>", "");

                    } catch (Exception ex) {
                        Logger.getLogger(FinalProgram.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    // System.out.println("Title: " + title);
                    // System.out.println("Title: " + title);

                    lowerCaseText = plainText.toLowerCase();
                    removedSpace = lowerCaseText.trim().replaceAll(" +", " ");
                    String cleanText = removedSpace.replaceAll("[^\\x00-\\x7F]", "");

                    String[] upperHeader = cleanText.split("table of contents|index|t a b l e o f c o n t e n t s");
                    String headers = "";
                    try {
                        headers = upperHeader[0];
                        contents = upperHeader[1];
                    } catch (Exception e) {

                        continue;
                    }
                    //Extract Borrower/Guarantor/

                    contract.setHeaders(headers);
                    contract.setTitle(title);
                    String allContent = "table of contents" + upperHeader[1];
                    try {
                        String tableOfContents = extractTableOfContents(allContent);

                        contract.setTableOfContent(tableOfContents);

                        Boolean containArticle = contents.contains("article");
                        ArrayList<String> keys = new ArrayList<>();

                        if (containArticle) {
                            String articleSplit[] = tableOfContents.split("article");
                            for (String article : articleSplit) {
                                Articles article1 = new Articles();
                                article1.setArticleName(article);
                                String sectionSplit[] = article.split("section");
                                // System.out.println(sectionSplit.length);
                                for (String section : sectionSplit) {
                                    //System.out.println(section);
                                    keys.add(section);
                                }
                            }
                            //System.out.println(articleSplit.length);
                        }
                        JSONObject myObj = new JSONObject();
                        for (int i = 0; i < keys.size(); i++) {
                            try {
                                String prevKeys = keys.get(i);
                                String afterKeys = keys.get(i + 1);

                                String prevKeysList[] = prevKeys.split("  ");
                                String my = prevKeysList[1].replaceAll("[^a-zA-Z]", "");
                                String finalPrevKeys = prevKeysList[0] + "  " + my;

                                String afterKeysList[] = afterKeys.split("  ");
                                my = afterKeysList[1].replaceAll("[^a-zA-Z]", "");
                                String finalafterKeys = afterKeysList[0] + "  " + my;

                                prevKeys = prevKeys.trim().replaceAll(" +", " ");
                                afterKeys = afterKeys.trim().replaceAll(" +", " ");
                                prevKeys = prevKeys.substring(0, prevKeys.length() - 3);
                                afterKeys = afterKeys.substring(0, afterKeys.length() - 3);

                                //For Contract 1 
                                int prevKeysIndex = contents.indexOf(prevKeys);
                                int afterKeysIndex = contents.indexOf(afterKeys);

                                //Search the Numbers not the text works but accuracy will not be great can generate false positive
                                //int prevKeysIndex = fullDocument.indexOf(prevKeysList[0]);
                                //int afterKeysIndex = fullDocument.indexOf(afterKeysList[0]);
                                if (prevKeysIndex < afterKeysIndex) {
                                    String value = contents.substring(prevKeysIndex, afterKeysIndex);
                                    value = value.replaceAll("\\[", "").replaceAll("\\]", "");
                                    value = value.substring(prevKeys.length(), value.length());
                                    value = value.replaceAll("\\n", "");

                                    if (prevKeys.contains("Increased Cost and Reduced Return; Capital Adequacy; Reserves on Eurodollar Rate Loans")) {
                                        System.out.println("Problem");
                                    }

                                    if (prevKeys.toLowerCase().contains("defined term")) {
                                        ArrayList<DefinedTerms> myMap = parseString(doc.toString());
                                        Section mysec = new Section();
                                        mysec.setSectionNumber(prevKeys);
                                        mysec.setSectionContent(value);
                                        // contract.setDefinedTermList(myMap);

                                    } else {
                                        Section section = new Section();
                                        section.setSectionNumber(prevKeys);
                                        section.setSectionContent(value);
                                        contract.getSectionList().add(section);
                                    }

                                }
                            } catch (Exception e) {
                                System.out.print(e.getMessage());
                            }
                        }

                        //PrintWriter out = new PrintWriter("MyIndexex1Sample.json");
                        //populateTable();
                        //populateTableDefinedTerms();
                        //FileWriter fw11 = new FileWriter("MyContractText.txt");
                        ObjectMapper mapper = new ObjectMapper();
                        BufferedWriter bw = null;
                        FileWriter fw = null;
                        try {
                            // Convert object to JSON string and save into a file directly
                            // mapper.writeValue(new File("contract.json"), contract);
                            // Convert object to JSON string
                            String jsonInString = mapper.writeValueAsString(contract);
                            MyContract con = new MyContract();
                            con.setDefinedTermList(contract.getDefinedTermList());
                            con.setHeaders(contract.getHeaders());
                            con.setSectionList(contract.getSectionList());
                            con.setTableOfContent(contract.getTableOfContent());
                            con.setTitle(contract.getTitle());
                            String jsonInStringSimple = mapper.writeValueAsString(con);

                            jsonInString = jsonInString.replaceAll("", "");
                            jsonInString = jsonInString.replaceAll("", "");
                            jsonInString = jsonInString.replaceAll("\\P{Print}", "");

                            jsonInStringSimple = jsonInStringSimple.replaceAll("", "");
                            jsonInStringSimple = jsonInStringSimple.replaceAll("", "");
                            jsonInStringSimple = jsonInString.replaceAll("\\P{Print}", "");
                            contractLinks.add(line);
                            contents = "";
                            JSONObject jsonObj = new JSONObject(jsonInString);

                            //Professor Ozbeck JSON
                            JSONObject jsonObjSimple = new JSONObject(jsonInStringSimple);
                            try (FileWriter file1 = new FileWriter("outputJSON/Contract" + String.valueOf(ii) + ".json")) {
                                file1.write(jsonObj.toString());

                                client = new PreBuiltTransportClient(Settings.EMPTY)
                                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300))
                                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

                                response = client.prepareIndex("elevencontracts", "contract", String.valueOf(ii))
                                        .setSource(jsonInString, XContentType.JSON)
                                        .get();

                                System.out.println("Successfully Copied JSON Object to File...");
                                System.out.println("\nJSON Object: " + jsonObj);
                            }
                            //Write files in simpleJSON
                            try (FileWriter file2 = new FileWriter("simpleJSON/Contract" + String.valueOf(ii) + ".json")) {
                                file2.write(jsonObjSimple.toString());

                                client = new PreBuiltTransportClient(Settings.EMPTY)
                                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300))
                                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

                                response = client.prepareIndex("finalsimplecontracts", "contract", String.valueOf(ii))
                                        .setSource(jsonInString, XContentType.JSON)
                                        .get();

                                System.out.println("Successfully Copied JSON Object to File...");

                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        try {
                            String content = cleanText;
                            fw = new FileWriter("output/" + myTitle + ".txt");
                            bw = new BufferedWriter(fw);
                            bw.write(content);
                            //System.out.println("Done");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (bw != null) {
                                    bw.close();
                                }
                                if (fw != null) {
                                    fw.close();
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                stringBuffer.append("\n");
            }
            fileReader.close();
            // System.out.println("Contents of file:");
            // System.out.println(stringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        contents = "";
        FileWriter writer = null;
        try {
            writer = new FileWriter("output.txt");
        } catch (IOException ex) {
            Logger.getLogger(FinalProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String str : contractLinks) {
            writer.write(str + "\n");
        }
        writer.close();
    }

    public static String extractTableOfContents(String documentText) {

        String[] tableOfContentsSplitter = documentText.split("schedules");
        String tableOfContents = tableOfContentsSplitter[0];
        contents = tableOfContentsSplitter[1];
        return tableOfContents;
    }

    public static ArrayList<DefinedTerms> getDefinedTermsFromText(String documentText) {
        System.out.println("----INDEXOF----");
        documentText = documentText.toLowerCase();
        int definedTermsStart = documentText.indexOf("article 1");
        int definedTermsEnd = documentText.indexOf("article 2");
        ArrayList<DefinedTerms> definedTerms = new ArrayList<>();
        if (definedTermsStart != -1 && definedTermsEnd != -1) {
            String definedTermsText = documentText.substring(definedTermsStart, definedTermsEnd);
            System.out.println(definedTermsText);

            // definedTermsText=definedTermsText.replaceAll("\u0093", "myStart");
            //definedTermsText=definedTermsText.replaceAll("\u0094", "myEnd");
            //documentText=documentText.replaceAll("\u0092", " ");
            int start = documentText.indexOf('\u0093', definedTermsStart);
            int end = documentText.indexOf('\u0094', start);
            String terms = definedTermsText.substring(start, end);
            System.out.println(terms);
            int delimiter = -1;
            int count = 0;

            while (start != -1 && end < definedTermsEnd) {
                try {
                    System.out.println(documentText.substring(start + 1, end));

                    delimiter = documentText.indexOf("\u0094", end + 1);

                    System.out.println(documentText.substring(end + 1, delimiter));

                    System.out.println();
                    DefinedTerms dt = new DefinedTerms();
                    dt.setTerm(documentText.substring(start + 1, end));
                    dt.setDefinition(documentText.substring(end + 1, delimiter));
                    //obj.put(documentText.substring(start + 1, end), documentText.substring(end + 1, delimiter));
                    definedTerms.add(dt);
                    String term = documentText.substring(start + 1, end);
                    count++;

                    start = documentText.indexOf("\u0093", end + 1);
                    end = documentText.indexOf("\u0094", start);
                } catch (Exception e) {
                    continue;
                }

            }
        } else {

            definedTermsStart = documentText.indexOf("article i");
            definedTermsEnd = documentText.indexOf("article ii");

            String definedTermsText = documentText.substring(definedTermsStart, definedTermsEnd);
            System.out.println(definedTermsText);

            // definedTermsText=definedTermsText.replaceAll("\u0093", "myStart");
            //definedTermsText=definedTermsText.replaceAll("\u0094", "myEnd");
            //documentText=documentText.replaceAll("\u0092", " ");
            int start = documentText.indexOf('\u0093', definedTermsStart);
            int end = documentText.indexOf('\u0094', start);
            String terms = definedTermsText.substring(start, end);
            System.out.println(terms);
            int delimiter = -1;
            int count = 0;

            while (start != -1 && end < definedTermsEnd) {
                try {
                    System.out.println(documentText.substring(start + 1, end));

                    delimiter = documentText.indexOf("\u0094", end + 1);

                    System.out.println(documentText.substring(end + 1, delimiter));

                    System.out.println();
                    DefinedTerms dt = new DefinedTerms();
                    dt.setTerm(documentText.substring(start + 1, end));
                    dt.setDefinition(documentText.substring(end + 1, delimiter));
                    //obj.put(documentText.substring(start + 1, end), documentText.substring(end + 1, delimiter));
                    definedTerms.add(dt);
                    String term = documentText.substring(start + 1, end);
                    count++;

                    start = documentText.indexOf("\u0093", end + 1);
                    end = documentText.indexOf("\u0094", start);
                } catch (Exception e) {
                    continue;
                }

            }
            System.out.println("Articles keyword not found");
            try (FileWriter file2 = new FileWriter("DefinedTermTest")) {
                file2.write(documentText);

            } catch (Exception e) {
            }

        }

        System.out.println("stop");
        // node.close();
        return definedTerms;
    }

    public static ArrayList<DefinedTerms> getDefinedTermsFromText1(String documentText) {
        System.out.println("----INDEXOF----");
        documentText = documentText.toLowerCase();
        int definedTermsStart = documentText.indexOf("article 1");
        int definedTermsEnd = documentText.indexOf("article 2");
        String definedTermsText = documentText.substring(definedTermsStart, definedTermsEnd);
        System.out.println(definedTermsText);
        ArrayList<DefinedTerms> definedTerms = new ArrayList<>();
        int start = definedTermsText.indexOf('\u0094', definedTermsStart);
        int end = definedTermsText.indexOf('\u0094', start + 1);
        String terms = definedTermsText.substring(start, end);
        System.out.println(terms);
        int delimiter = -1;
        int count = 0;

        while (start != -1 && end < definedTermsEnd) {

            //System.out.println(definedTermsText.substring(start + 1, end));
            delimiter = definedTermsText.indexOf(".", end + 1);

            System.out.println(definedTermsText.substring(end + 1, delimiter));

            System.out.println();
            DefinedTerms dt = new DefinedTerms();
            dt.setTerm(definedTermsText.substring(start + 1, end));
            dt.setDefinition(definedTermsText.substring(end + 1, delimiter));
            //obj.put(documentText.substring(start + 1, end), documentText.substring(end + 1, delimiter));
            definedTerms.add(dt);
            String term = definedTermsText.substring(start + 1, end);
            count++;

            start = definedTermsText.indexOf('', end + 1);
            end = definedTermsText.indexOf('', start);

        }

        System.out.println("stop");
        // node.close();
        return definedTerms;
    }

    public static ArrayList<DefinedTerms> parseString(String documentText) throws FileNotFoundException {

        int definedTermsStart = documentText.indexOf("article i");
        int definedTermsEnd = documentText.indexOf("article ii");
        ArrayList<DefinedTerms> definedTerms = new ArrayList<>();
        int start = documentText.indexOf('“', definedTermsStart);
        int end = documentText.indexOf('”', start);
        int delimiter = -1;
        int count = 0;

        while (start != -1 && end < definedTermsEnd) {

            System.out.println(documentText.substring(start + 1, end));

            delimiter = documentText.indexOf(".", end + 1);

            System.out.println(documentText.substring(end + 1, delimiter));

            System.out.println();
            DefinedTerms dt = new DefinedTerms();
            dt.setTerm(documentText.substring(start + 1, end));
            dt.setDefinition(documentText.substring(end + 1, delimiter));
            //obj.put(documentText.substring(start + 1, end), documentText.substring(end + 1, delimiter));
            definedTerms.add(dt);
            String term = documentText.substring(start + 1, end);
            count++;

            start = documentText.indexOf('“', end + 1);
            end = documentText.indexOf('”', start);

        }
        PrintWriter out = new PrintWriter("DefinedTerms.json");

        System.out.println("stop");
        // node.close();
        return definedTerms;
    }
}
