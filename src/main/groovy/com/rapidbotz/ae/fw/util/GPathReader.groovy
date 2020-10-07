package com.rapidbotz.ae.fw.util

import groovy.json.JsonSlurper

class GPathReader {
    static void main(String[] args) {
        def jsonPath = "name.first[0].alias"
        def jsonString = "{ \"name\": [{\"first\": {\"alias\":\"John Doe\"}}, {\"first\": {\"alias\":\"John Doe1\"}}] }"
        String jsonResponse = GPathReader.getJsonPathValue(jsonString, jsonPath)
        println(jsonResponse)

        def xmlPath = "name.first[1].alias"
        def xmlString = "<contact><name> <first><alias>John Doe</alias></first> <first><alias>John Doe1</alias></first> </name></contact>"
        String xmlResponse = GPathReader.getXmlPathValue(xmlString, xmlPath)
        println(xmlResponse)
    }

    String getJsonPathValue(String jsonString, String jsonPath) {
        def jsonSlurper = new JsonSlurper()
        def object = jsonSlurper.parseText(jsonString)
        return this.doGetPathValue(object, jsonPath)
    }

    String getXmlPathValue(String xmlString, String xmlPath) {
        def xmlSlurper = new XmlSlurper()
        def object = xmlSlurper.parseText(xmlString)
        return this.doGetPathValue(object, xmlPath)
    }

    private String doGetPathValue(Object object, String path) {
        path = path.replaceAll("\\[", ".#").replaceAll("]", "")
        String[] pathItems = path.split("\\.")

        pathItems.each { item ->
            if(item.contains("#")) {
                item = item.replaceAll("#", "")
                //if(object instanceof ArrayList)
                    object = this.getPathValue(object, new Integer(item))
            } else {
                object = this.getPathValue(object, item)
            }
        }
        if(object instanceof String) {
            return object
        } else if(object instanceof ArrayList) {
            return object.join("||")
        } else {
            return object.toString()
        }
    }

    private Object getPathValue(Object object, String path) {
        return object[path]
    }

    private Object getPathValue(Object object, Integer path) {
        return object[path]
    }
}