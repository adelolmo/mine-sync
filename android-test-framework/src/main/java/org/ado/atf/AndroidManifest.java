package org.ado.atf;

import org.w3c.dom.Document;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * Class description here.
 *
 * @author andoni
 * @since 15.03.2014
 */
public class AndroidManifest {

    private Document doc;
    private XPath xpath;

    NamespaceContext context = new NamespaceContextMap(
            "android", "http://schemas.android.com/apk/res/android",
            "bar", "http://bar",
            "def", "http://def");

    public AndroidManifest(File manifestFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(manifestFile);
            xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(context);
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse manifest file", e);
        }
    }

/*    private NamespaceContext createNamespaceContext() {
        XpathNamespaceContext namespaceContext = new XpathNamespaceContext();
        namespaceContext.setNamespace("env", "http://schemas.xmlsoap.org/soap/envelope/");
        namespaceContext.setNamespace("", "http://www.3gpp.org/ftp/Specs/archive/23_series/23.140     /schema/REL-6-MM7-1-4");
        return namespaceContext;
    }*/

    public String getPackage() {
        try {
            XPathExpression expr = xpath.compile("/manifest/@package");
            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve application package from manifest file", e);
        }
    }

    public int getVersionCode() {
        try {
            XPathExpression expr = xpath.compile("/manifest/@versionCode");
            return Integer.valueOf((String) expr.evaluate(doc, XPathConstants.STRING));
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve application package from manifest file", e);
        }
    }
}
