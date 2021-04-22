/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authorization;
import entities.Korisnik;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author adinc
 */
@Provider
public class Authorization implements ContainerRequestFilter{
    
    public static String[] getAuthParams(HttpHeaders httpHeaders){
        List<String> authHeaderValues = httpHeaders.getRequestHeader("Authorization");
        
        String authHeaderValue = authHeaderValues.get(0);
        String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
        StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
        String[] retStrings = new String[2];
        retStrings[0] = stringTokenizer.nextToken();
        retStrings[1] = stringTokenizer.nextToken();            

        return retStrings;
    }
    
    @PersistenceContext(unitName = "my_persistence_unit")
    EntityManager em;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try{
            List<String> authHeaderValues = requestContext.getHeaders().get("Authorization");

            if(authHeaderValues != null && authHeaderValues.size() > 0){
                String authHeaderValue = authHeaderValues.get(0);
                String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
                StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");


                String username = stringTokenizer.nextToken();
                String password = stringTokenizer.nextToken();

                String method = requestContext.getMethod();
                UriInfo uriInfo = requestContext.getUriInfo();
                List<PathSegment> pathSegments = uriInfo.getPathSegments();
                String endpointName = pathSegments.get(0).getPath();

                TypedQuery<Korisnik> query = em.createQuery("SELECT k FROM Korisnik k WHERE k.ime=:ime AND k.sifra=:sifra", Korisnik.class);

                query.setParameter("ime", username);
                query.setParameter("sifra", password);
                List<Korisnik> korisnici = query.getResultList();

                if(korisnici.isEmpty() && !(endpointName.equals("korisnik") && method.equals("POST"))){
                    Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Pogresno korisnicko ime ili lozinka").build();
                    requestContext.abortWith(response);
                }
            }
            else{
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Posaljite kredencijale").build();
                requestContext.abortWith(response);
            }
        }
        catch(NoSuchElementException ex){
            Response response = Response.status(Response.Status.BAD_REQUEST).entity("Neispravni kredencijali").build();
            requestContext.abortWith(response);
        }
    }


}