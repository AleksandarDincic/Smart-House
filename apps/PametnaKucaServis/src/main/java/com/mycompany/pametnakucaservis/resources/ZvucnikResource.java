/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pametnakucaservis.resources;

import authorization.Authorization;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author adinc
 */
@Path("zvucnik")
public class ZvucnikResource {

    public static final String NAZIV_UREDJAJ = "SERVIS";
    private static final String ODGOVOR = "odgovor";
    private static final String PUSTI = "pusti";
    private static final String ISTORIJA = "istorija";

    @Resource(lookup = "pametnaKucaFactory")
    private ConnectionFactory factory;

    @Resource(lookup = "pametnaKucaZvucnikTopic")
    private Topic topic;

    private TextMessage formMessage(JMSContext context, String content, String type, String user) throws JMSException {
        TextMessage txtMsg = context.createTextMessage(content);
        txtMsg.setStringProperty("tip", type);
        txtMsg.setStringProperty("korisnik", user);
        txtMsg.setStringProperty("izvor", NAZIV_UREDJAJ);
        return txtMsg;
    }

    @GET
    @Path("pusti")
    public Response pustiPesmu(@Context HttpHeaders httpHeaders, @QueryParam("naziv") String naziv) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);

            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            
            TextMessage txtMsg = formMessage(context, naziv, PUSTI, authParams[0]);
            txtMsg.setBooleanProperty("persist", true);
            producer.send(topic, txtMsg);
            
            return Response.ok("Zahtev poslat").build();
        } catch (JMSException ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }

    @GET
    @Path("istorija")
    public Response istorija(@Context HttpHeaders httpHeaders) {
        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);
            JMSContext context = factory.createContext();

            JMSProducer producer = context.createProducer();
            JMSConsumer consumer = context.createConsumer(topic, "tip='" + ODGOVOR + "' and izvor='"
                    + NAZIV_UREDJAJ + "' and korisnik='" + authParams[0] + "'");
            
            TextMessage txtMsg = formMessage(context, "x", ISTORIJA, authParams[0]);
            producer.send(topic, txtMsg);

            TextMessage replyMsg = (TextMessage) consumer.receive();
            
            return Response.status(Status.OK).type(MediaType.TEXT_PLAIN).entity(replyMsg.getText()).build();
        } catch (JMSException ex) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }
}
