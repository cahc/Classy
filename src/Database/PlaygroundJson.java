package Database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PlaygroundJson {


    private static void recursiveGetAffils(final JsonNode node, Set<String> affils)  {


        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();

        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();

            final String key = field.getKey();
            //System.out.println("Key: " + key);
            final JsonNode value = field.getValue();

            if(value.isContainerNode()) {


               Iterator<JsonNode> iter = value.elements();

               while(iter.hasNext()) {

                   recursiveGetAffils(iter.next(),affils); //recursion

               }


            } else {


               if("name".equals(key)) {



                  // System.out.println("key/Value: " + key + " " + value + " " + value.getNodeType());
                   affils.add(value.asText());
               }

            }
        }


    }






    public static void main(String[] arg) throws IOException {


        String hej = "{\"@context\":\"https://id.kb.se/context.jsonld\",\"@id\":\"oai:lup.lub.lu.se:281cbd94-201a-42ec-8315-b54c0dbfab2a\",\"@type\":\"Instance\",\"extent\":{\"@type\":\"Extent\",\"label\":\"1 s.\"},\"identifiedBy\":[{\"@type\":\"URI\",\"value\":\"https://lup.lub.lu.se/record/1690297\"}],\"instanceOf\":{\"@type\":\"Text\",\"contribution\":[{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"familyName\":\"Lindh\\u00e9\",\"givenName\":\"Anna\",\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"lu\"},\"value\":\"engl-ali\"}]},\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"identifiedBy\":[{\"@type\":\"URI\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"},\"value\":\"lu.se\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Lunds universitet\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"lu.se\"},\"value\":\"v1000032\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Humanistiska och teologiska fakulteterna\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000032\"},\"value\":\"v1000041\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Institutioner\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000041\"},\"value\":\"v1000088\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Spr\\u00e5k- och litteraturcentrum\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000088\"},\"value\":\"v1000119\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Sektion 4\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000119\"},\"value\":\"v1000120\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Avdelningen f\\u00f6r engelska\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000120\"},\"value\":\"v1000121\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Engelska\"},{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"identifiedBy\":[{\"@type\":\"URI\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"},\"value\":\"lu.se\"}],\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"name\":\"Lund University\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"lu.se\"},\"value\":\"v1000032\"}],\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"name\":\"Joint Faculties of Humanities and Theology\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000032\"},\"value\":\"v1000041\"}],\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"name\":\"Departments\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000041\"},\"value\":\"v1000088\"}],\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"name\":\"Centre for Languages and Literature\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000088\"},\"value\":\"v1000119\"}],\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"name\":\"Section 4\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000119\"},\"value\":\"v1000120\"}],\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"name\":\"Division of English Studies\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000120\"},\"value\":\"v1000121\"}],\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"name\":\"English Studies\"}],\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/aut\"}]},{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"familyName\":\"Sonesson\",\"givenName\":\"Anders\"},\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/edt\"}]},{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"familyName\":\"Amn\\u00e9r\",\"givenName\":\"Gunilla\"},\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/edt\"}]}],\"genreForm\":[{\"@id\":\"https://id.kb.se/term/swepub/svep/ref\"},{\"@id\":\"https://id.kb.se/term/swepub/conference/paper\"},{\"@id\":\"https://id.kb.se/term/swepub/ConferencePaper\"}],\"hasNote\":[{\"@type\":\"CreatorCount\",\"label\":\"1\"},{\"@id\":\"https://id.kb.se/term/swepub/Published\",\"@type\":\"PublicationStatus\"}],\"hasTitle\":[{\"@type\":\"Title\",\"mainTitle\":\"'You're one of us': N\\u00e5gra reflektioner kring att undervisa p\\u00e5 ett historiskt svart universitet i USA\"}],\"language\":[{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\",\"langCode\":\"swe\",\"source\":{\"@type\":\"Source\",\"code\":\"iso639-2b\"}}],\"subject\":[{\"@type\":\"Topic\",\"prefLabel\":\"SoTL\"},{\"@id\":\"https://id.kb.se/term/uka/602\",\"@type\":\"Topic\",\"broader\":{\"prefLabel\":\"Humaniora och konst\"},\"code\":\"602\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"prefLabel\":\"Spr\\u00e5k och litteratur\"},{\"@id\":\"https://id.kb.se/term/uka/602\",\"@type\":\"Topic\",\"broader\":{\"prefLabel\":\"Humanities\"},\"code\":\"602\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Languages and Literature\"},{\"@id\":\"https://id.kb.se/term/uka/6\",\"@type\":\"Topic\",\"code\":\"6\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"prefLabel\":\"Humaniora och konst\"},{\"@id\":\"https://id.kb.se/term/uka/6\",\"@type\":\"Topic\",\"code\":\"6\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Humanities and the Arts\"}]},\"meta\":{\"@type\":\"AdminMetadata\",\"assigner\":{\"@type\":\"Agent\",\"label\":\"lu\"},\"changeDate\":\"2018-11-21T21:09:13+01:00\",\"creationDate\":\"2016-04-04T12:08:23+02:00\"},\"partOf\":[{\"@type\":\"Work\",\"genreForm\":[{\"@id\":\"https://id.kb.se/term/swepub/event\"}],\"hasTitle\":[{\"@type\":\"Title\",\"mainTitle\":\"Lunds universitets utvecklingskonferens, 2009,Lund, Sweden,2009-09-24 - 2009-09-24\"}]},{\"@type\":\"Work\",\"hasInstance\":{\"@type\":\"Instance\",\"extent\":[{\"@type\":\"Extent\",\"label\":\"125-125\"}]},\"hasTitle\":[{\"@type\":\"Title\",\"mainTitle\":\"Proceedings Utvecklingskonferens Lunds universitet 2009\"}],\"identifiedBy\":[{\"@type\":\"ISBN\",\"value\":\"9789197797429\"}]}],\"publication\":[{\"@type\":\"Publication\",\"agent\":{\"@type\":\"Agent\",\"label\":\"Lednings- och kompetensutveckling/CED\"},\"date\":\"2009\"}]}";

        String hej2 = "{\"@context\":\"https://id.kb.se/context.jsonld\",\"@id\":\"oai:DiVA.org:umu-159084\",\"@type\":\"Instance\",\"carrierType\":{\"@type\":\"CarrierType\",\"label\":\"electronic\",\"source\":{\"@type\":\"Source\",\"code\":\"marcform\"}},\"identifiedBy\":[{\"@type\":\"URI\",\"value\":\"http://urn.kb.se/resolve?urn=urn:nbn:se:umu:diva-159084\"},{\"@type\":\"DOI\",\"value\":\"https://doi.org/10.1007/s11192-019-03121-z\"},{\"@type\":\"URI\",\"value\":\"http://urn.kb.se/resolve?urn=urn:nbn:se:kth:diva-255179\"},{\"@type\":\"ScopusID\",\"value\":\"2-s2.0-85066012563\"},{\"@type\":\"URI\",\"value\":\"http://urn.kb.se/resolve?urn=urn:nbn:se:uu:diva-390087\"}],\"indirectlyIdentifiedBy\":[],\"instanceOf\":{\"@type\":\"Text\",\"contribution\":[{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"familyName\":\"Colliander\",\"givenName\":\"Cristian\",\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"umu\"},\"value\":\"crco0001\"},{\"@type\":\"ORCID\",\"value\":\"https://orcid.org/0000-0002-7653-4004\"}],\"lifeSpan\":\"1980-\"},\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"identifiedBy\":[{\"@type\":\"URI\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"},\"value\":\"umu.se\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Ume\\u00e5 universitet\"},{\"@type\":\"Organization\",\"identifiedBy\":[{\"@type\":\"URI\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"},\"value\":\"umu.se\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Ume\\u00e5 universitet\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"umu.se\"},\"value\":\"Sociologiska institutionen\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Sociologiska institutionen\"},{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"identifiedBy\":[{\"@type\":\"URI\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"},\"value\":\"umu.se\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Ume\\u00e5 universitet\"},{\"@type\":\"Organization\",\"identifiedBy\":[{\"@type\":\"URI\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"},\"value\":\"umu.se\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Ume\\u00e5 universitet\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"umu.se\"},\"value\":\"Ume\\u00e5 universitetsbibliotek (UB)\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Ume\\u00e5 universitetsbibliotek (UB)\"},{\"@type\":\"Organization\",\"name\":\"Inforsk\"},{\"@type\":\"Organization\",\"name\":\"Umea Univ, Dept Sociol, Inforsk, Umea, Sweden.;Umea Univ, Univ Lib, Umea, Sweden.\"},{\"@type\":\"Organization\",\"name\":\"Umea Univ, Dept Sociol, Inforsk, Umea, Sweden;Umea Univ, Univ Lib, Umea, Sweden\"}],\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/aut\"}]},{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"familyName\":\"Ahlgren\",\"givenName\":\"Per\",\"identifiedBy\":[{\"@type\":\"ORCID\",\"value\":\"https://orcid.org/0000-0003-0229-3073\"}]},\"hasAffiliation\":[{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"identifiedBy\":[{\"@type\":\"URI\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"},\"value\":\"kth.se\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"KTH\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"kth.se\"},\"value\":\"Publiceringens infrastruktur\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Publiceringens infrastruktur\"},{\"@type\":\"Organization\",\"name\":\"Uppsala Univ, Dept Stat, Uppsala, Sweden.\"},{\"@type\":\"Organization\",\"name\":\"Department of Statistics, Uppsala University, Uppsala, Sweden\"},{\"@type\":\"Organization\",\"hasAffiliation\":[{\"@type\":\"Organization\",\"identifiedBy\":[{\"@type\":\"URI\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"},\"value\":\"uu.se\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Uppsala universitet\"}],\"identifiedBy\":[{\"@type\":\"Local\",\"source\":{\"@type\":\"Source\",\"code\":\"uu.se\"},\"value\":\"Statistiska institutionen\"}],\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"name\":\"Statistiska institutionen\"},{\"@type\":\"Organization\",\"name\":\"KTH Royal Inst Technol, KTH Lib, Stockholm, Sweden\"}],\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/aut\"}]}],\"electronicLocator\":[{\"@type\":\"Resource\",\"uri\":\"https://doi.org/10.1007/s11192-019-03121-z\"},{\"@type\":\"Resource\",\"uri\":\"http://umu.diva-portal.org/smash/get/diva2:1316344/FULLTEXT01.pdf\"},{\"@type\":\"Resource\",\"uri\":\"http://uu.diva-portal.org/smash/get/diva2:1340381/FULLTEXT01.pdf\"}],\"genreForm\":[{\"@id\":\"https://id.kb.se/term/swepub/svep/ref\"},{\"@id\":\"https://id.kb.se/term/swepub/publication/journal-article\"}],\"hasNote\":[{\"@type\":\"CreatorCount\",\"label\":\"2\"},{\"@id\":\"https://id.kb.se/term/swepub/Published\",\"@type\":\"PublicationStatus\"},{\"@type\":\"Note\",\"label\":\"QC 20190904\"}],\"hasTitle\":[{\"@type\":\"Title\",\"mainTitle\":\"Comparison of publication-level approaches to ex-post citation normalization\"}],\"language\":[{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\",\"langCode\":\"eng\",\"source\":{\"@type\":\"Source\",\"code\":\"iso639-2b\"}}],\"subject\":[{\"@type\":\"Topic\",\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Algorithmically constructed classification system approach\"},{\"@type\":\"Topic\",\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Citation impact\"},{\"@type\":\"Topic\",\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Field normalization\"},{\"@type\":\"Topic\",\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Item-oriented approach\"},{\"@type\":\"Topic\",\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Research evaluation\"},{\"@id\":\"https://id.kb.se/term/uka/50805\",\"@type\":\"Topic\",\"broader\":{\"broader\":{\"prefLabel\":\"Social Sciences\"},\"prefLabel\":\"Media and Communications\"},\"code\":\"50805\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Information Studies\"},{\"@id\":\"https://id.kb.se/term/uka/50805\",\"@type\":\"Topic\",\"broader\":{\"broader\":{\"prefLabel\":\"Samh\\u00e4llsvetenskap\"},\"prefLabel\":\"Medie- och kommunikationsvetenskap\"},\"code\":\"50805\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"prefLabel\":\"Biblioteks- och informationsvetenskap\"},{\"@id\":\"https://id.kb.se/term/uka/5\",\"@type\":\"Topic\",\"code\":\"5\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"prefLabel\":\"Samh\\u00e4llsvetenskap\"},{\"@id\":\"https://id.kb.se/term/uka/508\",\"@type\":\"Topic\",\"broader\":{\"prefLabel\":\"Samh\\u00e4llsvetenskap\"},\"code\":\"508\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"prefLabel\":\"Medie- och kommunikationsvetenskap\"},{\"@id\":\"https://id.kb.se/term/uka/5\",\"@type\":\"Topic\",\"code\":\"5\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Social Sciences\"},{\"@id\":\"https://id.kb.se/term/uka/508\",\"@type\":\"Topic\",\"broader\":{\"prefLabel\":\"Social Sciences\"},\"code\":\"508\",\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"prefLabel\":\"Media and Communications\"}],\"summary\":[{\"@type\":\"Summary\",\"label\":\"In this paper, we compare two sophisticated publication-level approaches to ex-post citation normalization: an item-oriented approach and an approach falling under the general algorithmically constructed classification system approach. Using articles published in core journals in Web of Science (SCIE, SSCI & A&HCI) during 2009 (n=955,639), we first examine, using the measure Proportion explained variation (PEV), to what extent the publication-level approaches can explain and correct for variation in the citation distribution that stems from subject matter heterogeneity. We then, for the subset of articles from life science and biomedicine (n=456,045), gauge the fairness of the normalization approaches with respect to their ability to identify highly cited articles when subject area is factored out. This is done by utilizing information from publication-level MeSH classifications to create high quality subject matter baselines and by using the measure Deviations from expectations (DE). The results show that the item-oriented approach had the best performance regarding PEV. For DE, only the most fine-grained clustering solution could compete with the item-oriented approach. However, the item-oriented approach performed better when cited references were heavily weighted in the similarity calculations.\",\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"}}]},\"meta\":{\"@type\":\"AdminMetadata\",\"assigner\":{\"@type\":\"Agent\",\"label\":\"umu\"},\"creationDate\":\"2019-05-17\"},\"partOf\":[{\"@type\":\"Work\",\"hasInstance\":{\"@type\":\"Instance\",\"extent\":[{\"@type\":\"Extent\",\"label\":\"283-300\"}]},\"hasTitle\":[{\"@type\":\"Title\",\"issueNumber\":\"1\",\"mainTitle\":\"Scientometrics\",\"volumeNumber\":\"120\"}],\"identifiedBy\":[{\"@type\":\"ISSN\",\"qualifier\":\"ISSN\",\"value\":\"0138-9130\"},{\"@type\":\"ISSN\",\"qualifier\":\"EISSN\",\"value\":\"1588-2861\"}]}],\"provisionActivity\":[],\"publication\":[{\"@type\":\"Publication\",\"agent\":{\"@type\":\"Agent\",\"label\":\"Springer\"},\"date\":\"2019\"}],\"usageAndAccessPolicy\":[{\"@type\":\"AccessPolicy\",\"label\":\"gratis\"},{\"@type\":\"AccessPolicy\",\"label\":\"gratis\"}]}";

        ObjectMapper mapper = new ObjectMapper();


        JsonNode obj = mapper.readTree(hej2);


        ////////////


        //TODO

        //for each AUTHOR
        //external to swepub contributors --> check if it is a duplicate
        //else get local id (crco0001 etc)
        //get university

        JsonNode contrib = obj.get("instanceOf").get("contribution");

        HashSet<String> a = new HashSet<>();

        for(JsonNode n : contrib) {

            System.out.println(n);

            System.out.println( n.size() );
            System.out.println(n.get("@type")); // contribution

           System.out.println( n.get("agent").getNodeType() );


            JsonNode aff = n.get("hasAffiliation");

            //System.out.println(aff);
            if (aff == null) continue;

            //aff is an array
            //System.out.println(  aff.size() );

            for (JsonNode nn : aff) {

               recursiveGetAffils(nn,a);

            }

        }


        System.out.println(a);


       ////////////////


    }

}
