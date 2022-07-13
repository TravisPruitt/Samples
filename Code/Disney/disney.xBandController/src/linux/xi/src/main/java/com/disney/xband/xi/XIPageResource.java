package com.disney.xband.xi;

import com.disney.xband.xi.model.XIPageDAO;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;


/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 7/18/12
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */

@Path("/page")
public class XIPageResource extends XiResource
{
    XIPageDAO dao = new XIPageDAO();
    @Context ServletContext context;

    public XIPageResource() {
    }

    /*
    @Path("/prop/{filename}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String propPage(
            @PathParam("filename") String fileName)
    {
         if(fileName.equalsIgnoreCase("index.html") ||
           fileName.equalsIgnoreCase("1/index.html") ||
                fileName.equalsIgnoreCase("testenc.html") ||
           fileName.equalsIgnoreCase("daily.html")) {

            String writePath = context.getRealPath("/");
            logger.info("writing to path:" + writePath);
            return dao.propXiPage(writePath, fileName);
        }
        else
            return dao.errorMessage("Invalid file path");
    }*/

    @Path("/guidcheck/{filename}/{guid}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String guidCheck(@PathParam("guid") String guid,
        @PathParam("filename") String fileName) {

        if(fileName.equalsIgnoreCase("index.html") ||
                fileName.equalsIgnoreCase("entry.html") ||
                fileName.equalsIgnoreCase("ping.html") ||
                fileName.equalsIgnoreCase("testenc.html") ||
                fileName.equalsIgnoreCase("daily.html")) {

            String writePath = context.getRealPath("/");
            return dao.guidCheck(guid, fileName, writePath);
        }
        else
            return dao.errorMessage("Invalid file path");
    }

    @Path("/set/{guid}/{filename}/{htmldata}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String setPage(
            @PathParam("guid") String guid,
            @PathParam("filename") String fileName,
            @PathParam("htmldata") String htmlData)
    {
        logger.debug("setPage: " + fileName);
        if(fileName.equalsIgnoreCase("index.html") ||
                fileName.equalsIgnoreCase("entry.html") ||
                fileName.equalsIgnoreCase("ping.html") ||
           fileName.equalsIgnoreCase("daily.html")) {

            String writePath = context.getRealPath("/");
            logger.info("writing to path:" + writePath);
            return dao.setXiPage(guid, fileName, writePath, XiResource.convertHexToString(htmlData));

        }
        else
            return dao.errorMessage("Invalid file path");
    }

    @Path("/uploadEnc")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String uploadEncPage(@FormDataParam("upfile") InputStream inputStream,
                         @FormDataParam("upfile") FormDataContentDisposition fileInfo,
                         @FormDataParam("filename") String fileName,
                         @FormDataParam("guid") String description)
    {
        logger.debug("setPage: " + fileName);

        if(fileName.equalsIgnoreCase("index.html") ||
           fileName.equalsIgnoreCase("entry.html") ||
           fileName.equalsIgnoreCase("ping.html") ||
                fileName.equalsIgnoreCase("testenc.html") ||
           fileName.equalsIgnoreCase("daily.html")) {

            String writePath = context.getRealPath("/");
            logger.info("writing to path:" + writePath);
            return dao.uploadEncPage(fileName, writePath, description, inputStream);
        }
        else
            return dao.errorMessage("Invalid file path");

    }

}
