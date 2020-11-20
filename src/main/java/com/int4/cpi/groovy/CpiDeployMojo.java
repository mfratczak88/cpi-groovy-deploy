package com.int4.cpi.groovy;


import okhttp3.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.net.www.http.HttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Mojo(name = "cpi-groovy-deploy", defaultPhase = LifecyclePhase.COMPILE)
public final class CpiDeployMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    private Properties properties;
    private final String CONFIG_FILE_NAME = "cpi.properties";
    private Edm edm;
    private HttpCaller httpCaller;
    private final String resourcesURL = "IntegrationPackages('DEVPDTPProductDataTransformationPlatform')/IntegrationDesigntimeArtifacts(Id='DEV-PDTP-ProductCatalog-ProductVersion-CU-Stibo-To-CP',Version='Active')/Resources";
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        setUpHttpCaller();
        loadProperties();
        upload();
    }

    private void setUpHttpCaller() {
        httpCaller = new HttpCaller(username(), pass());
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            properties.load(
                    new FileInputStream(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResource("")
                                    .getPath() + CONFIG_FILE_NAME
                    )
            );
        } catch (Exception e) {
            // todo: create specific exception class
            throw new RuntimeException(e.getMessage());
        }
    }

    private String username() {
        return properties.getProperty("username");
    }

    private String pass() {
        return properties.getProperty("password");
    }

    private String iFlowName() {
        return properties.getProperty("iflow");
    }

    private String iPackage() {
        return properties.getProperty("integration_package");
    }

    private String cpiUrl() {
        return properties.getProperty("cpi_url");
    }

    private void upload() {
        loadMetaData();

    }

    private ODataFeed iFlowResources(){
        return null;
    }

    private void existsAlready() {

    }

    private void loadMetaData() {
        try {
            edm = EntityProvider.readMetadata(httpCaller.call(cpiUrl()), false);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }




}
