package com.squad13.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.squad13.util.Local;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@ApplicationScoped
public class AggregatorService {

    @Inject
    MongoClient mongo;

    @Inject
    @Location("doc.qute.html")
    Template doc;

    @Inject
    PdfService pdfService;

    public byte[] generatePdf(String especId) throws IOException {
        System.out.println("generatePdf called with ID: " + especId);

        MongoCollection<Document> especCol = mongo
                .getDatabase("monolito")
                .getCollection("especificacoes");

        ObjectId especIdObject = new ObjectId(especId);
        Document espec = especCol.find(new Document("_id", especIdObject)).first();

        if (espec == null) {
            throw new NotFoundException("Especificação não encontrada");
        }

        MongoCollection<Document> locaisCol = mongo
                .getDatabase("monolito")
                .getCollection("locais");

        List<Document> locais = locaisCol.find(
                new Document("_id",
                        new Document("$in", espec.getList("locaisIds", ObjectId.class)))
        ).into(new ArrayList<>());

        MongoCollection<Document> ambCol = mongo
                .getDatabase("monolito")
                .getCollection("ambientes");

        MongoCollection<Document> itemCol = mongo
                .getDatabase("monolito")
                .getCollection("items");

        for (Document local : locais) {

            String localKey = local.getString("local");
            Local enumValue = Local.valueOf(localKey);
            local.put("localEnum", enumValue);

            List<ObjectId> ambIds = local.getList("ambienteIds", ObjectId.class);

            List<Document> ambientes = ambCol.find(
                    new Document("_id", new Document("$in", ambIds))
            ).into(new ArrayList<>());

            for (Document ambiente : ambientes) {

                List<ObjectId> itemIds = ambiente.getList("itemIds", ObjectId.class);

                List<Document> items = itemCol.find(
                        new Document("_id", new Document("$in", itemIds))
                ).into(new ArrayList<>());

                ambiente.put("items", items);
            }

            local.put("ambientes", ambientes);
        }

        MongoCollection<Document> matCol = mongo
                .getDatabase("monolito")
                .getCollection("materiais");

        MongoCollection<Document> marcaCol = mongo
                .getDatabase("monolito")
                .getCollection("marcas");

        List<Document> materiais = matCol.find(
                new Document("_id",
                        new Document("$in", espec.getList("materiaisIds", ObjectId.class)))
        ).into(new ArrayList<>());

        for (Document material : materiais) {
            List<ObjectId> marcaIds = material.getList("marcaIds", ObjectId.class);

            List<String> marcaNames = marcaCol.find(
                            new Document("_id", new Document("$in", marcaIds))
                    ).into(new ArrayList<>())
                    .stream()
                    .map(doc -> doc.getString("name"))
                    .toList();

            material.put("marcas", marcaNames);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("titulo", espec.getString("name"));
        data.put("localizacao", espec.getString("local"));
        data.put("descricao", espec.getString("desc"));
        data.put("observacoes", espec.getList("obs", String.class));

        data.put("locais", locais);
        data.put("materiais", materiais);

        System.out.println("Hi");
        byte[] headerBytes = loadResource("images/header.jpg");
        String headerBase64 = Base64.getEncoder().encodeToString(headerBytes);

        byte[] footerBytes = loadResource("images/footer.jpg");
        String footerBase64 = Base64.getEncoder().encodeToString(footerBytes);

        System.out.println("Hi2");
        data.put("headerBase64", headerBase64);
        data.put("footerBase64", footerBase64);

        String html = doc.data(data).render();
        return pdfService.htmlToPdf(html);
    }

    private byte[] loadResource(String path) throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            throw new IOException("Resource not found on classpath: " + path);
        }
        return stream.readAllBytes();
    }
}