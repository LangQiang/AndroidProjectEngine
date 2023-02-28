package com.lazylite.processorlib;

import com.google.auto.service.AutoService;
import com.lazylite.annotationlib.AutoInit;
import com.lazylite.annotationlib.Constants;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        Constants.ANNOTATION_FULL_NAME
})
@AutoService(Processor.class)
public class InitProcessor extends AbstractProcessor {

    public final static Object lock = new Object();

    private static Set<String> supportOptions = new LinkedHashSet<>();

    static {
        supportOptions.add("moduleName");
    }

    private Filer mFiler;
    private Elements mElements;
//    private Types types;
    private String moduleName = null;   // Module name, maybe its 'app' or others
    private Messager messager;

    private InitClassInfo initClassInfo;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        //初始化我们需要的基础工具
        mFiler = processingEnvironment.getFiler();
        mElements = processingEnvironment.getElementUtils();
//        types = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        moduleName = processingEnvironment.getOptions().get("moduleName"); //define in module's build.gradle
//        if (moduleName == null || "".equals(moduleName)) {
//            moduleName = UUID.randomUUID().toString().replace("-", "");
//        }
        initClassInfo = new InitClassInfo();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if(annotations != null && !annotations.isEmpty()) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(AutoInit.class);
            try {
                log("Found Processor start ...");
                parseProcessor(elements);
            } catch (IOException e) {
                log(e.getMessage() + "");
            }
            return true;
        }
        return false;
    }

    private void parseProcessor(Set<? extends Element> elements) throws IOException {
        if(elements != null && !elements.isEmpty()) {
            log("Found processor, size is " + elements.size());
            //template interface
            TypeElement type_componentinit = mElements.getTypeElement("com.lazylite.bridge.init.Init");
            TypeElement type_context = mElements.getTypeElement("android.content.Context");

            /*
             * generator params
             * (Context context,boolean isDebug)
             */
            ParameterSpec param_Context = ParameterSpec.builder(ClassName.get(type_context),"context").build();

            /*
             * build method : void init(Context context,boolean isDebug)
             */
            MethodSpec.Builder initMethod = MethodSpec.methodBuilder("init")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(param_Context);

            /*
             * build method : public void initAfterAgreeProtocol(Context context);
             */
            MethodSpec.Builder initAfterAgreeProtocolMethod = MethodSpec.methodBuilder("initAfterAgreeProtocol")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(param_Context);

            /*
             * build method : public Pair<String, Object> getServicePair()
             */
            ParameterizedTypeName returns = ParameterizedTypeName.get(ClassName.get("android.util", "Pair"), ClassName.get("java.lang", "String"), TypeName.OBJECT);
            MethodSpec.Builder getServicePairMethod = MethodSpec.methodBuilder("getServicePair")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(returns)
                    ;

            List<FieldSpec> fieldSpecList = new ArrayList<>();

            if (elements.size() != 1) {
                throw new RuntimeException("multi init class");
            }

            Element element = elements.iterator().next();

            String fullName = element.asType().toString().replaceAll("\\.", "_");
            log("componendInit#name = " + fullName);
            TypeMirror tm = element.asType();

            TypeName initer = ClassName.get(tm);
            FieldSpec spec = FieldSpec.builder(initer, "initer")
                    .addModifiers(Modifier.PRIVATE)
                    .initializer("new $T()", initer)
                    .build();

            fieldSpecList.add(spec);

            initMethod.addStatement("initer.init(context)");
            initAfterAgreeProtocolMethod.addStatement("initer.initAfterAgreeProtocol(context)");
            getServicePairMethod.addStatement("return initer.getServicePair()");
            String[] strings = element.getAnnotation(AutoInit.class).dependOn();
            for (String string : strings) {
                initClassInfo.setDepend(string);
            }
            initClassInfo.setModuleName(element.getAnnotation(AutoInit.class).moduleName());

            initClassInfo.setFullName(Constants.PACKAGE + "." +
                    Constants.CLASS_NAME + Constants.SEPARATOR + fullName);

            log("Generated InitImpl start");

            TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.CLASS_NAME + Constants.SEPARATOR + fullName)
                    .addJavadoc(Constants.WARNING_TIPS)
                    .superclass(ClassName.get(type_componentinit));
            for (FieldSpec fieldSpec : fieldSpecList) {
                builder.addField(fieldSpec);
            }
            builder.addModifiers(Modifier.PUBLIC)
                    .addMethod(initMethod.build())
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(initAfterAgreeProtocolMethod.build())
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(getServicePairMethod.build());

            JavaFile.builder(Constants.PACKAGE,builder.build()).build().writeTo(mFiler);

            synchronized (lock) {
                File dir = new File("./.idea");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, "init.config");
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        log("init.config create Failed");
                        return;
                    } else {
                        log("create init.config");
                    }
                } else {
                    log("init.config exists");
                }

                try {
                    FileWriter fileWriter = new FileWriter(file,true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(initClassInfo.toJson());
                    bufferedWriter.newLine();
                    bufferedWriter.close();
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }

                log("Generated InitImpl finish");
            }


        }
    }

    @Override
    public Set<String> getSupportedOptions() {
        return supportOptions;
    }

    private void log(CharSequence info) {
        if(messager != null && info != null && info.length() != 0) {
            messager.printMessage(Diagnostic.Kind.NOTE, info);
        }
    }
}
