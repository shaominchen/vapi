/*
 * Copyright 2012 VMware, Inc.    All rights reserved.
 */
package com.vmware.vapi.idl.transformer

import com.vmware.vapi.idl.model.*

import org.apache.commons.lang3.StringEscapeUtils

/**
 * Defines the XML language helper for concrete generators such as:
 * HTML, XSD and WSDL/WADL.
 */
class XmlLanguageHelper extends LanguageHelper {

    static final String NAME = "xml"

    //fyi: required factory method to construct an instance
    static XmlLanguageHelper getInstance(LanguageProperties properties, TemplateNamingContext naming) {
        return new XmlLanguageHelper(properties, naming)
    }

    XmlLanguageHelper(LanguageProperties properties, TemplateNamingContext naming) {
        super(NAME, properties, naming)

        // metaprogram our language helper on to the appropriate nodes
        AbstractTypedNode.metaClass.getXmlType = { -> delegate.type }
        AbstractTypedNode.metaClass.getXsdType = { -> delegate.type }
    }

    String escapeXml(String xml) {
        return StringEscapeUtils.escapeXml(xml)
    }

    String attr( String name, Object value ) {
        (value) ? """${name}="${value}" """ : ''
    }

    String attrs( Object node ) {
        StringBuilder sb = new StringBuilder()

        if (node instanceof AbstractNamedNode) {
            def name = node.name?.toString()

            if (name && name != "result") {
                sb << """name="${name}" """
            }
        }
        if (node instanceof AbstractTypedNode) {
            if (node.type.isVoid()) {
            } else if (node.type.isPrimitive()) {
                def val = node.type.getPrimitiveType()
                if (val != "void") {
                    sb << """primitive="${val}" """
                }
            } else if (node.type.isReference()) {
                sb << """structure="${node.type.name}" """
            } else if (node.type.isGeneric()) {
                sb << """generic="${node.type.name}" """
            } else if (node.type.isAnonymous()) {
                sb << """anonymous="true" """
            }
        }
        sb.toString()
    }

    boolean isGeneric(IdlType type, String name) {
        boolean result = false

        while (type.isGeneric()) {
            if (type.name == name) {
                result = true
                break // while
            }
            type = type.arguments[0]
        }
        return result
    }

    boolean isList(IdlType type) {
        return isGeneric(type, 'list')
    }

    boolean isOptional(IdlType type) {
        return isGeneric(type, 'optional')
    }

    String toTableData(IdlType type) {
        String result

        if (type.isAnonymous()) {
	    result = """<a href="#${type.anonymousStructure.name}" >${type.anonymousStructure.name}</a>"""
        } else {
	    result = toComponentForm(type)
        }
        return result
    }

    String toTableDataList(IdlType type) {
        return (isList(type)) ? 'Yes' : ''
    }

    String toTableDataOptional(IdlType type) {
        return (isOptional(type)) ? 'Yes' : ''
    }

    StringWriter sw = new StringWriter()
    PrintWriter anon = new PrintWriter( sw )

    String printAnonymousType( Writer out, Object node, int baseIndent = 4, boolean recursive = false ) {
        def in1 = " " * baseIndent
        def in2 = in1 + "    "
        def in3 = in2 + "    "
        if (node instanceof AbstractTypedNode && node.type.isAnonymous()) {
            if (!recursive) {
                sw.buffer.length = 0
                anon.println ""
            }
            anon.println "${in1}<structure>"
            //println "${in2}<doc>${node.comment}</doc>"
            node.type.getAnonymousType().attributes.each { a ->
                anon.println "${in2}<attribute ${attrs(a)}>"
                anon.println "${in3}<doc>${a.comment}</doc>"
                if (a.type.isAnonymous()) {
                    printAnonymousType( out, a, baseIndent+4, true )
                }
                anon.println "${in2}</attribute>"
            }
            anon.println "${in1}</structure>"
            return (recursive) ? "intermediate" : sw.toString()
        }
        ""
    }
}

