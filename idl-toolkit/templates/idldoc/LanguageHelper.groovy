/*
 * Copyright 2012-2014 VMware, Inc.    All rights reserved.
 */
package com.vmware.vapi.idl.transformer

import com.vmware.vapi.idl.model.*
import com.vmware.vapi.idl.model.doc.*

import groovy.json.JsonBuilder
import groovy.json.JsonOutput


/**
 * Defines the IDLDoc language helper for concrete generators such as:
 * HTML, XSD and WSDL/WADL.
 */
class IdldocLanguageHelper extends LanguageHelper {

    static final String NAME = "idldoc"

    static String getName() { NAME }

    //fyi: required factory method to construct an instance
    static IdldocLanguageHelper getInstance(LanguageProperties properties, TemplateNamingContext naming) {
        new IdldocLanguageHelper(properties, naming)
    }


    /** Constructs an instance. */
    IdldocLanguageHelper(LanguageProperties properties, TemplateNamingContext naming) {
        super(NAME, properties, naming)
    }


    String makePath(AbstractNamespacedNode node) {
        return makePath(node.namespace).replaceAll("\\.", "/")
    }

    String makePath(IdlNamespace namespace) {
        return namespace.path
    }

    String makePath(IdlPackage pkg) {
        return RelativePath.parseNode(pkg).joinWith('/')
    }

    String serviceHtml(IdlService service) {
        return makePath(service) + "/" + service.name.asCapitalizedWords + "-service.html"
    }

    String structureHtml(IdlStructure structure) {
        return makePath(structure) + "/" + structure.name.asCapitalizedWords + "-structure.html"
    }

    String enumerationHtml(IdlEnumeration enumeration) {
        return makePath(enumeration) + "/" + enumeration.name.asCapitalizedWords + "-enumeration.html"
    }

    String namespaceSummaryHtml(IdlNamespace namespace) {
        return namespace.path +    "/namespaceSummary.html"
    }

    String packageSummaryHtml(IdlPackage pkg) {
        return makePath(pkg) + "/packageSummary.html"
    }

    String nestedStructureUrl(IdlStructure structure, IdlService declaringNode) {
        return "${serviceHtml(declaringNode)}#struct-${structure.name}"
    }

    String nestedEnumerationUrl(IdlEnumeration enumeration, IdlService declaringNode) {
        return "${serviceHtml(declaringNode)}#enum-${enumeration.name}"
    }

    String nestedEnumerationUrl(IdlEnumeration enumeration, IdlStructure declaringNode) {
        return "${structureHtml(declaringNode)}#enum-${enumeration.name}"
    }

    String topDir() {
        return "."
    }

    String topDir(AbstractNamespacedNode node) {
        return invertPath(makePath(node)) + "/"
    }

    String topDir(IdlNamespace namespace) {
        return invertPath(makePath(namespace))
    }

    String topDir(IdlPackage pkg) {
        return invertPath(makePath(pkg))
    }

    String invertPath(String path) {
        return path.replaceAll('[a-zA-Z0-9]+', '..')
    }

    String operationUrl(Map opInfo) {
        return serviceHtml(opInfo.service) + '#oper-' + opInfo.operation.name
    }

    String structureUrl(Map structInfo) {
        return serviceHtml(structInfo.service) + '#struct-' + structInfo.structure.name
    }

    String attributeUrl(Map attrInfo) {
        if (attrInfo.type instanceof IdlStructure) {
            return structureHtml(attrInfo.type) + '#attr-' + attrInfo.attribute.name
        } else if (attrInfo.type instanceof IdlEnumeration) {
            return enumerationHtml(attrInfo.type) + '#attr-' + attrInfo.attribute.name
        }
    }

    def getStructuresInfo(IdlModel model) {
        def result = []
        model.services.each { s ->
            s.structures.each { struct ->
                result << [ 'structure': struct, 'service' : s]
            }
        }
        return result.sort { it.structure.name }
    }

    def getOperationsInfo(IdlModel model) {
        def result = []
        model.services.each { IdlService service ->
            service.operations.each { op ->
                result << [ 'operation': op, 'service' : service]
            }
        }
        return result.sort { it.operation.name }
    }

    String makeType(AbstractTypedNode element) {
        return makeType(element.impliedType)
    }

    String makeType(IdlType type) {
        if (type.isGeneric()) {
            IdlGenericType generic = type.asType(IdlGenericType.class)
            if (generic.isOptional()) {
                return optional(makeType(generic.arguments[0]))
            } else if (generic.isMap()) {
                String args = makeType(generic.arguments[0]) + ', ' + makeType(generic.arguments[1])

                return wrap(generic.asIdentifier.asCanonical + '&lt;', '>', args)
            } else { // List or Set
                String arg = makeType(generic.arguments[0])

                return wrap(generic.asIdentifier.asCanonical + '&lt;', '>', arg)
            }
        }
        if (type.isPrimitive() || type.isVoid()) return type.asIdentifier
        if (type.isReference()) {
            return '<a href="' + referenceTypeUrl(type.asType(IdlReferenceType.class)) + '">' + type.name + '</a>'
        }
        return "WTF?" + type.getClass().getName() + ":::" + type.name
    }

    def wrap = { prefix, suffix, string ->
        prefix + string + suffix
    }

    def optional = { s ->
        wrap('<span class="optional-type">', '</span>', s)
    }

    def referenceTypeUrl(IdlReferenceType type) {
        switch (type.target) {
        case IdlReferenceType.Target.ENUMERATION:
            return type.declaration.declaringNode ? nestedEnumerationUrl(type.declaration, type.declaration.declaringNode) : enumerationHtml(type.declaration)
        case IdlReferenceType.Target.STRUCTURE:
            return type.declaration.declaringNode ? nestedStructureUrl(type.declaration, type.declaration.declaringNode) : structureHtml(type.declaration)
        default:
            return "Illegal Reference to Type: ${type.declaration}"
        }
    }

    String multilineDoc(AbstractNamedNode node) {
        def result = new StringBuilder()

        if (node?.doc?.descriptionModel) {
            result << commentWriter.makeString(node.doc.descriptionModel)
        }
        if (node?.doc?.metadataModel) {
            result << "<p>"
            result << commentWriter.makeString(node.doc.metadataModel)
        }
        if (node?.doc?.optionalReasonModel) {
            result << "<p>"
            result << commentWriter.makeString(node.doc.optionalReasonModel)
        }
        return result.toString()
    }

    String summaryDoc(AbstractNamedNode node) {
        String text = node?.doc?.descriptionModel ? commentWriter.makeString(node.doc.descriptionModel) : null
        if (text) {
            int dot = text.indexOf('.')
            return dot < 0 ? text : text.substring(0, dot+1)
        } else {
            return "Missing description for <em>${node.name}</em>."
        }
    }

    def withComma(collection, Closure closure) {
        if (!collection) return
        int indexToStop = collection.size() -1
        collection.eachWithIndex {item, i ->
            closure(item, indexToStop == i ? '': ',')
        }
    }

    String nodeId(IdlNamespace namespace) {
        return nodeId("ns", namespace.name)
    }

    String nodeId(IdlPackage pkg) {
        return nodeId("pkg", pkg.name)
    }

    String nodeId(IdlStructure structure) {
        return nodeId("ds", structure.qualifiedName)
    }

    String nodeId(IdlService service) {
        return nodeId("s", service.qualifiedName)
    }

    String nodeId(IdlEnumeration enumeration) {
        return nodeId("e", enumeration.qualifiedName)
    }

    String nodeId(String prefix, IdlIdentifier identifier) {
        return nodeId(prefix, identifier.toString())
    }

    String nodeId(String prefix, String dottedPath) {
        return RelativePath.parsePackage(dottedPath).prepend(prefix).joinWith('-')
    }

    String getTreeJson(IdlModel model) {
        return new DataTree(model).getJson()
    }

    Set<IdlService> getServicesByNamespace(IdlModel model, IdlNamespace namespace) {
        return model.services.findAll{ it.namespace.name == namespace.name }
    }

    Set<IdlStructure> getStructuresByNamespace(IdlModel model, IdlNamespace namespace) {
        return model.structures.findAll{ it.namespace.name == namespace.name }
    }

    Set<IdlEnumeration> getEnumerationsByNamespace(IdlModel model, IdlNamespace namespace) {
        return model.enumerations.findAll{ it.namespace.name == namespace.name }
    }

    @Override
    CommentWriter createCommentWriter() {
        new HtmlCommentWriter(
                createLinkConverter(),
                createNameConverter()).injectTermMapping(this.termMapping)
    }

    LinkConverter createLinkConverter() {
        return new LinkConverter() {
            @Override
            String toLink(ResolvedLinkToPackage link) {
                return packageSummaryHtml(link.pkg)
            }

            @Override
            String toName(ResolvedLinkToPackage link) {
                return link.pkg.name
            }

            @Override
            String toLink(ResolvedLinkToType link) {
                switch (link?.type.class) {
                case IdlService: return serviceHtml(link.type)
                case IdlStructure: return structureHtml(link.type)
                case IdlEnumeration: return enumerationHtml(link.type)
                default: return ""
                }
            }

            @Override
            String toName(ResolvedLinkToType link) {
                return link.type.name
            }

            @Override
            String toLink(ResolvedLinkToMember link) {
                switch (link?.member.class) {
                case IdlOperation: return operationUrl([service: link.type, operation: link.member])
                case IdlAttribute: return attributeUrl([type: link.type, attribute: link.member])
                case IdlConstant:
                    //TODO: deal with all possible containers, which is Service, Structure or Enumeration
                    return attributeUrl([type: link.type, attribute: link.member])
                default: return ""
                }
            }

            @Override
            String toName(ResolvedLinkToMember link) {
                return link.member.name
            }
        }
    }

    private NameConverter createNameConverter() {
        return new NameConverter() {
            @Override
            String toName(ResolvedLinkToPackage link) {
                return toFullname(link)
            }

            @Override
            String toBasename(ResolvedLinkToPackage link) {
                return toFullname(link)
            }

            @Override
            String toFullname(ResolvedLinkToPackage link) {
                return link.pkg.name
            }

            @Override
            String toName(ResolvedLinkToType link) {
                //TODO: use current context to decide:
                // if element is in scope, then use basename
                // else use fullname
                return toFullname(link)
            }

            @Override
            String toBasename(ResolvedLinkToType link) {
                return link.type.name.asCapitalizedWords
            }

            @Override
            String toFullname(ResolvedLinkToType link) {
                return link.type.getQualifiedName('asCapitalizedWords')
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
                switch (link?.member.class) {
                case IdlOperation: return operationUrl([service: link.type, operation: link.member])
                case IdlAttribute: return attributeUrl([type: link.type, attribute: link.member])
                case IdlConstant:
                    //TODO: deal with all possible containers, which is Service, Structure or Enumeration
                    return attributeUrl([type: link.type, attribute: link.member])
                default: return ""
                }
            }
        }
    }

    abstract class WithChildren {

        Map<String, TreeLeaf> children = [:]

        WithChildren getOrMake(List<String> folders) {
            if (!folders) return this

            def childName = folders[0]
            def child = children.get(childName)

            if (!child) {
                child = new FolderLeaf(childName)
                addChild(child)
            }
            def newFolders = new ArrayList(folders)
            newFolders.remove(0)
            return child.getOrMake(newFolders)
        }

        void addChild(TreeLeaf leaf) {
            children[leaf.name] = leaf
        }
    }

    abstract class TreeLeaf extends WithChildren {
        final String name

        TreeLeaf(String name) {
            this.name = name
        }

        final Map<String, Object> getData() {
            Map<String, Object> result = [:]

            getLocalData().each { k, v ->
                result[k] = v
            }
            def childrenData = children.values().collect { child ->
                child.data
            }
            if (childrenData) {
                result['children'] = childrenData
            }
            return result
        }

        abstract Map<String, Object> getLocalData()
    }

    class DataTree extends WithChildren {
        DataTree(IdlModel model) {
            model.packages.each { pkg ->
                PackageLeaf packageLeaf = new PackageLeaf(pkg)

                getOrMake(RelativePath.parseNode(pkg).parents)
                        .addChild(packageLeaf)
                pkg.enumerations.each { enumeration ->
                    packageLeaf.addChild(new EnumerationLeaf(enumeration))
                }
                pkg.structures.each { structure ->
                    packageLeaf.addChild(new StructureLeaf(structure))
                }
                pkg.services.each { service ->
                    packageLeaf.addChild(new ServiceLeaf(service))
                }
            }
        }

        List<Object> getData() {
            children.values().collect { it.data }
        }

        String getJson() {
            JsonOutput.prettyPrint(new JsonBuilder(getData()).toString())
        }
    }

    class FolderLeaf extends TreeLeaf {

        FolderLeaf(String name) {
            super(name)
        }

        @Override
        Map<String, Object> getLocalData() {
            return [ data : [title: name]]
        }
    }

    class PackageLeaf extends TreeLeaf {

        private IdlPackage pkg

        PackageLeaf(IdlPackage pkg) {
            super(RelativePath.parsePackage(pkg.name.asCanonical).lastPart)
            this.pkg = pkg
        }

        @Override
        Map<String, Object> getLocalData() {
            return [
                data: [
                    title: name,
                    attr: [href: packageSummaryHtml(pkg)],
                    icon: "http://icons.iconarchive.com/icons/iconexpo/speech-balloon-orange/16/speech-balloon-orange-p-icon.png"
                ],
                attr: [
                    id: nodeId(pkg)
                ]
            ]
        }
    }

    abstract class NamespacedNodeLeaf extends TreeLeaf {
        AbstractNamespacedNode node

        NamespacedNodeLeaf(AbstractNamespacedNode node) {
            super(node.name.asCanonical)
            this.node = node
        }

        @Override
        Map<String, Object> getLocalData() {
            return [
                data: [
                    title:    node.name.asCanonical,
                    attr: [href: getLink()],
                    icon: getIconUrl()
                ],
                attr: [
                    id: nodeId(node)
                ]
            ]
        }

        abstract String getLink()
        abstract String getIconUrl()
    }

    class ServiceLeaf extends NamespacedNodeLeaf {
        ServiceLeaf(IdlService service) {
            super(service)
        }

        String getLink() {
            return serviceHtml(node)
        }

        String getIconUrl() {
            return "http://icons.iconarchive.com/icons/iconexpo/speech-balloon-green/16/speech-balloon-green-s-icon.png"
        }
    }


    class StructureLeaf extends NamespacedNodeLeaf {
        StructureLeaf(IdlStructure structure) {
            super(structure)
        }

        String getLink() {
            return structureHtml(node)
        }

        String getIconUrl() {
            return "http://icons.iconarchive.com/icons/iconexpo/speech-balloon-green/16/speech-balloon-green-d-icon.png"
        }
    }

    class EnumerationLeaf extends NamespacedNodeLeaf {
        EnumerationLeaf(IdlEnumeration enumeration) {
            super(enumeration)
        }

        String getLink() {
            return enumerationHtml(node)
        }

        String getIconUrl() {
            return "http://icons.iconarchive.com/icons/iconexpo/speech-balloon-green/16/speech-balloon-green-e-icon.png"
        }
    }

}
