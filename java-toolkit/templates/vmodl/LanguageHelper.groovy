/*
 * Copyright 2012-2014 VMware, Inc.  All rights reserved.
 */
package com.vmware.vapi.idl.transformer

import com.vmware.vapi.idl.model.*
import com.vmware.vapi.idl.model.metadata.*


/**
 * Defines the VMODL language helper.
 */
class VmodlLanguageHelper extends LanguageHelper {

    static final String NAME = "vmodl"

    static final String VMODL_LANG = 'vmodl.lang.*'
    static final String VMODL_CRUD = 'vmodl.crud.*'
    static final String VMODL_REST = 'vmodl.rest.*'

    //fyi: required factory method to construct an instance
    static VmodlLanguageHelper getInstance(LanguageProperties properties, TemplateNamingContext naming) {
        return new VmodlLanguageHelper(properties, naming)
    }

    private IdlPackage currentPackage = null

    /** Constructs a new instance. */
    VmodlLanguageHelper(LanguageProperties properties, TemplateNamingContext naming) {
        super(NAME, properties, naming)

        suffixes = ['vmodl', 'java']
    }

    String headerComment(AbstractNamedNode thing) {
        String result = """\
/*
 * Copyright 2012-2015 VMware, Inc.  All rights reserved.
 */
"""
        return result
    }

    //TODO: this needs to be broken out into partial templates
    String javadoc(AbstractNamedNode thing, int level=0) {
        final List<String> lines = []
        final String indent = "    " * level
        final String instar = "${indent} *"
        def buildTag = { tag, name, text ->
            name = name ? " ${name}" : ''
            if (text) {
                lines << "${instar} ${tag}${name} ${text[0]}"
                if (text.size() > 1) {
                    def fill = " " * tag.length()

                    text[1..-1].each { line ->
                        lines << "${instar} ${fill} ${line}"
                    }
                }
            }
        }

        if (thing.doc) {
            def oneliner = thing.doc.description.size() == 1 && thing.doc.description[0].length() < 60
            def reason = thing.doc.optionalReason.size() > 0
            def links = thing.doc.links.size() > 0
            def isOperation = thing instanceof IdlOperation

            // fyi - the indent will be implicit by where the "macro" is located in the template
            if (oneliner && !reason && !links && !isOperation) {
                lines << "/** " + thing.doc.description[0] + " */"
            } else {
                lines << "/**"
                thing.doc.description.each { text ->
                    lines << "${instar} ${text}"
                }
                // fyi - generally the one type of node that composes javadoc from contained elements
                if (thing instanceof IdlOperation) {
                    lines << "${instar}"
                    thing.parameters.each { p ->
                        buildTag('@param', p.name, p.doc.description)
                        buildTag('@param.optional', p.name, p.doc.optionalReason)
                    }
                    buildTag('@return', null, thing.result.doc.description)
                    buildTag('@return.optional', null, thing.result.doc.optionalReason)
                    thing.errors.each { e ->
                        e.doc.descriptions.each { desc ->
                            buildTag('@throws', e.name, desc)
                        }
                    }
                } else if (thing instanceof IdlAttribute) {
                    if (thing.doc.optionalReason) {
                        lines << "${instar}"
                        buildTag('@field.optional', null, thing.doc.optionalReason)
                    }
                }
                thing.doc.links.each { link ->
                    lines << "${instar} @see ${link}"
                }
                lines << "${instar}/"
            }
        } else {
            lines << "/* TODO: add doc here! */"
        }
        return lines.join('\n')
    }

    String mapType(IdlType type) {
        String result

        if (type.isGeneric()) {
            String arg = mapType(type.arguments[0])

            if (type.isList()) {
                result = "List<${arg}>"
            } else if (type.isSet()) {
                result = "Set<${arg}>"
            } else if (type.isOptional()) {
                result = "Optional<${arg}>"
            } else if (type.isMap()) {
                String arg1 = mapType(type.arguments[1])

                result = "Map<${arg}, ${arg1}>"
            }
        } else if (type.isReference()) {
            //TODO: does IdlReference type have a "symbol" property?
            result = type.asIdentifier.asCapitalizedWords
        } else if (type.isPrimitive()) {
            switch (type.name) {
            case "string":
                result = type.name.capitalize()
                break
            default:
                result = type.name
            }
        } else if (type.isAnonymous()) {
            result = "Anonymous"
        } else if (type.isVoid()) {
            result = type.name
        }
        return result
    }

    Set<String> getImports(IdlPackage pkg) {
        def result = [] as TreeSet

        if (pkg.isComponent() || pkg.markedCopyright()) {
            result << VMODL_LANG
        }
        return result
    }

    Set<String> getImports(IdlService service) {
        def result = [] as TreeSet

        currentPackage = service.containingPackage
        addImportsForMetadata(service, result)
        service.operations.each { o ->
            addImportsForMetadata(o, result)
            addImportsForMetadata(o.result, result)
            addImport(service, o.result.type, result)
            o.parameters.each {
                addImportsForMetadata(it, result)
                addImport(service, it.type, result)
            }
            o.errors.each {
                addImport(service, it.type, result)
            }
        }
        service.structures.each { s ->
            result.addAll(getImports(s, true))
        }
        service.enumerations.each { e ->
            result.addAll(getImports(e, true))
        }
        return result
    }

    Set<String> getImports(IdlStructure structure, boolean nested=false) {
        def result = [] as TreeSet

        if (!nested) {
            currentPackage = structure.containingPackage
        }
        addImportsForMetadata(structure, result)
        structure.attributes.each {
            addImportsForMetadata(it, result)
            addImport(structure, it.type, result)
        }
        structure.enumerations.each { e ->
            result.addAll(getImports(e, true))
        }
        return result
    }

    Set<String> getImports(IdlEnumeration enumeration, boolean nested=false) {
        def result = [] as TreeSet

        if (!nested) {
            currentPackage = enumeration.containingPackage
        }
        addImportsForMetadata(enumeration, result)
        return result
    }

    private void addImportsForMetadata(IdlService node, Set result) {
        mineCanonicalName(node, result)
        mineResource(node, result)
    }

    private void addImportsForMetadata(IdlOperation node, Set result) {
        mineCanonicalName(node, result)
        mineResource(node, result)
    }

    private void addImportsForMetadata(IdlParameter node, Set result) {
        mineCanonicalName(node, result)
        mineResource(node, result)
        mineHasFieldsOf(node, result)
        mineIsOneOf(node, result)
    }

    private void addImportsForMetadata(IdlResult node, Set result) {
        mineResource(node, result)
        mineHasFieldsOf(node, result)
    }

    private void addImportsForMetadata(IdlStructure node, Set result) {
        mineCanonicalName(node, result)
        mineResource(node, result)
        mineInclude(node, result)
        if (node.markedCrud()) {
            result << VMODL_CRUD
        }
        if (node.hasIncludedStructures() ||
            node.isOptionalByDefault() ||
            node.isIncludable()) {
            result << VMODL_LANG
        }
    }

    private void addImportsForMetadata(IdlAttribute node, Set result) {
        mineCanonicalName(node, result)
        mineResource(node, result)
        mineHasFieldsOf(node, result)
        mineIsOneOf(node, result)
        if (node.markedCreate() || node.markedRead() || node.markedUpdate()) {
            result << VMODL_CRUD
        }
        if (node.markedSerializationName()) {
            result << VMODL_REST
        }
    }

    private void addImportsForMetadata(IdlEnumeration node, Set result) {
        mineCanonicalName(node, result)
    }

    private void mineCanonicalName(AbstractNamedNode node, Set result) {
        if (node.metadata.CanonicalName) {
            result << VMODL_LANG // for the annotation itself
        }
    }

    private void mineResource(AbstractNamedNode node, Set result) {
        if (node.metadata.Resource) {
            def value = node.getResourceValue()
            def container = node instanceof AbstractNamespacedNode ? node :
                node.declaringNode instanceof IdlOperation ? node.declaringNode.declaringNode :
                node.declaringNode

            result << VMODL_LANG // for the annotation itself
            value.each {
                if (it instanceof IdlConstantRef) {
                    addImport(container, it, result)
                }
            }
        }
    }

    private void mineIsOneOf(AbstractTypedNode node, Set result) {
        if (node.metadata.IsOneOf) {
            def value = node.getIsOneOfValue()
            def container = node instanceof AbstractNamespacedNode ? node :
                node.declaringNode instanceof IdlOperation ? node.declaringNode.declaringNode :
                node.declaringNode

            result << VMODL_LANG // for the annotation itself
            value.each {
                if (it instanceof IdlConstantRef) {
                    addImport(container, it, result)
                }
            }
        }
    }

    private void mineHasFieldsOf(AbstractNamedNode node, Set result) {
        if (node.hasFieldsOf()) {
            def value = node.getFieldsOf()

            result << VMODL_LANG // for the annotation itself
            value.each { addImport(it, result) }
        }
    }

    private void mineInclude(IdlStructure node, Set result) {
        if (node.hasIncludedStructures()) {
            def value = node.getIncludedStructures()

            result << VMODL_LANG // for the annotation itself
            value.each { addImport(it, result) }
        }
    }

    private void addImport(IdlStructure structure, Set result) {
        if (currentPackage.qualifiedName != structure.namespace.qualifiedName) {
            result << structure.qualifiedName
        }
    }

    private void addImport(AbstractNamespacedNode container, IdlType type, Set result) {
        if (type.isReference()) {
            def diff1 = type.qualifiedName - (container.qualifiedName + '.')
            def diff2 = type.qualifiedName - (container.namespace.name + '.')

            // if the ref is not in scope, then we need to import the type
            if (type.name != diff1 && type.name != diff2) {
                result << type.qualifiedName
            }
        } else {
            // if type.isPrimitive() or isGeneric()
            result << VMODL_LANG
            if (type.isGeneric()) {
                addImport(container, type.arguments[0], result)
                if (type.isMap()) {
                    addImport(container, type.arguments[1], result)
                }
            }
        }
    }

    List<String> annotations(IdlPackage pkg) {
        return prune([
            atComponent(pkg),
            atCopyright(pkg),
        ])
    }

    List<String> annotations(IdlEnumeration enumeration) {
        return prune([
            atCanonicalName(enumeration),
        ])
    }

    List<String> annotations(IdlService service) {
        return prune([
            atCanonicalName(service),
            atResource(service),
        ])
    }

    List<String> annotations(IdlOperation operation) {
        return prune([
            atCanonicalName(operation),
            atResource(operation),
        ])
    }

    List<String> annotations(IdlResult result) {
        // fyi - result annotations are tricky, as they are associated
        // with the operation itself, but they logically belong to the
        // result node.
        return prune([
            atResource(result),
            atHasFieldsOf(result),
            atOptionalReason(result),
        ])
    }

    List<String> annotations(IdlParameter parameter) {
        return prune([
            atCanonicalName(parameter),
            atResource(parameter),
            atHasFieldsOf(parameter),
            atIsOneOf(parameter),
            atUnionTag(parameter),
            atUnionCase(parameter),
            atRequired(parameter),
            atOptionalReason(parameter),
        ])
    }

    List<String> annotations(IdlStructure structure) {
        return prune([
            atCanonicalName(structure),
            atResource(structure),
            atModel(structure),
            atIncludable(structure),
            atInclude(structure),
            atCrud(structure),
            atCreateReadUpdate(structure),
            atFOBD(structure),
        ])
    }

    List<String> annotations(IdlAttribute attribute) {
        return prune([
            atCanonicalName(attribute),
            atResource(attribute),
            atModelKey(attribute),
            atModelIndex(attribute),
            atHasFieldsOf(attribute),
            atIsOneOf(attribute),
            atCreateReadUpdate(attribute),
            atRequired(attribute),
            atUnionTag(attribute),
            atUnionCase(attribute),
            atOptionalReason(attribute),
            atSerializationName(attribute),
        ])
    }

    private List<String> prune(List<String> lines) {
        lines.findAll { it }
    }

    private String atCanonicalName(def node) {
        def value = node.metadata.CanonicalName
        value ? """@CanonicalName("${value}")""" : ''
    }

    private String atComponent(IdlPackage pkg) {
        if (pkg.isComponent()) {
            def name = pkg.getComponentName()
            def marker = name instanceof Boolean || "true".equals(name)

            return (marker || pkg.name.asDeclared.equals(name)) ? "@Component" :
                """@Component("${name}")"""
        }
        return ''
    }

    private String atCopyright(IdlPackage pkg) {
        if (pkg.markedCopyright()) {
            def lines = pkg.getCopyright()

            if (lines.size() == 1) {
                return """@Copyright("${lines[0]}")"""
            } else {
                def sb = new StringBuilder("@Copyright({\n")

                lines.eachWithIndex { line, i ->
                    sb << """    "${line}",\n"""
                }
                sb << "})"
                return sb.toString()
            }
        }
        return ''
    }

    private String atResource(AbstractNamedNode node) {
        if (node.markedResource()) {
            def result = new StringBuilder('@Resource')
            def args = []

            if (node.hasResourceValue()) {
                def prefix = node.hasResourceTypeHolder() ? 'value=' : ''
                def value = formatArg(node.getResourceValue())

                args << "${prefix}${value}"
            }
            if (node.hasResourceTypeHolder()) {
                def value = /"${node.getResourceTypeHolder()}"/

                args << "typeHolder=${value}"
            }
            if (args) {
                result << '(' << args.join(', ') << ')'
            }
            return result.toString()
        }
        return ''
    }

    private String atSerializationName(IdlAttribute node) {
        if (node.markedSerializationName()) {
            def result = new StringBuilder("@SerializationName")
            def value = node.hasSerializationNameValue()? node.getSerializationNameValue() : null
            if (value)
                result << '("' << value << '")'
            return result
        }
        return ""
    }

    private String formatArg(def value) {
        def arg = new StringBuilder()
        def isArray = value.size() > 1

        if (isArray) arg << '{'
        arg << value.collect {
            //use the symbol for constant refs, otherwise quote the string
            it instanceof IdlConstantRef ? it.symbol :
            it instanceof IdlConstant ? it.name : "\"${it}\""
        }.join(', ')
        if (isArray) arg << '}'
        return arg.toString()
    }

    private String atHasFieldsOf(def node) {
        if (node.hasFieldsOf()) {
            return "@HasFieldsOf(${node.getFieldsOf()[0].name}.class)"
        }
        return ''
    }

    private String atIsOneOf(def node) {
        if (node.markedIsOneOf()) {
            def value = formatArg(node.getIsOneOfValue())

            return "@IsOneOf(${value})"
        }
        return ''
    }

    private String atModel(IdlStructure node) {
        node.isModel() ? "@Model" : ''
    }

    private String atModelKey(IdlAttribute node) {
        if (node.isModelKey()) {
            def result = new StringBuilder('@ModelKey')

            if (node.getOrder() > 1) {
                result << '(order=' << node.getOrder() << ')'
            }
            return result.toString()
        }
        return ''
    }

    private String atModelIndex(IdlAttribute node) {
        node.isModelIndex() ? "@ModelIndex" : ''
    }

    private String atIncludable(IdlStructure node) {
        node.isIncludable() ? "@Includable" : ''
    }

    private String atInclude(IdlStructure node) {
        if (node.hasIncludedStructures()) {
            def values = node.getIncludedStructures().collect { "${it.name}.class" }
            return "@Include(" + values.join(", ")  + ")"
        }
        return ''
    }

    private String atUnionTag(AbstractTypedNode node) {
        node.isUnionTag() ? "@UnionTag" : ''
    }

    private String atUnionCase(AbstractTypedNode node) {
        if (node.isUnionCase()) {
            def tag = node.getUnionCaseTag().name
            def values = node.getUnionCaseValues().collect { /"${it.name}"/ }
            def value = values.size() > 1 ? """{${values.join(', ')}}""" : values[0]

            return """@UnionCase(tag="${tag}", value=${value})"""
        }
        return ''
    }

    private String atFOBD(IdlStructure node) {
        node.isOptionalByDefault() ? "@FieldsOptionalByDefault" : ''
    }

    private String atRequired(AbstractTypedNode node) {
        node.markedRequired() ? "@Required" : ''
    }

    private String atCrud(IdlStructure node) {
        //TODO: handle the optional arg for this guy someday
        node.markedCrud() ? "@Crud" : ''
    }

    private String atCreateReadUpdate(IdlStructure node) {
        prune([atCreate(node), atRead(node), atUpdate(node)]).join(' ')
    }

    private String atCreateReadUpdate(IdlAttribute node) {
        prune([atCreate(node), atRead(node), atUpdate(node)]).join(' ')
    }

    private String atCreate(def node) {
        node.markedCreate() ? "@Create(Usage." + node.getCreate() + ")" : ''
    }

    private String atRead(def node) {
        node.markedRead() ? "@Read(Usage." + node.getRead() + ")" : ''
    }

    private String atUpdate(def node) {
        node.markedUpdate() ? "@Update(Usage." + node.getUpdate() + ")" : ''
    }

    private String atOptionalReason(def node) {
        // we can't use node.getOptionalReason(), as it may return a value
        // that is implicitly produced and/or provided by the @x.optional
        // javadoc tag instead
        if (node.metadata.OptionalReason) {
            def result = new StringBuilder('@OptionalReason(')
            def value = node.metadata.OptionalReason

            if (value instanceof List) {
                // fyi - multiline reasons are tough to handle using this
                // buffer builder technique - it does not handle indentation -
                // but, this annotation should be deprecated (soon) anyway.
                result << '{\n'
                value.each { line ->
                    result << """        "${line}",\n"""
                }
                result << '}'
            } else {
                result << /"${value}"/
            }
            result << ')'
            return result.toString()
        }
        return ''
    }

    String getConstantType(IdlConstant constant) {
        final IdlType type = constant.type
        String result

        if (type.isGeneric() && type.isList()) {
            def argType = type.arguments[0]
            result = "${argType.name}[]"
        } else if (type.isPrimitive()) {
            // this should be one of String, Long, Double or Boolean only
            result = type.name
        }
        return result
    }

    String getConstantValue(IdlConstant constant) {
        if (constant.type.isPrimitive()) {
            return getPrimitiveConstantValue(constant.value)
        } else if (constant.type.isGeneric() && constant.type.isList()) {
            def values = constant.value.collect { getPrimitiveConstantValue(it) }
            return '{' + values.join(', ') + '}'
        }
    }

    private String getPrimitiveConstantValue(IdlConstantRef ref) {
        return ref.symbol
    }

    private String getPrimitiveConstantValue(Object value) {
        switch (value.class) {
        case String:
            def s = /"${value}"/
            return s
        case Float:
        case Double:
            return "${value}D"
        case Integer:
        case Long:
            return "${value}"
        default:
            return value.toString()
        }
    }
}

