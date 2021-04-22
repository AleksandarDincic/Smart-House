/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pametnakucaservis.resources;

import authorization.Authorization;
import static com.mycompany.pametnakucaservis.resources.ZvucnikResource.NAZIV_UREDJAJ;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author adinc
 */
@Path("planer")
public class PlanerResource {

    private static final String DODAJ = "dodaj";
    private static final String IZMENI = "izmeni";
    private static final String LISTAJ = "listaj";
    private static final String OBRISI = "obrisi";
    private static final String ADRESA = "adresa";
    private static final String ODGOVOR = "odgovor";
    private static final String ALARM = "alarm";
    private static final String KALKULATOR = "kalkulator";

    @Resource(lookup = "pametnaKucaFactory")
    private ConnectionFactory factory;

    @Resource(lookup = "pametnaKucaPlanerTopic")
    private Topic topic;

    private TextMessage formMessage(JMSContext context, String content, String type, String user) throws JMSException {
        TextMessage txtMsg = context.createTextMessage(content);
        txtMsg.setStringProperty("tip", type);
        txtMsg.setStringProperty("korisnik", user);
        txtMsg.setStringProperty("izvor", NAZIV_UREDJAJ);
        return txtMsg;
    }

    @GET
    public Response dohvObaveze(@Context HttpHeaders httpHeaders) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);

            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic, "tip='" + ODGOVOR + "' and izvor='" + NAZIV_UREDJAJ + "' and korisnik='" + authParams[0] + "'");

            TextMessage txtMsg = formMessage(context, "x", LISTAJ, authParams[0]);
            producer.send(topic, txtMsg);

            TextMessage replyMsg = (TextMessage) consumer.receive();
            return Response.ok(replyMsg.getText()).build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }

    @POST
    public Response dodajObavezu(@Context HttpHeaders httpHeaders, @QueryParam("destinacija") String destinacija, @QueryParam("pocetak") String pocetak,
            @QueryParam("trajanjeSat") Integer trajanjeSat, @QueryParam("trajanjeMinut") Integer trajanjeMinut) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);

            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic, "tip='" + ODGOVOR + "' and izvor='" + NAZIV_UREDJAJ + "' and korisnik='" + authParams[0] + "'");

            TextMessage txtMsg = formMessage(context, pocetak, DODAJ, authParams[0]);
            txtMsg.setIntProperty("trajanjeSat", trajanjeSat);
            txtMsg.setIntProperty("trajanjeMinut", trajanjeMinut);

            if (destinacija != null) {
                txtMsg.setStringProperty("destinacija", destinacija);
            }

            producer.send(topic, txtMsg);
            TextMessage replyMsg = (TextMessage) consumer.receive();
            return Response.ok(replyMsg.getText()).build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }

    @POST
    @Path("adresa")
    public Response postaviAdresu(@Context HttpHeaders httpHeaders, @QueryParam("adresa") String adresa) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);

            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            TextMessage txtMsg = formMessage(context, adresa, ADRESA, authParams[0]);

            producer.send(topic, txtMsg);

            return Response.ok("Zahtev poslat").build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }

    @PUT
    public Response izmeniObavezu(@Context HttpHeaders httpHeaders, @QueryParam("idOb") int idOb, @QueryParam("destinacija") String destinacija, @QueryParam("pocetak") String pocetak,
            @QueryParam("trajanjeSat") Integer trajanjeSat, @QueryParam("trajanjeMinut") Integer trajanjeMinut) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);

            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic, "tip='" + ODGOVOR + "' and izvor='" + NAZIV_UREDJAJ + "' and korisnik='" + authParams[0] + "'");

            TextMessage txtMsg = formMessage(context, pocetak, IZMENI, authParams[0]);
            txtMsg.setIntProperty("idOb", idOb);
            txtMsg.setIntProperty("trajanjeSat", trajanjeSat);
            txtMsg.setIntProperty("trajanjeMinut", trajanjeMinut);

            if (destinacija != null) {
                txtMsg.setStringProperty("destinacija", destinacija);
            }

            producer.send(topic, txtMsg);
            TextMessage replyMsg = (TextMessage) consumer.receive();
            return Response.ok(replyMsg.getText()).build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }

    @DELETE
    public Response obrisiObavezu(@Context HttpHeaders httpHeaders, @QueryParam("idOb") int idOb) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);

            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();

            TextMessage txtMsg = formMessage(context, Integer.toString(idOb), OBRISI, authParams[0]);

            producer.send(topic, txtMsg);

            return Response.ok("Zahtev poslat").build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }

    @POST
    @Path("alarm")
    public Response postaviAlarm(@Context HttpHeaders httpHeaders, @QueryParam("idOb") int idOb) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);

            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic, "tip='" + ODGOVOR + "' and izvor='" + NAZIV_UREDJAJ + "' and korisnik='" + authParams[0] + "'");

            TextMessage txtMsg = formMessage(context, Integer.toString(idOb), ALARM, authParams[0]);

            producer.send(topic, txtMsg);

            TextMessage replyMsg = (TextMessage) consumer.receive();
            return Response.ok(replyMsg.getText()).build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }

    @GET
    @Path("kalkulator")
    public Response izracunajRazdaljinu(@Context HttpHeaders httpHeaders, @QueryParam("start") String start, @QueryParam("end") String end) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);

            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic, "tip='" + ODGOVOR + "' and izvor='" + NAZIV_UREDJAJ + "' and korisnik='" + authParams[0] + "'");

            TextMessage txtMsg = formMessage(context, end, KALKULATOR, authParams[0]);
            if(start != null)
                txtMsg.setStringProperty("start", start);
            
            producer.send(topic, txtMsg);

            TextMessage replyMsg = (TextMessage) consumer.receive();
            return Response.ok(replyMsg.getText()).build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }
}
