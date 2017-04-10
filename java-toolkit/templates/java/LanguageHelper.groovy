/* **********************************************************
 * Copyright 2012-2016 VMware, Inc.  All rights reserved. -- VMware Confidential
 * **********************************************************/

package com.vmware.vapi.idl.transformer

import com.vmware.vapi.idl.model.*
import com.vmware.vapi.idl.model.metadata.*
import com.vmware.vapi.idl.model.doc.*
import com.vmware.vapi.idl.transformer.*
import com.vmware.vapi.idl.util.IndentWriter

import org.apache.commons.lang3.text.WordUtils


/**
 * Defines the Java language helper.
 */
class JavaLanguageHelper extends LanguageHelper {

    static final String NAME = 'java'

    private static final int SPACES_IN_TAB = 4

    private static def IDL_TYPE_TO_BINDINGS_TYPE_REPRESENTATION = [
        'boolean': 'com.vmware.vapi.bindings.type.BooleanType',
        'long': 'com.vmware.vapi.bindings.type.IntegerType',
        'double': 'com.vmware.vapi.bindings.type.DoubleType',
        'string': 'com.vmware.vapi.bindings.type.StringType',
        'void': 'com.vmware.vapi.bindings.type.VoidType',
        'opaque': 'com.vmware.vapi.bindings.type.OpaqueType',
        'binary': 'com.vmware.vapi.bindings.type.BinaryType',
        'date_time': 'com.vmware.vapi.bindings.type.DateTimeType',
        'URI': 'com.vmware.vapi.bindings.type.UriType',
        'secret': 'com.vmware.vapi.bindings.type.SecretType',
        'ID': 'com.vmware.vapi.bindings.type.IdType',
        'structure': 'com.vmware.vapi.bindings.type.DynamicStructType',
        'dynamic_structure': 'com.vmware.vapi.bindings.type.DynamicStructType',
        'exception': 'com.vmware.vapi.bindings.type.AnyErrorType',
    ]

    //fyi: required factory method to construct an instance
    static JavaLanguageHelper getInstance(LanguageProperties properties, TemplateNamingContext naming) {
        return new JavaLanguageHelper(properties, naming)
    }

    JavaLanguageHelper(LanguageProperties properties, TemplateNamingContext naming) {
        super(NAME, properties, naming)
        this.typeMapping.referenceMapping = new JavaReferenceMapping();
        this.commentWriter = createCommentWriter()
    }

    String quotify(Object value) {
        '"' + value + '"'
    }

    String stringLiteral(Object value) {
        return quotify(value)
    }

    String stringOrNullLiteral(Object value) {
        if (value) {
            return quotify(value)
        }
        return "null"
    }

    String getOptionalTypeName(IdlType type) {
        type = type.addOptionalWrapper()
        return toCanonicalForm(type)
    }

    String getTypeName(AbstractTypedNode element) {
        return getTypeName(element.impliedType)
    }

    String getTypeName(IdlType type) {
        return toCanonicalForm(type)
    }

    /**
     * Determines the name of a Java type which is appropriate for a template
     * type parameter of an l-value: <code> List<?> = ... </code>
     */
    String getTypeNameForTypeParameterForLvalue(IdlType type) {
        if (type.isVoid()) {
            return 'java.lang.Void'
        } else {
            return getOptionalTypeName(type)
        }
    }

    /**
     * Determines the name of a Java type which is appropriate for a template
     * type parameter of an r-value: <code> ... = new ArrayList<Object>() </code>
     */
    String getTypeNameForTypeParameterForRvalue(IdlType type) {
        if (type.isVoid()) {
            return 'java.lang.Void'
        } else {
            return getOptionalTypeName(type)
        }
    }

    String getTypeDefinition(AbstractTypedNode element) {
        IdlType type = element.impliedType ?: element.type
        return getTypeDefinition(element, type)
    }

    String getTypeDefinition(AbstractTypedNode element, IdlType type) {
        if (type.isGeneric()) {
            if (type.isList()) {
                return 'new com.vmware.vapi.bindings.type.ListType('+getTypeDefinition(element, type.arguments[0])+')'
            }
            if (type.isMap()) {
                StringBuilder result = new StringBuilder()
                result.append('new com.vmware.vapi.bindings.type.MapType(')
                result.append(getTypeDefinition(element, type.arguments[0]))
                result.append(', ')
                result.append(getTypeDefinition(element, type.arguments[1]))
                result.append(')')
                return result.toString()
            }
            if (type.isSet()) {
                return 'new com.vmware.vapi.bindings.type.SetType('+getTypeDefinition(element, type.arguments[0])+')'
            }
            if (type.isOptional()) {
                return 'new com.vmware.vapi.bindings.type.OptionalType('+getTypeDefinition(element, type.arguments[0])+')'
            }
        }
        if (type.isReference()) {
            if (type.isEnumeration()) {
                StringBuilder result = new StringBuilder()
                result.append('new com.vmware.vapi.bindings.type.EnumType("')
                result.append(type.declaration.getQualifiedName('asCanonical'))
                result.append('", ')
                result.append(getBindingClassFullName(type.declaration))
                result.append('.class)')
                return result.toString()
            } else {
                return 'new com.vmware.vapi.bindings.type.TypeReference<com.vmware.vapi.bindings.type.StructType>() { @java.lang.Override public com.vmware.vapi.bindings.type.StructType resolve() { return ' + getStructDefFullName(type) + '; } }'
            }
        }

        String typeRepr = IDL_TYPE_TO_BINDINGS_TYPE_REPRESENTATION[type.asIdentifier.asCanonical]
        // TODO: refactor for better (more generic) approach for this
        if (type == IdlPrimitiveType.ID && element.isResource()) {
            if (element.hasResourceTypeHolder() || element.getResourceTypes().size() > 1) {
                // Polymorphic resource
                StringBuilder resources = new StringBuilder()
                if (element.getResourceTypes().size() > 0) {
                    resources.append('new String[] {"')
                    resources.append(element.getResourceTypes().join('", "'))
                    resources.append('"}')
                } else {
                    resources.append('null');
                }
                def resourceTypeHolder = 'null'
                if (element.getResourceTypeHolderNode() != null) {
                    resourceTypeHolder = '"' + element.getResourceTypeHolderNode().name.asCanonical + '"';
                }
                return "new ${typeRepr}(" + resources.toString() + ", " + resourceTypeHolder + ")"
            } else {
                // Static resource type
                return "new ${typeRepr}(\"${element.getResourceTypes()[0]}\")"
            }
        } else if (type.isPrimitive() && type.isDynamicStructure() && element.hasFieldsOf()) {
            // dynamic structure with @HasFieldOf
            String validator = "java.util.Arrays.<com.vmware.vapi.internal.data.ConstraintValidator>asList(" +
                    "new com.vmware.vapi.internal.bindings.HasFieldsOfValidator(" +
                    // TODO: need to access the IdlReferenceType of the @HasFieldsOf, but it
                    //       is not exposed (so create new IdlReferenceType instance for now)
                    getTypeDefinition(element,
                            IdlReferenceType.fromDatastructure(element.getFieldsOf()[0])) + "))"
            return "new ${typeRepr}(" + validator + ")"
        } else {
            return "new ${typeRepr}()"
        }
    }

    boolean hasIsOneOfFields(List<IdlAttribute> attributes) {
        boolean result = false;
        attributes.each { attribute ->
            if (attribute.hasIsOneOfValue()) {
                result = true;
            }
        }

        return result;
    }

    List<String> getIsOneOfFieldValidators(List<AbstractTypedNode> attributes) {
        List<String> result = new ArrayList<String>()
        attributes.each { attribute ->
            if (attribute.hasIsOneOfValue()) {
                StringBuilder validator = new StringBuilder()
                validator.append('validators.add(new com.vmware.vapi.internal.bindings.IsOneOfValidator("')
                         .append(getSerializationName(attribute))
                         .append('", new String[] {"')
                         .append(attribute.getIsOneOfTypes().join('", "'))
                         .append('"}));')

                result.add(validator.toString())
            }
        }
        return result
    }

    String getSimpleName(IdlService service) {
        //TODO: this does not work - we must go thru the TemplateNamingContext API
        //return service.getBasename()
        //return naming.simpleName
        return service.name.asCapitalizedWords
    }

    String getJavaPackageName(IdlService service) {
        //TODO: this does not work - we must go thru the TemplateNamingContext API
        //return service.name.asCanonical
        return naming.packageName
    }

    String getJavaImplPackageName(IdlService service) {
        return getJavaPackageName(service) + '.impl'
    }

    /** Returns simple name of the client side Java interface for the service */
    String getClientInterfaceName(IdlService service) {
        return getSimpleName(service)
    }

    /** Returns FQ name of the client side Java interface for the service */
    String getClientInterfaceFullName(IdlService service) {
        return getJavaPackageName(service) + '.' + getClientInterfaceName(service)
    }

    /** Returns simple name of the provider side Java interface for the service */
    String getProviderInterfaceName(IdlService service) {
        return getSimpleName(service) + 'Provider'
    }

    /** Returns FQ name of the provider side async Java interface for the service */
    String getProviderInterfaceFullName(IdlService service) {
        return getJavaPackageName(service) + '.' + getProviderInterfaceName(service)
    }

    /** Returns simple name of the provider side sync Java interface for the service */
    String getSyncProviderInterfaceName(IdlService service) {
        return getSimpleName(service) + 'SyncProvider'
    }

    /** Returns FQ name of the provider side sync Java interface for the service */
    String getSyncProviderInterfaceFullName(IdlService service) {
        return getJavaPackageName(service) + '.' + getSyncProviderInterfaceName(service)
    }

    /** Returns simple name of the common xxxTypes Java interface for the service */
    String getCommonInterfaceName(IdlService service) {
        return getSimpleName(service) + 'Types'
    }

    String getJavaImplName(IdlService service) {
        return getSimpleName(service) + 'Impl'
    }

    String getJavaImplFullName(IdlService service) {
        return getJavaImplPackageName(service) + '.' + getJavaImplName(service)
    }

    String getFieldData(AbstractTypedNode node, IdlStructure struct = null) {
        def isExplicitlyOptional = node.type.isGeneric() && node.type.isOptional()
        if (struct && struct.markedCrud() && !node.isRequiredForAll()) {
            // In @Crud structure, @UnionCase fields must be treated as
            // explicitly optional, because they might not be present
            // for some of the CRUD operations
            // UNLESS the field isRequiredForAll CRUD operations
            // in which case we don't change anything
            isExplicitlyOptional = true
        }

        if (struct && struct.isOptionalByDefault()) {
            // For @OptionalByDefault structure, the Union validation must
            // be loosened just like for @Crud above
            isExplicitlyOptional = true
        }

        return 'new com.vmware.vapi.internal.data.UnionValidator.FieldData(' +
                    '"' + node.name.asCanonical + '", ' +
                    isExplicitlyOptional + ')'
    }

    static String getJavaMethodName(IdlOperation operation) {
        return operation.name.asMixedCase
    }

    static String getJavaParamName(IdlParameter param) {
        return param.name.asMixedCase
    }

    static String getPropertyAccessorName(IdlAttribute attribute) {
        def accessor = getCapitalized(attribute, 'get')
        if (accessor == 'getClass') {
            accessor = 'get_Class'
        }
        return accessor
    }

    static String getPropertyMutatorName(IdlAttribute attribute) {
        return getCapitalized(attribute, 'set')
    }

    static String getCapitalized(IdlAttribute attribute, String accessorPrefix) {
        def accessor = accessorPrefix
        def canonicalId = attribute.name.asCanonical
        def capitalizedId = attribute.name.asCapitalizedWords
        // if the field canonical name is v_app the method name should be getvApp()
        // if the field canonical name is V_app the method name should be getVApp()
        // while the capitalizedId for both v_app and V_app is VApp
        if (canonicalId.size() > 1 && canonicalId.charAt(0).isLowerCase() && canonicalId[1] == '_') {
            accessor += capitalizedId[0].toLowerCase() + capitalizedId.substring(1)
        } else {
            accessor += capitalizedId
        }
        return accessor
    }

    static String getJavaStructName(IdlType type) {
        return getJavaStructName(type.asIdentifier)
    }

    static String getJavaStructName(IdlIdentifier id) {
        return id.asCapitalizedWords
    }

    String getJavaConstantName(IdlIdentifier id) {
        return id.asUpperCaseWithUnderscores
    }

    String getPrimitiveConstantValue(IdlConstantRef constantRef) {
        return getConstantValue(constantRef.declaration)
    }

    String getPrimitiveConstantValue(IdlConstant constant) {
        return getPrimitiveConstantValue(constant.value)
    }

    String getPrimitiveConstantValue(String value) {
        return '"' + value + '"'
    }

    String getPrimitiveConstantValue(Long value) {
        return value + 'L'
    }

    String getPrimitiveConstantValue(Double value) {
        return value + 'D'
    }

    String getPrimitiveConstantValue(Boolean value) {
        return value.toString()
    }

    String getConstantValue(IdlConstant constant) {
        if (constant.type.isPrimitive()) {
            return getPrimitiveConstantValue(constant)
        } else if (constant.type.isList()) {
            def values = []
            constant.value.each { value ->
                values.add(getPrimitiveConstantValue(value))
            }
            return "{" + values.join(", ") + "}"
        }
    }

    String getConstantTypeName(AbstractTypedNode element) {
        return getConstantTypeName(element.impliedType)
    }

    String getConstantTypeName(IdlType type) {
        if (type.isGeneric() && type.isList()) {
            IdlType nested = type.arguments[0]
            return toCanonicalForm(type.arguments[0]) + '[]'
        }

        return toCanonicalForm(type)
    }

    private void printCommentHeader(IndentWriter writer) {
        writer.indent().println('/**')
    }

    private void printCommentBody(IdlDocumentation doc, IndentWriter writer) {
        printDocModel(doc.descriptionModel, writer)
    }

    private void printCommentLine(String line, IndentWriter writer) {
        printCommentPartialLine(line, writer)
        writer.println()
    }

    private void printCommentPartialLine(String line, IndentWriter writer) {
        if (line && !line.isEmpty()) {
            writer.indent().print(' * ').print(line)
        } else {
            writer.indent().print(' *')
        }
    }

    private void printEmptyCommentLine(IndentWriter writer) {
        printCommentLine('', writer)
    }

    private void printCommentFooter(IndentWriter writer) {
        writer.indent().println(' */')
    }

    private IndentWriter createIndentWriter(StringWriter backend, int indentLevel) {
        IndentWriter writer = new IndentWriter(backend)
        writer.indentSpaces = SPACES_IN_TAB
        writer.indentLevel = indentLevel
        return writer
    }

    String getComment(IdlDocumentation doc,
                      int indentLevel,
                      AbstractNamedNode commentTarget = null) {

        StringWriter sw = new StringWriter()
        IndentWriter writer = createIndentWriter(sw, indentLevel)

        if (doc.description && doc.description[0]) {
            printCommentHeader(writer)
            printCommentBody(doc, writer)
            if (commentTarget instanceof IdlEnumeration) {
                printEnumerationExtraComment(writer)
            }
            printCommentFooter(writer)
        }

        return sw.toString()
    }

    void printEnumerationExtraComment(IndentWriter writer) {
        printEmptyCommentLine(writer)
        printCommentLine('<p> See {@link com.vmware.vapi.bindings.ApiEnumeration enumerated types description}.',
                         writer)
    }

    String getMethodComment(IdlOperation operation,
                            int indentLevel,
                            boolean invocationConfig = false,
                            boolean async = false,
                            boolean invocationContext = false) {

        StringWriter sw = new StringWriter()
        IndentWriter writer = createIndentWriter(sw, indentLevel)

        printCommentHeader(writer)
        if (operation.doc.description && operation.doc.description[0]) {
            printCommentBody(operation.doc, writer)
        }
        if (isClientSideTemplate()) {
            printOverloadExtraComment(invocationConfig, async, writer)
        }

        if (async) {
            // if the method is async the result and errors comments are
            // included in the method description javadoc
            printResultComment(operation.result, writer, false)
            printOperationErrorsComment(operation, writer, false)
        }

        boolean hasParameters = operation.parameters || invocationConfig || invocationContext
        if ((hasParameters && operation.doc) || (async || operation.doc)) {
            printEmptyCommentLine(writer)
        }

        // @param tags
        operation.parameters.each {
            printParamComment(it, writer)
        }

        String ASYNC_CALLBACK_PARAM_JAVADOC =
            '@param asyncCallback Receives the status (progress, result or error) of the operation invocation.'
        String ASYNC_CONTEXT_PARAM_JAVADOC =
            '@param asyncContext Handle to report the progress, result or error of the operation invocation.'

        if (async) {
            printCommentLine(isClientSideTemplate()? ASYNC_CALLBACK_PARAM_JAVADOC
                                                   : ASYNC_CONTEXT_PARAM_JAVADOC,
                             writer)
        }

        if (invocationConfig) {
            printCommentLine('@param invocationConfig Configuration for the method invocation.',
                             writer)
        }

        if (invocationContext) {
            printCommentLine('@param invocationContext Information about a method invocation.',
                             writer)
        }

        // if the method is async @result should not be added
        if (!async) {
            // @result tag
            printResultComment(operation.result, writer)
            // @throws tags
            printOperationErrorsComment(operation, writer)
        }

        printCommentFooter(writer)

        return sw.toString()
    }

    private String printOverloadExtraComment(boolean invocationConfig, boolean async,
                                             IndentWriter writer) {
        StringBuilder extraComment = new StringBuilder('<p>')

        printEmptyCommentLine(writer)
        if (async) {
            printCommentLine('<p>Asynchronous method overload. Result of the invocation will be ', writer)
            printCommentLine('reported via the specified {@code asyncCallback}.', writer)
        } else {
            printCommentLine('<p>Synchronous method overload. Result of the invocation will be ', writer)
            printCommentLine('reported as a method return value.', writer)
        }

        if (invocationConfig) {
            printCommentLine('Use {@code invocationConfig} to specify configuration for this particular invocation.', writer)
        }
    }

    private void printParamComment(IdlParameter param, IndentWriter writer) {
        String prefix = '@param ' + getJavaParamName(param) + ' '
        if (param.doc.descriptionModel) {
            printDocModel(param.doc.descriptionModel, prefix, writer)
        } else {
            printCommentLine(prefix, writer)
        }

        printMetadataComment(param, writer)
        printOptionalReasonComment(param, writer)
    }

    private void printResultComment(IdlResult result, IndentWriter writer,
                                    boolean useJavaDocTag = true) {
        if (!result.doc.descriptionModel) {
            // no documentation - nothing to generate
            return
        }

        String prefix = null;
        if (useJavaDocTag) {
            prefix = '@return '
        } else {
            printEmptyCommentLine(writer)
            printCommentLine('<p><b>Operation Result</b>:<br>', writer)
        }

        printDocModel(result.doc.descriptionModel, prefix, writer)
        printMetadataComment(result, writer)
        printOptionalReasonComment(result, writer)

        if (!useJavaDocTag) {
            printCommentLine('</p>', writer);
        }
    }

    private void printOperationErrorsComment(IdlOperation operation, IndentWriter writer,
                                             boolean useJavaDocTag = true) {
        boolean hasErrors = operation.errors || operation.doc.privilegeModel
        if (hasErrors && !useJavaDocTag) {
            printEmptyCommentLine(writer)
            printCommentLine('<p><b>Operation Errors:</b></br>', writer)
        }

        operation.errors.each {
            printErrorComment(it, writer, useJavaDocTag)
        }
        if (operation.doc.privilegeModel) {
            printSingleErrorInstanceComment(operation.doc.privilegeModel,
                "com.vmware.vapi.std.errors.Unauthorized", writer, useJavaDocTag)
        }

        if (hasErrors && !useJavaDocTag) {
            printCommentLine('</p>', writer);
        }
    }

    private void printErrorComment(IdlError error, IndentWriter writer,
                                    boolean useJavaDocTag) {
        String fullExceptionName = getBindingClassFullName(error.type.declaration)
        error.doc.getDescriptionModels().each {
            printSingleErrorInstanceComment(it, fullExceptionName, writer, useJavaDocTag)
        }
    }

    private void printSingleErrorInstanceComment(DocModel doc, String fullExceptionName,
                                                 IndentWriter writer, boolean useJavaDocTag) {
        String comment = null;
        if (useJavaDocTag) {
            comment = "@throws ${fullExceptionName} ";
        } else {
            comment = "{@link ${fullExceptionName}} - ";
        }
        List<String> lines = commentWriter.makeLines(doc)
        lines.eachWithIndex { line, i ->
            comment += line
            if (i == lines.size() - 1) {
                // skip new line char for the last line, because we
                // might want to append <br> to the same line
                printCommentPartialLine(comment, writer)
            } else {
                printCommentLine(comment, writer)
            }
            comment = ''
        }

        if (useJavaDocTag) {
            writer.println()
        } else {
            writer.println('<br>')
        }
    }

    private void printOptionalReasonComment(AbstractNamedNode node, IndentWriter writer) {
        if (node.doc.optionalReasonModel) {
            printDocModel(node.doc.optionalReasonModel, writer)
        }
    }

    private void printMetadataComment(AbstractNamedNode node, IndentWriter writer) {
        if (node.doc.metadataModel) {
            printDocModel(node.doc.metadataModel, writer)
        }
    }

    /**
     * Print the given IDL doc model.
     *
     * @param docModel IDL doc model.
     * @param firstLinePrefix Prefix to prepend to the first line of the comment
     *        only (e.g. "@param paramName" when printing docModel for an operation parameter)
     * @param writer writer to print to
     */
    private void printDocModel(DocModel docModel, String firstLinePrefix = null, IndentWriter writer) {
        commentWriter.makeLines(docModel).eachWithIndex { line, i ->
            if (i == 0 && firstLinePrefix) {
                printCommentLine(firstLinePrefix + line, writer)
            } else {
                printCommentLine(line, writer)
            }
        }
    }

    String getPropertyComment(IdlAttribute property, int indentLevel, boolean setter = false) {
        StringWriter sw = new StringWriter()
        IndentWriter writer = createIndentWriter(sw, indentLevel)

        printCommentHeader(writer)
        printCommentBody(property.doc, writer)
        printEmptyCommentLine(writer)
        printOptionalReasonComment(property, writer)
        if (property.doc.optionalReasonModel) {
            printEmptyCommentLine(writer)
        }
        if (setter) {
            // TODO: extract method/constants here (in the lang helper)
            printCommentLine("@param ${getJavaFieldName(property.name)} New value for the property.", writer)
        } else {
            printCommentLine('@return The current value of the property.', writer)
        }
        printMetadataComment(property, writer)
        printCommentFooter(writer)

        return sw.toString()
    }

    String getNativeEnumConstantComment(IdlEnumeration e, IdlConstant c, int indentLevel) {
        StringWriter sw = new StringWriter()
        IndentWriter writer = createIndentWriter(sw, indentLevel)
        printCommentHeader(writer)
        printCommentLine("Represents {@link ${e.name}#${c.name}}.", writer);
        printCommentFooter(writer)

        return sw.toString()
    }

    String addTrailingComma(String str) {
        return str ? "${str}, " : str
    }

    String getParameterList(IdlOperation operation) {
        return operation.parameters.collect {
            getTypeName(it) + ' ' + getJavaParamName(it)
        }.join(', ')
    }

    static String getParameterListForInvocation(IdlOperation operation) {
        return operation.parameters.collect { getJavaParamName(it) }.join(', ')
    }

    String getAugmentedErrorTypesListAsJava(IdlOperation operation) {
        return buildErrorTypesListJava(getAugmentedErrors(operation));
    }

    String getErrorTypesListAsJava(IdlOperation operation) {
        return buildErrorTypesListJava(operation.getErrors());
    }

    private String buildErrorTypesListJava(List<IdlError> errors) {
        if (errors) {
            def list = errors.collect { this.getTypeDefinition(null, it.type) }
            return 'java.util.Arrays.<com.vmware.vapi.bindings.type.Type>asList(' +
                list.join(', ') + ')'
        } else {
            return 'null'
        }
    }

    String getJavaStubName(IdlService service) {
        return getSimpleName(service) + 'Stub'
    }

    String getDefinitionsName(IdlService service) {
        return getSimpleName(service) + 'Definitions'
    }

    String getJavaPackageName(IdlNamespace namespace) {
        getPackageNameHandlingSuffixForStdTypes(namespace)
    }

    String getDefinitionsFullName(IdlService service) {
        return getJavaPackageName(service.namespace) + '.' + getDefinitionsName(service)
    }

    String getPackageDefinitionsFullName(IdlNamespace namespace) {
        return getJavaPackageName(namespace) + '.StructDefinitions'
    }

    /**
    * Returns valid Java reference to the bindings <code>Type</code>
    * field generated for the structure referred by an
    * <code>IdlReferenceType</code>.
    *
    * @param refType reference to get the type field for
    * @return <code>String</code> representing the Java reference
    */
    String getStructDefFullName(IdlReferenceType refType) {
        return getStructDefFullName(refType.declaration)
    }

    /**
     * Returns valid Java reference to the bindings <code>Type</code>
     * field generated for the <code>IdlStructure</code>.
     *
     * @param structure structure to get the type field for
     * @return <code>String</code> representing the Java reference
     */
    String getStructDefFullName(IdlStructure structure) {
        if (structure.declaringNode == null) {
            // reference to top-level structure, use its
            // definition from StructDefinitions of the package
            return getPackageDefinitionsFullName(structure.namespace) +
                    '.' + getJavaFieldName(structure.name)
        }

        // structure nested in service, use its definition
        // from <enclosing service>Definition
        return getNestedStructDefFullName((IdlService) structure.declaringNode,
                                    structure.name)
    }

    private String getNestedStructDefFullName(IdlService service,
                                              IdlIdentifier structId) {
        getDefinitionsFullName(service) + '.' + getJavaFieldName(structId)
    }

    String getInputDefFullName(IdlOperation operation) {
        getDefinitionsFullName(operation.declaringNode) + '.' + getInputDefSimpleName(operation)
    }

    String getInputDefSimpleName(IdlOperation operation) {
        "__" + getJavaFieldName(operation.name) + 'Input'
    }

    String getOutputDefFullName(IdlOperation operation) {
        getDefinitionsFullName(operation.declaringNode) + '.' + getOutputDefSimpleName(operation)
    }

    String getOutputDefSimpleName(IdlOperation operation) {
        "__" + getJavaFieldName(operation.name) + 'Output'
    }

    String getOperationDefFullName(IdlOperation operation) {
        getDefinitionsFullName(operation.declaringNode) + '.' + getOperationDefSimpleName(operation)
    }

    String getOperationDefSimpleName(IdlOperation operation) {
        "__" + getJavaFieldName(operation.name) + 'Def'
    }

    /** Assumes {@code operation} has {@code @RequestMapping} metadata. */
    String getOperationDefCtorParams(IdlOperation op) {
        def operationId = stringLiteral(op.name.asCanonical)
        def urlTemplate = stringLiteral(op.getRequestMappingValue())
        def httpMethod = stringLiteral(op.getRequestMappingMethod())
        def acceptValue = stringOrNullLiteral(op.getRequestMappingAccept())
        def contentTypeValue = stringOrNullLiteral(op.getRequestMappingContentType())
        "${operationId}, ${urlTemplate}, ${httpMethod}, ${acceptValue}, ${contentTypeValue}"
    }

    String getOperationDefParamRegistration(IdlParameter param) {
        def operDefRef = getOperationDefSimpleName(param.declaringNode)
        def paramName = stringLiteral(param.name.asCanonical)
        if (param.markedRequestBody()) {
            return "${operDefRef}.registerRequestBody(${paramName});"
        } else if (param.markedPathVariable()) {
            def pathVar = stringLiteral(param.getPathVariableValue())
            return "${operDefRef}.registerPathVariable(${pathVar}, ${paramName});"
        } else if (param.markedRequestParam()) {
            def reqParam = stringLiteral(param.getRequestParamValue())
            return "${operDefRef}.registerRequestParam(${reqParam}, ${paramName});"
        } else if (param.markedRequestHeader()) {
            def reqHeader = stringLiteral(param.getRequestHeaderValue())
            return "${operDefRef}.registerRequestHeader(${reqHeader}, ${paramName});"
        }
        return null;
    }

    String getOperationDefListForService(IdlService service) {
        service.operations.findAll {
            it.markedRequestMapping()
        }.collect {
            getOperationDefFullName(it)
        }.join(', ')
    }


    // TODO: rest native: move these down in the idl-toolkit so that
    //       other language templates can benefit of reusing it
    /**
     * {@link IdlAttribute}'s serialization name can be changed
     *  {@code @SerializationName}, while {@link IdlParameter} doesn't
     *  support this annotation at all.
     */
    String getSerializationName(AbstractTypedNode node) {
        if (node instanceof IdlAttribute) {
            return getSerializationName((IdlAttribute) node)
        }
        return node.name.asCanonical
    }

    String getSerializationName(IdlAttribute attr) {
        if (attr.markedSerializationName()) {
            return attr.getSerializationNameValue();
        }
        return attr.name.asCanonical
    }

    String getJavaFieldName(IdlIdentifier field) {
        def canonicalId = field.asCanonical
        def mixedId = field.asMixedCase
        // asMixedCase returns vApp for both v_app and V_app canonical names
        // the correct result is: v_app -> vApp, V_app -> VApp
        if (canonicalId.size() > 1 && canonicalId.charAt(0).isUpperCase() && canonicalId[1] == '_') {
            return mixedId[0].toUpperCase() + mixedId.substring(1)
        }
        return mixedId
    }

    String getSyncApiIfaceName(IdlService service) {
        return getSimpleName(service) + 'SyncApiInterface'
    }

    String getAsyncApiIfaceName(IdlService service) {
        return getSimpleName(service) + 'ApiInterface'
    }

    String getApiMethodName(IdlOperation operation) {
        return operation.name.asCapitalizedWords + 'ApiMethod'
    }

    /**
     * Returns FQ name of a bindings class for structure or enum. Handles
     * "Types" suffix of outer class for nested defintions.
     */
    String getBindingClassFullName(AbstractNamedNode typeDeclarationNode) {
        List<String> tokens = new ArrayList<String>()
        AbstractNamedNode node = typeDeclarationNode
        AbstractNamedNode lastNode = null
        boolean isLastNode = true
        while (node) {
            tokens.add(getBindingClassSimpleName(node, isLastNode))
            lastNode = node
            node = node.declaringNode
            isLastNode = false
        }

        tokens.add(getPackageNameHandlingSuffixForStdTypes(lastNode.namespace))
        return tokens.reverse().join('.')
    }

    String getBindingClassSimpleName(AbstractNamedNode node,
                                     boolean isLastNode = true) {
        if (node instanceof IdlService) {
            if (isLastNode) {
                // TODO: extract a method to deal with levels of nesting
                if (isClientSideTemplate()) {
                    return getClientInterfaceName(node)
                } else {
                    if (isSyncProviderTemplate()) {
                        return getSyncProviderInterfaceName(node)
                    } else {
                        return getProviderInterfaceName(node)
                    }
                }
            } else {
                return getCommonInterfaceName(node)
            }
        } else if (node instanceof IdlStructure) {
            return getJavaStructName(node.name)
        } else {
            // enums and others
            return node.name.asCapitalizedWords
        }
    }

    // TODO: this method includes a patch to handle --package-suffix
    //       and std types;
    //
    //   problem: with --package-suffix the TemplateNamingContext "naming"
    //            includes the suffix in the generated JAva package names;
    //            however we ship pre-generated bindings for the std types
    //            which are always in the "com.vmware.vapi.std" (without the
    //            suffix)
    //
    //   temp solution: for std types do not use "naming" but directly
    //                  the namespace
    String getPackageNameHandlingSuffixForStdTypes(IdlNamespace namespace) {
        if (namespace?.name?.startsWith("com.vmware.vapi.std")) {
            // this way we avoid any prefix or suffix being added to the
            // package for the std types (e.g. errors)
            return namespace.name
        } else {
            return naming.getPackageName(namespace)
        }
    }

    String getSelfAndSuperFieldsAsFormalParams(IdlStructure structure) {
        formatAsFormalParams(getSelfAndSuperAttributes(structure))
    }

    String getSelfAndSuperFieldsAsActualParams(IdlStructure structure) {
        formatAsActualParams(getSelfAndSuperAttributes(structure))
    }

    String formatAsFormalParams(List<AbstractTypedNode> typedNodes) {
        typedNodes.collect {
            getTypeName(it) + ' ' + getJavaFieldName(it.name)
        }.join(', ')
    }

    String formatAsActualParams(List<AbstractTypedNode> typedNodes) {
        typedNodes.collect {
            getJavaFieldName(it.name)
        }.join(', ')
    }

    String getSuperFieldsAsActualParams(IdlStructure structure) {
        formatAsActualParams(getSuperOnlyAttributes(structure))
    }

    List<IdlAttribute> getSelfAndSuperAttributes(IdlStructure structure,
                                                 Closure filter = Closure.IDENTITY) {
        List<IdlAttribute> result = new LinkedList<IdlAttribute>();
        collectSelfAndSuperAttributes(structure, filter, result);
        return result;
    }

    List<IdlAttribute> getSuperOnlyAttributes(IdlStructure structure) {
        if (structure.extendsType && structure.extendsType.isStructure()) {
            return getSelfAndSuperAttributes(structure.extendsType.declaration)
        }

        Collections.emptyList()
    }

    boolean isOptional(IdlAttribute attr) {
        attr.impliedType.isGeneric() && attr.impliedType.isOptional()
    }

    boolean isRequired(IdlAttribute attr) {
        !isOptional(attr)
    }

    protected void collectSelfAndSuperAttributes(IdlStructure structure,
                                                 Closure filter,
                                                 List<IdlAttribute> accum) {
        if (structure.extendsType && structure.extendsType.isStructure()) {
            collectSelfAndSuperAttributes(structure.extendsType.declaration,
                                          filter,
                                          accum)
        }
        accum.addAll(structure.impliedAttributes.findAll(filter))
    }


    class JavaReferenceMapping implements ReferenceMapping {

        @Override
        String getStandardForm(IdlReferenceType reference) {
            return getBindingClassFullName(reference.declaration)
        }

        @Override
        String getOptionalForm(IdlReferenceType reference) {
            return getBindingClassFullName(reference.declaration)
        }

        @Override
        String getGenericForm(IdlReferenceType reference) {
            return getBindingClassFullName(reference.declaration)
        }
    }
    /**
     * Return the name of the appropriate Java base class for a VMODL2
     * structure or error type.
     */
    String getBaseStructureReferenceName(IdlStructure structure) {
       if (structure.extendsType == null) {
           // TODO: the "else" branch is obviously a fake value; it might
           //       be needed for @specializable, but is not used yet
           return structure.isErrorType() ? "com.vmware.vapi.bindings.ApiError"
                                          : " TODO_base_structure"
       }
       return getTypeName(structure.extendsType)
    }

    /**
     * Returns java code representing new string array of structure fields
     * marked with <code>@ModelKey</code>. The array preserve the order
     * specified with the <code>@ModelKey</code> order property.
     */
    String getModelPrimaryKeys(IdlStructure structureType) {
        List<IdlAttribute> keys = structureType.getModelKeys()
        if (!keys) {
            return "null"
        }

        StringBuilder result = new StringBuilder()
        result.append("java.util.Arrays.asList(new String []{")
              .append(keys.collect { stringLiteral(getSerializationName(it)) }.join(", "))
              .append("})")
        return result.toString()
    }

    private LinkConverter createLinkConverter() {
        return new LinkConverter() {
            @Override
            String toLink(ResolvedLinkToPackage link) {
                return toName(link)
            }

            @Override
            String toName(ResolvedLinkToPackage link) {
                return link.pkg.name
            }

            @Override
            String toLink(ResolvedLinkToType link) {
                return toName(link)
            }

            @Override
            String toName(ResolvedLinkToType link) {
                return toName(link.type)
            }

            @Override
            String toLink(ResolvedLinkToMember link) {
                return toName(link)
            }

            @Override
            String toName(ResolvedLinkToMember link) {
                StringBuilder result = new StringBuilder(toName(link.type))

                switch (link.member) {
                    case null: break
                    case IdlConstant:
                        result << '#' << getJavaConstantName(link.member.name)
                        break
                    case IdlOperation:
                        result << '#' << getJavaMethodName(link.member)
                        break
                    case IdlAttribute:
                        result << '#' << getPropertyAccessorName(link.member)
                        break
                    default:
                        result << '.' << link.member.name
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
                return toBasename(link)
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
                StringBuilder result = new StringBuilder()

                switch (link.member) {
                    case null:
                        // TODO: maybe throw an exception
                        break
                    case IdlConstant:
                        result << getJavaConstantName(link.member.name)
                        break
                    case IdlOperation:
                        result << getJavaMethodName(link.member) << "()"
                        break
                    case IdlAttribute:
                        result << getPropertyAccessorName(link.member) << "()"
                        break
                    default:
                        result << link.member.name
                        break
                }
                return result.toString()
            }

            @Override
            String toFullname(ResolvedLinkToMember link) {
                StringBuilder result = new StringBuilder(toName(link.type))

                switch (link.member) {
                    case null:
                        // TODO: maybe throw an exception
                        break
                    case IdlConstant:
                        result << '.' << getJavaConstantName(link.member.name)
                        break
                    case IdlOperation:
                        result << '.' << getJavaMethodName(link.member) << "()"
                        break
                    case IdlAttribute:
                        result << '.' << getPropertyAccessorName(link.member) << "()"
                        break
                    default:
                        result << '.' << link.member.name
                        break
                }
                return result.toString()
            }
        }
    }

    @Override
    CommentWriter createCommentWriter() {
        return new JavadocCommentWriter(
                createLinkConverter(),
                createNameConverter()).injectTermMapping(this.termMapping)
    }

    boolean inStdPackage(IdlStructure structure) {
        return structure.namespace && structure.namespace.name.startsWith("com.vmware.vapi.std")
    }

    boolean hasMemeberFields(IdlStructure structure) {
        return !getSelfAndSuperFieldsAsFormalParams(structure).isEmpty()
    }

    private String toName(AbstractNamespacedNode node) {
        switch (node) {
            case IdlService:
            case IdlStructure:
            case IdlEnumeration:
                return getBindingClassFullName(node)
            default:
                return "BUG" //TODO: node ? getBindingClassFullName(node) : "BUG"
        }
    }

    private boolean isClientSideTemplate() {
        // TODO: this doesn't seem quite correct in general; is it OK for the
        //       purpose?
        return [
            "__Name__.java",
            "__Name__Definitions.java",
            "__Name__Types.java",
            "__Name__Stub.java"].any {
                    naming.templateFile.file.path.endsWith(it)
                }
    }

    private boolean isSyncProviderTemplate() {
        return naming.templateFile.file.path.endsWith("__Name__SyncProvider.java")
    }
}

/**
 * Provides a {@link CommentWriter} implementation using the {@link FunctionalHtmlCommentWriter}
 * base class, which renders a {@link DocModel} into a {@code Javadoc} comment.
 */
class JavadocCommentWriter extends HtmlCommentWriter {

    LanguageTermMapping termMapping

    /** Constructs an instance with the given link resolver. */
    JavadocCommentWriter(LinkConverter linkConverter, NameConverter nameConverter) {
        super(linkConverter, nameConverter)
    }

    JavadocCommentWriter injectTermMapping(LanguageTermMapping termMapping) {
        this.termMapping = termMapping
        return this
    }

    @Override
    String visit(AtLink element) {
        def linkText = element.resolvedLink ? linkConverter.toLink(element.resolvedLink) :
            element.unresolvedLink
        def displayText = element.displayText ? " " + element.displayText : ""

        return "{@link " + linkText + displayText + "}"
    }

    @Override
    String visit(AtCode element) {
        return "{@code " + element.text + "}"
    }

    @Override
    String visit(AtLiteral element) {
        return "{@literal " + element.text + "}"
    }

    @Override
    String visit(AtParamRef element) {
        def text = element.resolvedParameter ?
            JavaLanguageHelper.getJavaParamName(element.resolvedParameter) : element.text
        return "{@code " + text + "}"
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
        return "{@code ${name}}"
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
        return System.properties['term.emphasis'] ? "<em>${term}</em>" : term
    }

    @Override
    String makeString(DocModel docModel) {
        return WordUtils.wrap(super.makeString(docModel), 80)
    }
}

