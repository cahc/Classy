package Database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class Playground2 {


    private static void recursive(final JsonNode node, List<JsonNode> affils) {


        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();

        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> field = fieldsIterator.next();

            final String key = field.getKey();
          //  System.out.println("Key: " + key);
            if("hasAffiliation".equals(key)) affils.add( field.getValue() );

            final JsonNode value = field.getValue();

            if (value.isContainerNode()) {


                Iterator<JsonNode> iter = value.elements();

                while (iter.hasNext()) {

                    recursive( iter.next(), affils ); //recursion

                }


            } else {

                 //not implemented here

            }
        }


    }


    public static void main(String[] arg) throws IOException {

        String hej6 ="{\"master\":{\"@context\":\"https://id.kb.se/context.jsonld\",\"@id\":\"oai:lup.lub.lu.se:d26ce7bf-7df3-426d-b255-5ca5694301af\",\"@type\":\"Instance\",\"instanceOf\":{\"@type\":\"Text\",\"genreForm\":[{\"@id\":\"https://id.kb.se/term/swepub/svep/vet\"},{\"@id\":\"https://id.kb.se/term/swepub/publication/book-chapter\"},{\"@id\":\"https://id.kb.se/term/swepub/BookChapter\"}],\"language\":[{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/ger\",\"code\":\"ger\",\"langCode\":\"ger\",\"source\":{\"@type\":\"Source\",\"code\":\"iso639-2b\"}}],\"hasTitle\":[{\"@type\":\"Title\",\"mainTitle\":\"Habermas in Schweden: Eine Rezeption mit Hindernissen\"},{\"@type\":\"VariantTitle\",\"mainTitle\":\"Habermas i Sverige: En reception med f\\u00f6rhinder\"}],\"contribution\":[{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"givenName\":\"Carl-G\\u00f6ran\",\"familyName\":\"Heidegren\",\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"soc-che\",\"source\":{\"@type\":\"Source\",\"code\":\"lu\"}}]},\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/aut\"}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Sociologi\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000692\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000689\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Sociologiska institutionen\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000689\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000675\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Samh\\u00e4llsvetenskapliga institutioner och centrumbildningar\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000675\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000670\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Samh\\u00e4llsvetenskapliga fakulteten\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000670\",\"source\":{\"@type\":\"Source\",\"code\":\"lu.se\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Lunds universitet\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"URI\",\"value\":\"lu.se\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"}}]}]}]}]}]},{\"@type\":\"Organization\",\"name\":\"Sociology\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000692\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000689\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Department of Sociology\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000689\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000675\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Departments of Administrative, Economic and Social Sciences\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000675\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000670\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Faculty of Social Sciences\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000670\",\"source\":{\"@type\":\"Source\",\"code\":\"lu.se\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Lund University\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"URI\",\"value\":\"lu.se\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"}}]}]}]}]}]}]},{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"givenName\":\"Mikael\",\"familyName\":\"Carleheden\"},\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/aut\"}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"University of Copenhagen\"}]}],\"subject\":[{\"@id\":\"https://id.kb.se/term/uka/504\",\"@type\":\"Topic\",\"code\":\"504\",\"prefLabel\":\"Sociologi\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"broader\":{\"prefLabel\":\"Samh\\u00e4llsvetenskap\"}},{\"@id\":\"https://id.kb.se/term/uka/504\",\"@type\":\"Topic\",\"code\":\"504\",\"prefLabel\":\"Sociology\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"broader\":{\"prefLabel\":\"Social Sciences\"}},{\"@id\":\"https://id.kb.se/term/uka/5\",\"@type\":\"Topic\",\"code\":\"5\",\"prefLabel\":\"Samh\\u00e4llsvetenskap\",\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"}},{\"@id\":\"https://id.kb.se/term/uka/5\",\"@type\":\"Topic\",\"code\":\"5\",\"prefLabel\":\"Social Sciences\",\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"}}],\"hasNote\":[{\"@type\":\"CreatorCount\",\"label\":\"2\"},{\"@type\":\"PublicationStatus\",\"@id\":\"https://id.kb.se/term/swepub/Published\"}]},\"identifiedBy\":[{\"@type\":\"URI\",\"value\":\"https://lup.lub.lu.se/record/d26ce7bf-7df3-426d-b255-5ca5694301af\"}],\"partOf\":[{\"@type\":\"Work\",\"hasTitle\":[{\"@type\":\"Title\",\"mainTitle\":\"Habermas global : Wirkungsgeschichte eines Werks - Wirkungsgeschichte eines Werks\",\"subtitle\":\"Wirkungsgeschichte eines Werks\"}],\"identifiedBy\":[{\"@type\":\"ISBN\",\"value\":\"9783518298794\"}],\"hasInstance\":{\"@type\":\"Instance\",\"extent\":[{\"@type\":\"Extent\",\"label\":\"459-475\"}]}}],\"meta\":{\"@type\":\"AdminMetadata\",\"assigner\":{\"@type\":\"Agent\",\"label\":\"lu\"},\"creationDate\":\"2020-03-20T11:18:05+01:00\",\"changeDate\":\"2021-02-18T13:55:46+01:00\"},\"publication\":[{\"@type\":\"Publication\",\"agent\":{\"@type\":\"Agent\",\"label\":\"Suhrkamp\"},\"date\":\"2019\"}]},\"publications\":[{\"@context\":\"https://id.kb.se/context.jsonld\",\"@id\":\"oai:lup.lub.lu.se:d26ce7bf-7df3-426d-b255-5ca5694301af\",\"@type\":\"Instance\",\"instanceOf\":{\"@type\":\"Text\",\"genreForm\":[{\"@id\":\"https://id.kb.se/term/swepub/svep/vet\"},{\"@id\":\"https://id.kb.se/term/swepub/publication/book-chapter\"},{\"@id\":\"https://id.kb.se/term/swepub/BookChapter\"}],\"language\":[{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/ger\",\"code\":\"ger\",\"langCode\":\"ger\",\"source\":{\"@type\":\"Source\",\"code\":\"iso639-2b\"}}],\"hasTitle\":[{\"@type\":\"Title\",\"mainTitle\":\"Habermas in Schweden: Eine Rezeption mit Hindernissen\"},{\"@type\":\"VariantTitle\",\"mainTitle\":\"Habermas i Sverige: En reception med f\\u00f6rhinder\"}],\"contribution\":[{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"givenName\":\"Carl-G\\u00f6ran\",\"familyName\":\"Heidegren\",\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"soc-che\",\"source\":{\"@type\":\"Source\",\"code\":\"lu\"}}]},\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/aut\"}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Sociologi\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000692\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000689\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Sociologiska institutionen\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000689\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000675\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Samh\\u00e4llsvetenskapliga institutioner och centrumbildningar\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000675\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000670\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Samh\\u00e4llsvetenskapliga fakulteten\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000670\",\"source\":{\"@type\":\"Source\",\"code\":\"lu.se\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Lunds universitet\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"identifiedBy\":[{\"@type\":\"URI\",\"value\":\"lu.se\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"}}]}]}]}]}]},{\"@type\":\"Organization\",\"name\":\"Sociology\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000692\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000689\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Department of Sociology\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000689\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000675\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Departments of Administrative, Economic and Social Sciences\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000675\",\"source\":{\"@type\":\"Source\",\"code\":\"v1000670\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Faculty of Social Sciences\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"Local\",\"value\":\"v1000670\",\"source\":{\"@type\":\"Source\",\"code\":\"lu.se\"}}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"Lund University\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"identifiedBy\":[{\"@type\":\"URI\",\"value\":\"lu.se\",\"source\":{\"@type\":\"Source\",\"code\":\"kb.se\"}}]}]}]}]}]}]},{\"@type\":\"Contribution\",\"agent\":{\"@type\":\"Person\",\"givenName\":\"Mikael\",\"familyName\":\"Carleheden\"},\"role\":[{\"@id\":\"http://id.loc.gov/vocabulary/relators/aut\"}],\"hasAffiliation\":[{\"@type\":\"Organization\",\"name\":\"University of Copenhagen\"}]}],\"subject\":[{\"@id\":\"https://id.kb.se/term/uka/504\",\"@type\":\"Topic\",\"code\":\"504\",\"prefLabel\":\"Sociologi\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/swe\",\"code\":\"swe\"},\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"broader\":{\"prefLabel\":\"Samh\\u00e4llsvetenskap\"}},{\"@id\":\"https://id.kb.se/term/uka/504\",\"@type\":\"Topic\",\"code\":\"504\",\"prefLabel\":\"Sociology\",\"language\":{\"@type\":\"Language\",\"@id\":\"https://id.kb.se/language/eng\",\"code\":\"eng\"},\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"},\"broader\":{\"prefLabel\":\"Social Sciences\"}},{\"@id\":\"https://id.kb.se/term/uka/5\",\"@type\":\"Topic\",\"code\":\"5\",\"prefLabel\":\"Samh\\u00e4llsvetenskap\",\"language\":{\"@id\":\"https://id.kb.se/language/swe\",\"@type\":\"Language\",\"code\":\"swe\"},\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"}},{\"@id\":\"https://id.kb.se/term/uka/5\",\"@type\":\"Topic\",\"code\":\"5\",\"prefLabel\":\"Social Sciences\",\"language\":{\"@id\":\"https://id.kb.se/language/eng\",\"@type\":\"Language\",\"code\":\"eng\"},\"inScheme\":{\"@id\":\"https://id.kb.se/term/uka/\",\"@type\":\"ConceptScheme\",\"code\":\"uka.se\"}}],\"hasNote\":[{\"@type\":\"CreatorCount\",\"label\":\"2\"},{\"@type\":\"PublicationStatus\",\"@id\":\"https://id.kb.se/term/swepub/Published\"}]},\"identifiedBy\":[{\"@type\":\"URI\",\"value\":\"https://lup.lub.lu.se/record/d26ce7bf-7df3-426d-b255-5ca5694301af\"}],\"partOf\":[{\"@type\":\"Work\",\"hasTitle\":[{\"@type\":\"Title\",\"mainTitle\":\"Habermas global : Wirkungsgeschichte eines Werks - Wirkungsgeschichte eines Werks\",\"subtitle\":\"Wirkungsgeschichte eines Werks\"}],\"identifiedBy\":[{\"@type\":\"ISBN\",\"value\":\"9783518298794\"}],\"hasInstance\":{\"@type\":\"Instance\",\"extent\":[{\"@type\":\"Extent\",\"label\":\"459-475\"}]}}],\"meta\":{\"@type\":\"AdminMetadata\",\"assigner\":{\"@type\":\"Agent\",\"label\":\"lu\"},\"creationDate\":\"2020-03-20T11:18:05+01:00\",\"changeDate\":\"2021-02-18T13:55:46+01:00\"},\"publication\":[{\"@type\":\"Publication\",\"agent\":{\"@type\":\"Agent\",\"label\":\"Suhrkamp\"},\"date\":\"2019\"}]}],\"publication_count\":1}";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(hej6);

        JsonNode master = root.get("master"); //the master record in case of duplicates. Secondary records in publications[] but hey are of no interest here. No particular order is implied in publications[].
        //array
        JsonNode contrib = master.get("instanceOf").get("contribution");

        List<JsonNode> nodeList = new ArrayList<>();


        System.out.println( contrib.get(0) );

        JsonNode node = contrib.get(0);

        recursive(node,nodeList);


        System.out.println(nodeList.size());

        for(JsonNode jsonArray : nodeList) {


            //array can be of size > 1, ofthen different languages for same unit
            for(int i=0; i<jsonArray.size(); i++) {

                JsonNode affilObject = jsonArray.get(i);

                //only kb authorized orgs
                if ("Organization".equals(affilObject.get("@type").textValue()) && "swe".equals(affilObject.get("language").get("code").textValue()) && "kb.se".equals(affilObject.get("identifiedBy").get(0).get("source").get("code").textValue()))  System.out.println(affilObject.get("name"));

            }


        }



    }



        }




