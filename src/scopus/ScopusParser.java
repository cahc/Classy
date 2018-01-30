package scopus;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by crco0001 on 1/5/2018.
 */
public class ScopusParser {


    /*

    public static final short ELEMENT_NODE              = 1;
    public static final short ATTRIBUTE_NODE            = 2
    public static final short TEXT_NODE                 = 3;
    public static final short CDATA_SECTION_NODE        = 4;
    public static final short ENTITY_REFERENCE_NODE     = 5;
    public static final short ENTITY_NODE               = 6;
    public static final short PROCESSING_INSTRUCTION_NODE = 7;
    public static final short COMMENT_NODE              = 8;
    public static final short DOCUMENT_NODE             = 9;
    public static final short DOCUMENT_TYPE_NODE        = 10;
    public static final short DOCUMENT_FRAGMENT_NODE    = 11;
    public static final short NOTATION_NODE             = 12;

     */


    private final XPath xpath;
    private XPathExpression bibliographyExp;
    private XPathExpression bibliographySourceTitleExp;
    private XPathExpression coreDataExp;
    private XPathExpression languageExp;
    private XPathExpression standardizedAffiliationsEXp;
    private XPathExpression authorGroupEXp;
    private XPathExpression indexTermsExp;
    private XPathExpression authorKeywordsExp;
    private XPathExpression documentTypeExp;
    private final Document doc;

    public static File[] acceptedFileNames(File directory ) {

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {

                if(name.matches(".*2-s2\\.0-[0-9]{4,25}.*\\.xml")) return true;

                return false;
            }
        };




        return directory.listFiles(filter);


    }

    public ScopusParser(Document doc) throws XPathExpressionException {

        this.xpath = XPathFactory.newInstance().newXPath();
        this.doc = doc;


        this.bibliographyExp = this.xpath.compile("/abstracts-retrieval-response/item/bibrecord/tail/bibliography/*");
        this.bibliographySourceTitleExp = this.xpath.compile(".//ref-sourcetitle");


        this.coreDataExp = this.xpath.compile("/abstracts-retrieval-response/coredata/*");
        this.languageExp = this.xpath.compile("/abstracts-retrieval-response/language");
        this.standardizedAffiliationsEXp = this.xpath.compile("/abstracts-retrieval-response/affiliation");

        this.authorGroupEXp = this.xpath.compile ("/abstracts-retrieval-response/item/bibrecord/head/author-group");
        this.indexTermsExp = this.xpath.compile ("/abstracts-retrieval-response/idxterms/*");
        this.authorKeywordsExp = this.xpath.compile ("/abstracts-retrieval-response/authkeywords/*");

        this.documentTypeExp = this.xpath.compile("/abstracts-retrieval-response/item/bibrecord/head/citation-info/citation-type");

    }

    public String getDocumentType() throws XPathExpressionException {

        Node node = (Node)documentTypeExp.evaluate(this.doc,XPathConstants.NODE);


        String code = ((Element)node).getAttribute("code");

        if(code == null) return "unknown";

        return code;

    }

    public List<CitedReference> getCitedReferences() throws XPathExpressionException {


        NodeList nodeList = (NodeList)bibliographyExp.evaluate(this.doc,XPathConstants.NODESET);

        if(nodeList.getLength() == 0) return Collections.emptyList();

        List<CitedReference> citedReferenceList = new ArrayList<>();

        for(int i=0; i<nodeList.getLength(); i++) {



            Node node = nodeList.item(i);

            if(node.getNodeType() == Node.ELEMENT_NODE &&  "reference".equals( ((Element)node).getTagName()) )   {

                CitedReference citedReference = new CitedReference();

                Element element = (Element)node;

                String fulltext = (String)xpath.evaluate("ref-fulltext",element,XPathConstants.STRING);
                citedReference.setRefFulltext(fulltext);

                String title = (String)xpath.evaluate("ref-info/ref-title",element,XPathConstants.STRING);
                citedReference.setRefTitle(title);

                NodeList ids = (NodeList)xpath.evaluate("ref-info/refd-itemidlist/*",element,XPathConstants.NODESET);
                for(int j=0; j<ids.getLength(); j++) {

                    Node node1 = ids.item(j);
                    String idtype = ((Element)node1).getAttribute("idtype");

                    if("SGR".equals(idtype)) {
                        citedReference.setRefIdType(idtype);
                        citedReference.setRefID( node1.getTextContent() );
                        break;
                    }
                }


                String sourceTitle = (String)xpath.evaluate("ref-info/ref-sourcetitle",element,XPathConstants.STRING);
                citedReference.setRefSourceTitle(sourceTitle);

                Node refpubYear = (Node)xpath.evaluate("ref-info/ref-publicationyear",element,XPathConstants.NODE);

                if(refpubYear != null) citedReference.setPubYear(  ((Element)refpubYear).getAttribute("first") );

                Node firstRefAuthor = (Node)xpath.evaluate("ref-info/ref-authors/author[@seq=1][1]",element,XPathConstants.NODE);


                if(firstRefAuthor != null) {

                    Object indexedName = xpath.evaluate("indexed-name",firstRefAuthor,XPathConstants.STRING);

                    if(  indexedName != null ) citedReference.setFirstAuthor(indexedName.toString());


                } else {

                    //maybe a collaboration?

                    Node firstCollab = (Node)xpath.evaluate("ref-info/ref-authors/collaboration[@seq=1][1]",element,XPathConstants.NODE);

                    if(firstCollab != null) citedReference.setFirstAuthor(firstCollab.getFirstChild().getTextContent());

                }

                citedReferenceList.add(citedReference);
            }



        }


        return citedReferenceList;
    }


    public Record getCoreData() throws XPathExpressionException {

        NodeList nodeList = (NodeList)coreDataExp.evaluate(this.doc,XPathConstants.NODESET);

        System.out.println("Extracting core data");
        Record record = new Record();

        for(int i=0; i< nodeList.getLength(); i++) {

            Node node = nodeList.item(i);

            if( node.getNodeType() == Node.ELEMENT_NODE ) {


                Element element = (Element)node;
                //System.out.println(element.getTagName());

                if(  "prism:url".equals( element.getTagName() ) )               record.setURL(element.getTextContent() );

                if(  "eid".equals( element.getTagName() ) )                     record.setEid( element.getTextContent() );
                if(  "prism:doi".equals( element.getTagName() ) )               record.setDoi( element.getTextContent() );
                if(  "dc:title".equals( element.getTagName() ) )                record.setTitle( element.getTextContent() );
                if(  "srctype".equals( element.getTagName() ) )                 record.setSrctype( element.getTextContent() );

                if( "prism:aggregationType".equals( element.getTagName() ) ) record.setAggregationType( element.getTextContent()  );

                if(  "dc:description".equals( element.getTagName() ) )  {

                   Object summary = xpath.evaluate("abstract/para",node,XPathConstants.STRING);

                   if(summary != null)                                        record.setSummaryText((String)summary);
                }


                if(  "prism:publicationName".equals( element.getTagName() ) ) record.setPublicationName( element.getTextContent() );

                if(  "prism:coverDate".equals( element.getTagName() ) )       record.setPublicationYear( Integer.valueOf( element.getTextContent().substring(0,4) ) );

                if(  "source-id".equals( element.getTagName() ) )       record.setSourceId( element.getTextContent()  );
                if(  "citedby-count".equals( element.getTagName() ) )       record.setCitedBy( Integer.valueOf(element.getTextContent() ) );




            }

        }


        //finally add lang that is not part of coredata

        System.out.println("Extracting cited refs");
        List<CitedReference> citedRefs = getCitedReferences();
        record.setCitedReferences( citedRefs );
        System.out.println("Extracting Standardized affiliations");
        record.setAffiliationLevel1List(getStandardizedAffiliations() );
        System.out.println("Extracting authors and affiliations");
        List<Author> authorList = getAuthors();
        record.setAuthorList(authorList);
        System.out.println("Extracting type");
        record.setDocumentType(getDocumentType() );

        Node lang  = (Node)languageExp.evaluate(this.doc,XPathConstants.NODE);
        if(lang != null) record.setLanguage( ((Element)lang).getAttribute("xml:lang") );

       NodeList indexTermNodes =  (NodeList)indexTermsExp.evaluate(this.doc,XPathConstants.NODESET);

       for(int i=0; i<indexTermNodes.getLength(); i++) record.addIndexTerms(  indexTermNodes.item(i).getTextContent()  );

        NodeList authorKeywordsNodes =  (NodeList)authorKeywordsExp.evaluate(this.doc,XPathConstants.NODESET);

      for(int i=0; i<authorKeywordsNodes.getLength(); i++) record.addAuthorKeyword(  authorKeywordsNodes.item(i).getTextContent()  );


        return record;
    }

    public List<AffiliationLevel1> getStandardizedAffiliations() throws XPathExpressionException {

       NodeList nodeList =  (NodeList)this.standardizedAffiliationsEXp.evaluate(this.doc,XPathConstants.NODESET);
       List<AffiliationLevel1> affiliationLevel1List = new ArrayList<>();
       if(nodeList.getLength() == 0) return Collections.emptyList();

       for(int i=0; i<nodeList.getLength(); i++) {

           AffiliationLevel1 affiliationLevel1 = new AffiliationLevel1();

           Element element =  (Element)nodeList.item(i);
           affiliationLevel1.setAfid( element.getAttribute("id") );
           Object affiliation = ( xpath.evaluate("affilname",element,XPathConstants.STRING) );
           Object city =( xpath.evaluate("affiliation-city",element,XPathConstants.STRING) );
           Object country =( xpath.evaluate("affiliation-country",element,XPathConstants.STRING) );


           if(affiliation != null) affiliationLevel1.setAffiliation( affiliation.toString());
           if(city != null) affiliationLevel1.setAffiliationCity( city.toString());
           if(country != null) affiliationLevel1.setAffiliationCountry( country.toString());

           affiliationLevel1List.add(affiliationLevel1);

       }



        return affiliationLevel1List;
    }


    public List<Author> getAuthors() throws XPathExpressionException {

        List<Author> authorList = new ArrayList<>();

        NodeList nodeList = (NodeList)authorGroupEXp.evaluate(this.doc,XPathConstants.NODESET);

        //System.out.println("Author groups: " + nodeList.getLength());

        HashMap<String,Author> authorHashMap = new HashMap<>();

        for(int i=0; i<nodeList.getLength(); i++) {

            Node authorGroup = nodeList.item(i);

            Element affiliation = (Element)xpath.evaluate("affiliation",authorGroup,XPathConstants.NODE);
            AffiliationLevel2 affiliationLevel2 = null;
            if(affiliation != null) {

                affiliationLevel2 = new AffiliationLevel2();

                affiliationLevel2.setAfid(affiliation.getAttribute("afid"));

                affiliationLevel2.setDptid(affiliation.getAttribute("dptid"));

               NodeList organizations =  (NodeList)xpath.evaluate("organization",affiliation,XPathConstants.NODESET);
               for(int j=0; j<organizations.getLength(); j++) affiliationLevel2.addOrganisations( organizations.item(j).getTextContent() );

                affiliationLevel2.setCity( (String)xpath.evaluate("city",affiliation,XPathConstants.STRING) );
                affiliationLevel2.setCountry( (String)xpath.evaluate("country",affiliation,XPathConstants.STRING) );

            }

          //  System.out.println(affiliationLevel2);

            //process the authors in the author_group
            // an author can in several authorgroups depending on # affiliations

            NodeList authors = (NodeList)xpath.evaluate("author",authorGroup,XPathConstants.NODESET);

            for(int aut=0; aut<authors.getLength(); aut++ ) {

                Element authorInSeq = (Element)authors.item(aut);

                String auid = authorInSeq.getAttribute("auid");
                String seq = authorInSeq.getAttribute("seq");
                Author author = authorHashMap.get(auid);



                if(author == null) {

                  String initials = (String)xpath.evaluate("initials",authorInSeq,XPathConstants.STRING);
                    String surname = (String)xpath.evaluate("surname",authorInSeq,XPathConstants.STRING);
                    String givenname = (String)xpath.evaluate("given-name",authorInSeq,XPathConstants.STRING);


                   author = new Author();
                   author.setAuid(auid);
                   author.setSeq(Integer.valueOf(seq));
                   author.setGiveNname(givenname);
                   author.setInitials(initials);
                   author.setSurnNme(surname);
                   author.addAffiliationsLevel2(affiliationLevel2);
                   authorHashMap.put(auid,author);

                } else {

                    author.addAffiliationsLevel2(affiliationLevel2);

                }


            } //for each author in author-group





        } //next author_group node

        authorList.addAll(authorHashMap.values() );

        return authorList;

    }


    static void dump(Element e) {

        System.out.printf("Element: %s, %s, %s, %s%n", e.getNodeName(), e.getLocalName(), e.getPrefix(), e.getNamespaceURI());

        NamedNodeMap nnm = e.getAttributes();
        if (nnm != null)
            for (int i = 0; i < nnm.getLength(); i++) {
                Node node = nnm.item(i);
                Attr attr = e.getAttributeNode(node.getNodeName());
                System.out.printf("  Attribute %s = %s%n", attr.getName(), attr.getValue());
            }
        NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element)
                dump((Element) node);
        }
    }




    public static void main(String[] arg) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {


        if(arg.length != 1) {System.out.println("supply directory"); System.exit(0);}

       File[] eidFiles =ScopusParser.acceptedFileNames(new File(arg[0]) );


        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
       // documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();





        List<Record> records = new ArrayList<>();
        for(File file : eidFiles) {

            System.out.println("Parsing file: " + file.toString());

            FileInputStream fileInputStream = new FileInputStream(file);

            InputSource is = new InputSource( fileInputStream );
            is.setEncoding("ISO-8859-1"); // set ISO Latin-1, not valid UTF8 for some reason
            Document doc = db.parse(is);

            //Document doc = db.parse(file);


            ScopusParser scopusParser = new ScopusParser(doc);
            Record record = scopusParser.getCoreData();

           // System.out.println(record.getAuthorKeywords());
          //  System.out.println(record.getIndexTerms() );
         //   System.out.println(record.getAuthorList());
          //  System.out.println(record.getTitle());

            records.add(record);
            System.out.println(record.getEid());
            //System.out.println("##########");
            fileInputStream.close();

        }

        SimplePersistor.serialiseList(records,"records.ser");

        //Document doc = db.parse( "C:\\Users\\crco0001\\Desktop\\PARSE_SCOPUS\\EXAMPLE1.xml" );
       // Document doc = db.parse( "/Users/Cristian/Desktop/SCOPUS_XML_PARSE/EXAMPLE1.xml" );

        //ScopusParser scopusParser = new ScopusParser(doc);
        //Record record = scopusParser.getCoreData();


        //System.out.println(record.getAuthorKeywords());
        //System.out.println(record.getIndexTerms() );
        //System.out.println(record.getAuthorList());
        //System.out.println(record.getTitle());

        /*
        For debugging:

        System.out.println("Endcoding: " + doc.getXmlEncoding() );

        if(doc.hasChildNodes() ) {

            NodeList nodeList = doc.getChildNodes();
            System.out.println("#Child nodes  :" + nodeList.getLength() ) ;
            Node node = nodeList.item(0);
            System.out.println(node.getNodeName() + " : type: " + node.getNodeType() );

            if(node.getNodeType() == Node.ELEMENT_NODE) {

                dump( (Element)node );

            }


        }

        */



    }


}
