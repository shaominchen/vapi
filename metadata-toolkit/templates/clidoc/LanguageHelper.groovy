/*
 * Copyright 2014 VMware, Inc.  All rights reserved.
 */
package com.vmware.vapi.idl.transformer

import com.vmware.vapi.idl.model.*
import com.vmware.vapi.idl.model.doc.*
import com.vmware.vapi.idl.model.metadata.*
import com.vmware.vapi.idl.util.IndentWriter
import com.vmware.vapi.idl.transformer.HtmlCommentWriter
import com.vmware.vapi.idl.transformer.LanguageTermMapping

import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.text.WordUtils

/**
 * Defines the Metadata language helper.
 */
class ClidocLanguageHelper extends LanguageHelper {

    static final String NAME = "clidoc"
    static final String RESOURCE = "Resource"
    static final String UNION_CASE = "UnionCase"
    static final String MAP_ENTRY = "map-entry"

    private static final int SPACES_IN_TAB = 4
    private static final int CHARS_PER_LINE = 80
    private StringWriter commentBuffer = new StringWriter()
    private IndentWriter comment = new IndentWriter(commentBuffer)

    static String toLowerCaseSansUnderscores(String name) {
        return name.replaceAll(/_/,'')
    }

    static String toLowerCaseSansUnderscores(IdlIdentifier id) {
        return toLowerCaseSansUnderscores(id.asLowerCaseWithUnderscores)
    }

    static ClidocLanguageHelper getInstance(LanguageProperties properties, TemplateNamingContext naming) {
        return new ClidocLanguageHelper(properties, naming)
    }

    ClidocLanguageHelper(LanguageProperties properties, TemplateNamingContext naming) {
        super(NAME, properties, naming)
        suffixes = [ "json" ]
    }

    @Override
    CommentWriter createCommentWriter() {
        return new CliCommentWriter(
                createLinkConverter(),
                createNameConverter()).injectTermMapping(termMapping)
    }

    LinkConverter createLinkConverter() {
        return new LinkConverter() {
            @Override
            String toLink(ResolvedLinkToPackage link) {
                StringBuilder result = new StringBuilder()
                result << toName(link)
                return result.toString()
            }

            @Override
            String toName(ResolvedLinkToPackage link) {
                List<String> pkgName = link.pkg.name.asCanonical.split('\\.')
                return pkgName.last()
            }

            @Override
            String toLink(ResolvedLinkToType link) {
                return toName(link)
            }

            @Override
            String toName(ResolvedLinkToType link) {
                return formatTypeLinkName(link.type)
            }

            @Override
            String toLink(ResolvedLinkToMember link) {
                StringBuilder result = new StringBuilder()

                switch (link.member) {
                    case IdlConstant:
                    case IdlAttribute:
                        result << toName(link)
                        break
                    case IdlOperation:
                        result << toName(link)
                        break
                }
                return result.toString()
            }

            @Override
            String toName(ResolvedLinkToMember link) {
                StringBuilder result = new StringBuilder()
                String typeName = formatTypeLinkName(link.type)

                switch (link.member) {
                    case IdlConstant:
                        result << link.member.name.asDeclared
                        break
                    case IdlOperation:
                    case IdlAttribute:
                        result << typeName << '.' << link.member.name.asLowerCaseWithHyphens
                        break
                }
                return result.toString()
            }
        }
    }

    private NameConverter createNameConverter() {
        return new NameConverter() {
            @Override
            String toName(ResolvedLinkToPackage link) {
                List<String> pkgName = link.pkg.name.asCanonical.split('\\.')
                return pkgName.last()
            }

            @Override
            String toBasename(ResolvedLinkToPackage link) {
                return toName(link)
            }

            @Override
            String toFullname(ResolvedLinkToPackage link) {
                return toName(link)
            }

            @Override
            String toName(ResolvedLinkToType link) {
                //TODO: use current context to decide:
                // if element is in scope, then use basename
                // else use fullname
                return formatTypeLinkName(link.type)
            }

            @Override
            String toBasename(ResolvedLinkToType link) {
                return toName(link)
            }

            @Override
            String toFullname(ResolvedLinkToType link) {
                return toName(link)
            }

            @Override
            String toName(ResolvedLinkToMember link) {
                //TODO: use current context to decide:
                // if element is in scope, then use basename
                // else use fullname
                return toBasename(link)
            }

            @Override
            String toBasename(ResolvedLinkToMember link) {
                return link.member.name
            }

            @Override
            String toFullname(ResolvedLinkToMember link) {
                return toName(link)
            }
        }
    }

    private void printDocModel(DocModel docModel, int level, boolean newLine, boolean indentFirst=false) {
        List<String> textLines = commentWriter.makeLines(docModel)
        boolean firstLine = true
        textLines.eachWithIndex { line, i ->
            if (i == 0) {
                printLine(line, level, firstLine, newLine, indentFirst)
            } else {
                printLine(line, level, firstLine, newLine)
            }
            firstLine = false
        }
    }

    /**
     * Reset the comment buffer
     */
    private void resetCommentBuffer() {
        commentBuffer.buffer.setLength(0)
    }

    /**
     * Print a line in the comment buffer.
     *
     * @param line String to be printed.
     * @param level Indentation level.
     * @param firstLine If true, then this line is the first line of the text paragraph.
     * @param newLine If true, print newline, otherwise replace by space.
     * @param indentFirst If true, indent the first line, otherwise do not indent. The subsequent
     *                    lines are always indented.
     */
    private void printLine(String line, int level, boolean firstLine, boolean newLine, boolean indentFirst=false) {
        // Word wrap the current line based on maximum characters allowed per
        // line and the current indent level
        List<String> wrappedLines = WordUtils.wrap(
            line, CHARS_PER_LINE - level * SPACES_IN_TAB).split('\n')
        wrappedLines.eachWithIndex { it, i ->
            if (firstLine && !indentFirst) {
                if (newLine) {
                    comment.println(it)
                } else {
                    comment.print(it.replaceAll("\r", ""))
                    // Don't write newlines in CLI metadata JSON file.
                    comment.print(" ")
                }
            } else {
                if (newLine) {
                    comment.indent(level)
                }
                if (newLine) {
                    comment.println(it)
                } else {
                    comment.print(it.replaceAll("\r", ""))
                    // Don't write newlines in CLI metadata JSON file.
                    comment.print(" ")
                }
            }
            firstLine = false
        }
    }

    private String formatTypeLinkName(AbstractNamedNode node) {
        def typeName = getTypeReferenceName(node)
        return "${typeName}"
    }

    String getTypeReferenceName(AbstractNamedNode typeDeclarationNode) {
        List<String> tokens = new ArrayList<String>()
        AbstractNamedNode node = typeDeclarationNode
        AbstractNamedNode lastNode = null
        while (node) {
            tokens.add(node.name)
            lastNode = node
            node = node.declaringNode
        }
        return tokens.reverse().join('.')
    }

    String getCliNamespace(AbstractNamedNode node) {
        String result = ''

        if (node instanceof AbstractNamespacedNode) {
             AbstractNamespacedNode namespacedNode = (AbstractNamespacedNode) node
            if (namespacedNode.namespace) {
                result = naming.getPackageName(namespacedNode)
            }
        }
        return result
    }

    String getPackageName(AbstractNamedNode typeDeclarationNode) {
        AbstractNamedNode node = typeDeclarationNode.declaringNode
        AbstractNamedNode lastNode = typeDeclarationNode
        while (node) {
            lastNode = node
            node = node.declaringNode
        }
        return getCliNamespace(lastNode)
    }

    /**
     * Helper methods for identifier metadata template
     */
    int getNumResourceFields(IdlOperation op) {
        def result = 0
        op.parameters.each { param ->
            if (param.isResource()) {
                result++
            }
        }
        return result
    }

    int getNumResourceFields(IdlStructure st) {
        def result = 0
        st.impliedAttributes.each { attr ->
            if (attr.isResource()) {
                result++
            }
        }
        return result
    }

    /**
     * Helper methods for enum metadata template
     */
    Boolean hasEnumType(IdlType type) {
        if (type.isReference() && type.isEnumeration()) {
            return true
        }
        if (type.isGeneric()) {
            if (type.arguments[0].isReference() && type.arguments[0].isEnumeration()) {
                return true
            }
            if (type.isMap() && type.arguments[1].isReference() && type.arguments[1].isEnumeration()) {
                return true
            }
            return false
        }
        if (type.isReference() && type.isStructure()) {
            return hasEnumFields(type.declaration)
        }
        return false
    }

    Boolean hasEnumFields(IdlStructure structure) {
        for (IdlAttribute a : structure.impliedAttributes) {
            if (hasEnumType(a.type)) {
                return true
            }
        }
        return false
    }

    List<IdlAttribute> getEnumFields(IdlStructure structure) {
        List<IdlAttribute> attributes = new ArrayList<IdlAttribute>()
        for (IdlAttribute a : structure.impliedAttributes) {
            if (hasEnumType(a.type)) {
                attributes.add(a)
            }
        }
        return attributes
    }

    Boolean hasEnumParams(IdlOperation operation) {
        for (IdlParameter p : operation.parameters) {
            if (hasEnumType(p.type)) {
                return true
            }
        }
        return false
    }

    List<IdlParameter> getEnumParams(IdlOperation operation) {
        List<IdlParameter> parameters = new ArrayList<IdlParameter>();
        for (IdlParameter p : operation.parameters) {
            if (hasEnumType(p.type)) {
                parameters.add(p)
            }
        }
        return parameters
    }

    Boolean hasEnumReturnType(IdlOperation operation) {
        return hasEnumType(operation.result.type)
    }

    /**
     * Helper methods for union metadata template
     */
    Map<String, Set<IdlAttribute> > getUnionCaseMap(IdlStructure s, IdlAttribute d) {
        Map<String, Set<IdlAttribute> >result = new HashMap<String, Set<IdlAttribute> >()
        for (u_case in s.getUnionCases(d)) {
            for (IdlConstant case_val in u_case.getUnionCaseValues()) {
                String key = case_val.name.asDeclared
                Set<IdlAttribute> attr_list = result.get(key, new HashSet<IdlAttribute>())
                attr_list.add(u_case)
            }
        }
        return result
    }

    Map<String, Set<IdlParameter> > getUnionCaseMap(IdlOperation o, IdlParameter d) {
        Map<String, Set<IdlParameter> >result = new HashMap<String, Set<IdlParameter> >()
        for (u_case in o.getUnionCases(d)) {
            for (IdlConstant case_val in u_case.getUnionCaseValues()) {
                String key = case_val.name.asDeclared
                Set<IdlParameter> param_list = result.get(key, new HashSet<IdlParameter>())
                param_list.add(u_case)
            }
        }
        return result
    }

    String getUnionTypeCategory(IdlType type) {
        if (type.isReference() && type.isStructure()) {
                return "structure"
        } else if (type.isGeneric() && type.isList()) {
            return "list"
        } else if (type.isGeneric() && type.isOptional()) {
            return "optional"
        }
        return ""
    }

    // Helper methods for metamodel service

    String getTypeString(AbstractTypedNode element) {
        return getTypeString(element.impliedType)
    }

    String getTypeString(IdlType type) {
        String instance = ""
        if (type.isPrimitive() || type.isVoid()) {
            instance = /"category": "primitive", "primitive_type": "${type.asIdentifier.asCanonical}"/
        } else if (type.isGeneric()){
            if (type.isMap()) {
                String arg_key = getTypeString(type.arguments[0])
                String arg_value = getTypeString(type.arguments[1])
                instance = /"category": "generic", "generic_type": "Map" ,"map_key_type": ${arg_key}, "map_value_type": ${arg_value}/
            } else {
                String arg = getTypeString(type.arguments[0])
                String genericType = ""
                if (type.isOptional()) {
                    genericType = "Optional"
                } else if (type.isList()) {
                    genericType = "List"
                } else if (type.isSet()) {
                    genericType = "Set"
                }
                instance = /"category": "generic", "generic_type": "${genericType}" ,"element_type": ${arg}/
            }
        } else if (type.isReference()) {
            IdlReferenceType referenceType = (IdlReferenceType) type;
            String refType = ""
            if (referenceType.isStructure()) {
                refType = "Structure"
            } else {
                refType = "Enumeration"
            }
            String referenceName = type.declaration.getQualifiedName('asCanonical')
            instance = /"category": "user_defined", "user_defined_type": "${refType}" ,"user_defined_type_name": "${referenceName}"/
        }
        instance = "{" + instance + "}"
        return instance
    }

    String getPrimitiveConstantTypeString(IdlConstantRef constantRef) {
        IdlConstant constant = constantRef.declaration
        return getPrimitiveConstantTypeString(constant.value)
    }

    String getPrimitiveConstantTypeString(String value) {
        return "String"
    }

    String getPrimitiveConstantTypeString(Long value) {
        return "Long"
    }

    String getPrimitiveConstantTypeString(Double value) {
        return "Double"
    }

    String getPrimitiveConstantTypeString(Boolean value) {
        return "Boolean"
    }

    String getConstantTypeString(IdlConstant constant) {
        String instance = ""
        if (constant.type.isPrimitive()) {
            String primitiveType = getPrimitiveConstantTypeString(constant.value)
            instance = /"category": "Primitive", "type": "${primitiveType}"/
        } else if (constant.type.isList()) {
            String primitiveType = getPrimitiveConstantTypeString(constant.value.getAt(0))
            instance = /"category": "List", "type": "${primitiveType}"/
        }
        return "{" + instance + "}"
    }

    List<IdlAttribute> getStructureFields(IdlStructure structure) {
        List<IdlAttribute> fields = new ArrayList<IdlAttribute>()
        if( structure.impliedAttributes != null) {
            fields.addAll(structure.impliedAttributes)
        }
        if (structure.extendsType != null) {
            List<IdlAttribute> baseFields = getStructureFields(structure.extendsType.declaration)
            if (baseFields.size > 0) {
                fields.addAll(baseFields)
            }
        }
        return fields;
    }

    List<String> getMetadataFields(AbstractNamedNode node) {
        def results = []
        node.metadata.properties.each { k, v ->
            // exclude resource id metadata from the container nodes
            if (k != RESOURCE) {
                results.add(k)
            }
        }
        return results
    }

    List<String> getMetadataFields(AbstractTypedNode node) {
        def results = []
        boolean local = false
        node.metadata.properties.each { k, v ->
            // include all metadata from the leaf nodes, but track resource ids
            if (k == RESOURCE) {
                local = true
            }
            results.add(k)
        }
        // pull resource id metadata from the container nodes, when not present locally
        if (!local && node instanceof ResourceMetadata && node.isResource()) {
            results.add(RESOURCE)
        }
        return results
    }

    String getPrimitiveConstantValue(IdlConstant constant) {
        if (constant.type == IdlPrimitiveType.STRING) {
            return '"' + constant.value + '"'
        } else {
            return constant.value
        }
    }

    String getPrimitiveConstantValue(IdlConstantRef constantRef) {
        IdlConstant constant = constantRef.declaration
        return getPrimitiveConstantValue(constant)
    }

    String getPrimitiveConstantValue(String value) {
        return "\"${value}\""
    }

    String getPrimitiveConstantValue(Long value) {
        return value
    }

    String getPrimitiveConstantValue(Double value) {
        return value
    }

    String getPrimitiveConstantValue(Boolean value) {
        return value
    }

    String getConstantValue(IdlConstant constant) {
        if (constant.type.isPrimitive()) {
            return getPrimitiveConstantValue(constant)
        } else if (constant.type.isList()) {
            def values = []
            constant.value.each { value ->
                values.add(getPrimitiveConstantValue(value))
            }
            return "[" + values.join(", ") + "]"
        }
    }

    String getConstantValue(IdlConstantRef constantRef) {
        Object object = constantRef.declaration
        if (object instanceof IdlConstant) {
            return getConstantValue(object)
        } else if (object instanceof IdlStructure) {
            return '"' + object.getQualifiedName('asCanonical') + '"'
        } else {
            return constantRef.deref();
        }
    }

    String processElementValue(Object value) {
        String result = null
        if (value instanceof IdlConstantRef) {
            result = getConstantValue(value)
        } else if (value instanceof IdlReferenceType) {
            result = '"' + value.declaration.getQualifiedName('asCanonical') + '"'
        } else {
            result = '"' + value.toString() + '"'
        }
        return result
    }

    String processResourceValue(AbstractNamedNode node) {
        if (node.hasResourceTypeHolder()) {
            List<String> resourceValues = node.getResourceTypes();
            def values = []
            def result = []
            resourceValues.each { value ->
                values.add('"' + value + '"')
            }
            result.add('"value": [' + values.join(', ') + ']')
            result.add('"typeHolder": "' + node.getResourceTypeHolderNode().name.asCanonical + '"')
            return '{' + result.join(', ') + '}'
        } else {
            return '{"value": "' + node.getResourceId() + '"}'
        }
    }

    String processUnionCase(AbstractNamedNode node) {
        def values = []
        node.getUnionCaseValues().each { value ->
            values.add('"' + value.name + '"')
        }
        return '{"tag": "' + node.getUnionCaseTag().name.asCanonical + '", "value": [' + values.join(', ') + ']}'
    }

    String getMetadataField(AbstractNamedNode node, String key, Object value) {
        def value_string = null
        if (key == RESOURCE) {
            value_string = processResourceValue(node)
        } else if (key == UNION_CASE) {
            value_string = processUnionCase(node)
        } else if (value instanceof Map) {
            def elements = []
            value.each { k, v ->
                def elementValue = processElementValue(v)
                elements.add(/"${k}": ${elementValue}/)
            }
            value_string = '{' + elements.join(', ') + '}'
        } else if (value instanceof Collection) {
            def elements = value.collect { processElementValue(it) }
            value_string = '{"value": [' + elements.join(', ') + ']}'
        } else {
            value_string = '{"value":' + processElementValue(value) + '}'
        }
        String result = /"${key}": ${value_string}/
        return result
    }

    String getMetadataField(AbstractNamedNode node, String key) {
        return getMetadataField(node, key, node.metadata[key])
    }

    String escapeJson(IdlDocumentation doc) {
        // To escape \ with \\, we need to escape the escape characters as well
        // So, \ -> \\\\ and \\ -> \\\\\\\\
        return escapeJson(doc.toString())
    }

    String escapeJson(String doc) {
        // To escape \ with \\, we need to escape the escape characters as well
        // So, \ -> \\\\ and \\ -> \\\\\\\\
        String text = doc
        text = text.replaceAll('\\\\','\\\\\\\\')
        text = text.replaceAll('"','\\\\\"')
        return text.trim()
    }

    String getDocumentation(AbstractNamedNode node) {
        return escapeJson(node.doc)
    }

    // CLI metadata helpers

    String getPackageName(IdlService service) {
        return toLowerCaseSansUnderscores(service.getQualifiedName('asLowerCaseWithUnderscores'))
    }

    String getNamespace(IdlPackage pkg) {
        if (pkg.name.toString().contains(".")) {
            List<String> pkgName = pkg.name.toString().split('\\.')
            pkgName.pop()
            String nsName = pkgName.join('.')
            return toLowerCaseSansUnderscores(nsName)
        } else {
            return ""
        }
    }

    String getNamespace(IdlService service) {
        return toLowerCaseSansUnderscores(service.namespace.toString())
    }

    String getInterfaceName(IdlPackage pkg) {
        if (pkg.name.toString().contains(".")) {
            List<String> pkgName = pkg.name.toString().split('\\.')
            return toLowerCaseSansUnderscores(pkgName.last())
        } else {
            return toLowerCaseSansUnderscores(pkg.name)
        }
    }

    String getInterfaceName(IdlService service) {
        return toLowerCaseSansUnderscores(service.name)
    }

    String getCLITypeName(IdlType type) {
        return toCanonicalForm(type)
    }

    String getDocComment(IdlDocumentation doc) {
        resetCommentBuffer()
        printDocModel(doc.getDescriptionModel(), 2, true)
        if (doc.metadataModel) {
            printDocModel(doc.metadataModel, 2, true)
        }
        if (doc.optionalReasonModel) {
            printDocModel(doc.optionalReasonModel, 2, true)
        }
        return commentBuffer.toString().trim()
    }

    String getComment(IdlDocumentation doc) {
        resetCommentBuffer()
        printDocModel(doc.getDescriptionModel(), 2, false)

        // Only write first line of documentation in CLI metadata file.
        List<String> docArr = commentBuffer.toString().split('\\. ')
        return escapeJson(docArr[0])
    }

    String getCommandName(IdlIdentifier id) {
        return toLowerCaseSansUnderscores(id)
    }

    class CliOption {
        final String name
        final IdlParameter parameter

        CliOption(String name, IdlParameter parameter) {
            this.name = name
            this.parameter = parameter
        }
    }

    List handleCliParamDuplicates(List<IdlParameter> params) {
        Set<String> cliOptionNames = new HashSet<String>()
        List<CliOption> newOptions = new ArrayList<CliOption>()
        params.each { param ->
            def optionNameSplit = param.name.asLowerCaseWithHyphens.split('\\.')
            def optionName = optionNameSplit.last()
            if (optionNameSplit.size() > 1) {
                optionName = optionNameSplit[1..optionNameSplit.size()-1].join('-')
            }

            def index = 2
            def tempName = optionName
            while (cliOptionNames.contains(tempName)) {
                tempName = "${optionName}-${index}"
                index += 1
            }
            optionName = tempName

            CliOption option = new CliOption(optionName, param)
            cliOptionNames.add(optionName)
            newOptions.add(option)
        }
        return newOptions
    }

    List getInputParams(IdlOperation operation) {
        List<IdlParameter> params = new ArrayList<IdlParameter>()

        for(IdlParameter p : operation.getParameters()) {
            boolean optionalOverride = isOptional(p.impliedType)
            IdlType type = getCliFormatType(p.impliedType)
            List<String> structNames = new ArrayList<String>()
            if (type.isReference() && type.isStructure()) {
                structNames.add(type.declaration.getQualifiedName('asCanonical'))
                getNestedInputParams(operation, params, structNames, p.name.asCanonical, type.declaration, optionalOverride)
                structNames.remove(type.declaration.getQualifiedName('asCanonical'))
            } else if (p.hasFieldsOf()) {
                for (IdlStructure s : p.getFieldsOf()) {
                    structNames.add(s.getQualifiedName('asCanonical'))
                    getNestedInputParams(operation, params, structNames, p.name.asCanonical, s, optionalOverride)
                    structNames.remove(s.getQualifiedName('asCanonical'))
                }
            }
            else {
                params.add(p)
            }
        }
        return handleCliParamDuplicates(params)
    }

    void getNestedInputParams(IdlOperation op, List<IdlParameter> params, List<String> structNames,
            String name, IdlStructure s, boolean optionalOverride) {
        for (IdlAttribute att : s.impliedAttributes) {
            if (((getCommandName(op.name) == "create") && att.getCreate() == CrudUsage.UNUSED)
                || ((getCommandName(op.name) == "read") && att.getRead() == CrudUsage.UNUSED)
                || ((getCommandName(op.name) == "update") && att.getUpdate() == CrudUsage.UNUSED))
                continue
            IdlType attType = getCliFormatType(att.impliedType)
            String paramName = name + '.' + att.name.asCanonical
            if (attType.isReference() && attType.isStructure()) {
                if (structNames.contains(attType.declaration.getQualifiedName('asCanonical'))) {
                    continue
                }
                structNames.add(attType.declaration.getQualifiedName('asCanonical'))
                getNestedInputParams(op, params, structNames, paramName, attType.declaration, isOptional(att.impliedType))
                structNames.remove(attType.declaration.getQualifiedName('asCanonical'))
            }
            else {
                IdlParameter newParam = new IdlParameter(paramName)
                if (optionalOverride == true && !isList(att.impliedType)) {
                    newParam.type = IdlGenericType.newOptional()
                    newParam.type.arguments << att.impliedType
                } else {
                    newParam.type = att.impliedType
                }
                if (att.metadata.CanonicalName) {
                    newParam.name.canonicalNameOverride = paramName
                }
                newParam.doc.description << getComment(att.doc)
                newParam.doc.docModel = att.doc.docModel
                newParam.doc.metadataModel = att.doc.metadataModel
                newParam.doc.optionalReasonModel = att.doc.optionalReasonModel
                params.add(newParam)
            }
        }
    }


    Set getNestedStructs(IdlResult result) {
        Map<String, IdlStructure> structsMap = new HashMap<String, IdlStructure>()
        IdlType type = getCliFormatType(result.type)

        if (type.isReference() && type.isStructure()) {
            getNestedStructList(type.declaration, structsMap)
        }

        if (type.isGeneric() && type.isMap()) {
            getMapStructs(type, structsMap)
        }

        return structsMap.values()
    }

    void getMapStructs(IdlType type, Map<String, IdlStructure> structsMap) {
        IdlType key = getCliFormatType(type.arguments[0])
        IdlType value = getCliFormatType(type.arguments[1])
        if (!structsMap.containsKey(MAP_ENTRY)) {
            IdlStructure mapStruct = new IdlStructure(MAP_ENTRY, '')
            IdlAttribute keyAttr = new IdlAttribute('key')
            keyAttr.type = key
            mapStruct.addAttribute(keyAttr)
            IdlAttribute valAttr = new IdlAttribute('value')
            valAttr.type = value
            mapStruct.addAttribute(valAttr)
            structsMap.put(mapStruct.getQualifiedName('asCanonical'), mapStruct)
        }

        if (value.isReference() && value.isStructure()) {
            if (!structsMap.containsKey(value.declaration.getQualifiedName('asCanonical'))) {
                getNestedStructList(value.declaration, structsMap)
            }
        }
        if (value.isGeneric() && value.isMap()){
            getMapStructs(value, structsMap)
        }
    }

    void getNestedStructList(IdlStructure s, Map<String, IdlStructure> structsMap) {
        structsMap.put(s.getQualifiedName('asCanonical'), s)
        for (IdlAttribute att : s.impliedAttributes) {
            IdlType type = getCliFormatType(att.impliedType)
            if (type.isReference() && type.isStructure()) {
                if (!structsMap.containsKey(type.declaration.getQualifiedName('asCanonical'))) {
                    getNestedStructList(type.declaration, structsMap)
                }
            }
            if (type.isGeneric() && type.isMap()) {
                getMapStructs(type, structsMap)
            }
        }
    }

    IdlType getCliFormatType(IdlType type) {
        IdlType formatType = type
        while (formatType.isGeneric() && !formatType.isMap()) {
            formatType = formatType.getLatestType()
        }
        return formatType
    }

    boolean isList(IdlType type) {
        return type.isGeneric() && (type.isList() || type.isSet())
    }

    long getCliNodeCount(IdlProduct product) {
        long count = product.services.size()

        for (IdlService s : product.services) {
            count += s.operations.size()
        }

        return count
    }

    boolean isOptional(IdlType type) {
        return type.isGeneric() && type.isOptional()
    }

    boolean isOperation(AbstractNamedNode node) {
        return node instanceof IdlOperation
    }

    String formatList(List<String> elements) {
        List<String> result = elements.collect { '"' + it + '"' }
        return '[' + result.join(', ') + ']'
    }

    String getAuthnSchemes(List<AuthenticationScheme> schemes) {
        return formatList(schemes.collect {it.id})
    }

    List<String> getPrivilegeMapping(String baseString, AuthzParameterSettings paramSettings) {
        List<String> result = new ArrayList<String>()
        paramSettings.fields.each { field ->
            List<String> propertyPathTokens = field.fields.collect { it.name.asCanonical }
            String currentString = baseString + '.' + propertyPathTokens.join('.')
            result.add('"' + currentString + '" : ' + formatList(field.privileges))
        }
        return result
    }

    List<String> getPrivilegeMapping(String baseString, AuthzOperationSettings opSettings) {
        List<String> result = new ArrayList<String>()
        opSettings.parameters.each { param ->
            String currentString = baseString + '.' + param.parameter.name.asCanonical
            result.addAll(getPrivilegeMapping(currentString, param))
            if (param.privileges != null) {
                result.add('"' + currentString + '" : ' + formatList(param.privileges))
            }
        }
        return result
    }

    List<String> getPrivilegeMapping(AuthzServiceSettings svcSettings) {
        List<String> result = new ArrayList<String>()
        svcSettings.operations.each { op ->
            String currentString = op.operation.name.asCanonical
            result.addAll(getPrivilegeMapping(currentString, op))
            if (op.privileges != null) {
                result.add('"' + currentString + '" : ' + formatList(op.privileges))
            }
        }
        return result
    }

    String serviceHtml(IdlService service) {
        return service.namespace.toString() + "/" + service.name.asCapitalizedWords + ".html"
    }

    String getFullCommandName(IdlService service, IdlIdentifier name) {
        String command = toLowerCaseSansUnderscores(service.namespace.toString()).replaceAll("\\.", " ") + toLowerCaseSansUnderscores(" " + service.name.toString().toLowerCase() + " " + name.toString().toLowerCase())
        return command
    }

    String isRequired(IdlType type) {
        return (isOptional(type) || (isList(type) && isOptional(type.arguments[0]))) ? "No" : "Yes"
    }

    String getParameterHelp(String optionName, IdlType idlType, String enumValue) {
        String type = idlType.toString();
        String help = ""
        help += "--" + optionName

        if (type == "ID") {
            help += " testId"
        } else if (type == "String") {
            help += " testString"
        } else if (type == "Long") {
            help += " 15"
        } else if (type == "Double") {
            help += " 10.5"
        } else if (type == "URI") {
            help += " http://testURL"
        } else if (type == "Boolean") {
            help += " False"
        } else if (type == "Enum") {
            help += " "
            help += enumValue
        } else if (type == "Secret") {
        } else {
            help += " test"
        }

        help += " "

        return help
    }

    /**
    * A {@link CommentWriter} implementation that renders DocModel into
    * CLI documentation format.
    */
    class CliCommentWriter extends HtmlCommentWriter {
        private final LinkConverter linkConverter
        private final NameConverter nameConverter
        private LanguageTermMapping termMapping
        private Deque<List<String>> tableRows
        private List<Integer> tableColumnWidths
        private boolean tableHasHeader
        private String indent = " " * 4
        private String markup = "*`"

        public CliCommentWriter(LinkConverter linkConverter, NameConverter nameConverter) {
            super(linkConverter, nameConverter)
            this.linkConverter = linkConverter
            this.nameConverter = nameConverter
        }

        CliCommentWriter injectTermMapping(LanguageTermMapping termMapping) {
            this.termMapping = termMapping
            return this
        }

        private String escape(String str, String chars) {
            chars.each { str = str.replace(it, "\\$it") }
            return str
        }

        private String unescapeBraces(String str) {
            return str.replace("&#123;", "{").replace("&#125;", "}")
        }

        @Override
        public <T> String visitChildren(ContainerElement<T> element) {
            return element.childElements.collect { visit(it) }.join('')
        }

        /* Top-level doc model */
        @Override
        String visit(DocModel element) {
            return visitChildren(element)
        }

        /*
        * HTML:
        *    xxx
        *
        * CLI:
        *    xxx
        */
        @Override
        String visit(Text element) {
            return escape(element.text, markup)
        }

        /*
        * HTML:
        *    <br>
        *
        * CLI:
        *    \n
        */
        @Override
        String visit(BreakLine element) {
            return ""
        }

        /*
        * HTML:
        *    <u>xxx</u>
        *
        * CLI:
        *    xxx
        *
        * Notes:
        *    1. CLI does not support underline markup; using italic instead.
        */
        @Override
        String visit(Underscore element) {
            return "${visitChildren(element).trim()}"
        }

        /*
        * HTML:
        *    <i>xxx</i>
        *
        * CLI:
        *    xxx
        *
        */
        @Override
        String visit(Italic element) {
            return "${visitChildren(element).trim()}"
        }

        /*
        * HTML:
        *    <b>xxx</b>
        *
        * CLI:
        *    *xxx*
        *
        */
        @Override
        String visit(Bold element) {
            return "*${visitChildren(element).trim()}*"
        }

        /*
        * HTML:
        *    <p>
        *
        * CLI:
        *    \n
        *
        */
        @Override
        String visit(Paragraph element) {
            return "\n\n" + visitChildren(element)
        }

        /*
        * HTML:
        *    {@link xxx}
        *
        * CLI:
        *    xxx
        *
        */
        @Override
        String visit(AtLink element) {
            def linkText = element.resolvedLink ? linkConverter.toLink(element.resolvedLink) : element.resolvedLink
            return linkText
        }

        /*
        * HTML:
        *    {@code xxx}
        *
        * CLI:
        *    xxx
        *
        */
        @Override
        String visit(AtCode element) {
            return "${escape(unescapeBraces(element.text.trim()), markup)}"
        }

        /*
        * HTML:
        *    {@literal xxx}
        *
        * CLI:
        *    xxx
        */
        @Override
        String visit(AtLiteral element) {
            return escape(unescapeBraces(element.text), markup)
        }

        /*
        * HTML:
        *    {@param.ref xxx}
        *
        * CLI:
        *    xxx
        *
        */
        @Override
        String visit(AtParamRef element) {
            def name = element.resolvedParameter?.name?.asLowerCaseWithHyphens ?: element.text
            return name
        }

        @Override
        String visit(AtName element) {
            def name = element.reference

            if (element.resolvedLink) {
                switch (element.tag) {
                case 'name':
                    name = nameConverter.toName(element.resolvedLink)
                    break
                case 'basename':
                    name = nameConverter.toBasename(element.resolvedLink)
                    break
                case 'fullname':
                    name = nameConverter.toFullname(element.resolvedLink)
                    break
                }
            }
            return name
        }

        @Override
        String visit(AtEnumValues element) {
            String result

            if (element.resolvedLink?.isToType() && element.resolvedLink.type instanceof IdlEnumeration) {
                IdlEnumeration e = element.resolvedLink.type

                // build a csv of the enum's constant values
                result = e.constants.collect { it.name.asDeclared }.join(', ')
            } else {
                // put the tag back into the Javadoc as a means to report a problem
                result = "{@enum.values ${element.reference}}"
            }
            return result
        }

        @Override
        String visit(AtKeyword element) {
            String term = termMapping?.translate(element.text) ?: element.text
            return "${term}"
        }
    }
}
