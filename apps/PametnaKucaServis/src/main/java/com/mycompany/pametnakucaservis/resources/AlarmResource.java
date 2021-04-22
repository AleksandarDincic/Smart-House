/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pametnakucaservis.resources;

import authorization.Authorization;
import static com.mycompany.pametnakucaservis.resources.ZvucnikResource.NAZIV_UREDJAJ;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author adinc
 */
@Path("alarm")
public class AlarmResource {

    private static final String NAPRAVI = "napravi";
    private static final String ZVONO = "zvono";

    @Resource(lookup = "pametnaKucaFactory")
    private ConnectionFactory factory;

    @Resource(lookup = "pametnaKucaAlarmTopic")
    private Topic topic;

    private TextMessage formMessage(JMSContext context, String content, String type, String user) throws JMSException {
        TextMessage txtMsg = context.createTextMessage(content);
        txtMsg.setStringProperty("tip", type);
        txtMsg.setStringProperty("korisnik", user);
        txtMsg.setStringProperty("izvor", NAZIV_UREDJAJ);
        return txtMsg;
    }

    @POST
    @Path("napravi")
    public Response napraviAlarm(@Context HttpHeaders httpHeaders, @QueryParam("vreme") String vreme, @QueryParam("perioda") String perioda) {

        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);
            
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            
            TextMessage txtMsg = formMessage(context, vreme, NAPRAVI, authParams[0]);
            
            if(perioda != null)
                txtMsg.setStringProperty("perioda", perioda);
            producer.send(topic, txtMsg);
            
            return Response.ok("Zahtev poslat").build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }
    
    @POST
    @Path("zvono")
    public Response postaviZvono(@Context HttpHeaders httpHeaders, @QueryParam("naziv") String naziv) {

        try {
            String[] authParams = Authorization.getAuthParams(httpHeaders);
            
            JMSContext context = factory.createContext();
            JMSProducer producer = context.createProducer();
            
            TextMessage txtMsg = formMessage(context, naziv, ZVONO, authParams[0]);
            
            producer.send(topic, txtMsg);
            
            return Response.ok("Zahtev poslat").build();
        } catch (JMSException ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Greska").build();
        }
    }
}