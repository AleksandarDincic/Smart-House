/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.pametnakucaservis.resources;

import authorization.Authorization;
import entities.Korisnik;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author adinc
 */
@Path("korisnik")
@Stateless
public class KorisnikResource {
    
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;
    
    @GET
    public Response testAuth(@Context HttpHeaders httpHeaders){
        return Response.ok("OK").build();
    }
    
    @POST
    public Response register(@Context HttpHeaders httpHeaders){
        String[] authParams = Authorization.getAuthParams(httpHeaders);
    
        TypedQuery<Korisnik> query = em.createQuery("SELECT k FROM Korisnik k WHERE k.ime=:ime", Korisnik.class);
            
        query.setParameter("ime", authParams[0]);
        List<Korisnik> korisnici = query.getResultList();
        
        if(!korisnici.isEmpty())
            return Response.status(Status.CONFLICT).entity("Korisnik vec postoji").build();
        
        Korisnik korisnik = new Korisnik();
        korisnik.setIme(authParams[0]);
        korisnik.setSifra(authParams[1]);
        
        em.persist(korisnik);
        
        return Response.ok("Korisnik dodat").build();
    }
}
