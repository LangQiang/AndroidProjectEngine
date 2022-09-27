package com.lazylite.processorlib;

import com.google.auto.service.AutoService;
import com.lazylite.annotationlib.Constants;
import com.lazylite.annotationlib.DeepLink;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

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
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        Constants.ANNOTATION_DEEP_LINK_FULL_NAME
})
@AutoService(Processor.class)
public class DeeplinkProcessor extends AbstractProcessor {

    private static Set<String> supportOptions = new LinkedHashSet<>();

//    static {
//        supportOptions.add("xxx");
//    }

    private Filer mFiler;
    private Elements mElements;
    private Messager messager;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        log("DeeplinkProcessor start ...");
        if(annotations != null && !annotations.isEmpty()) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(DeepLink.class);
            try {
                parseProcessor(elements);
            } catch (IOException e) {
                log(e.getMessage() + "");
            }
            return true;
        }
        return false;
    }

    private void parseProcessor(Set<? extends Element> elements) throws IOException{
        if(elements == null || elements.isEmpty()) {
            return;
        }

        DeepLinkClassInfo deepLinkClassInfo = new DeepLinkClassInfo();

        for (Element element : elements) {

            deepLinkClassInfo.clear();

            TypeMirror tm = element.asType();

            deepLinkClassInfo.setFullName(tm.toString());

            deepLinkClassInfo.setPath(element.getAnnotation(DeepLink.class).path());

            File file = new File("./.idea/deeplink.config");
            File dir = new File("./.idea");
            if(!dir.exists()){
                if(!dir.mkdirs()){
                    return;
                }
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return;
                } else {
                    log("create deeplink.config");
                }
            } else {
                log("deeplink.config exists");
            }

            try {
                FileWriter fileWriter = new FileWriter(file,true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(deepLinkClassInfo.toJson());
                bufferedWriter.newLine();
                bufferedWriter.close();
            } catch (Exception ioe) {
                ioe.printStackTrace();
            }
        }
        log("-----------DeeplinkProcessor end------------");

    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        log("-----------DeeplinkProcessor start ------------");
        mFiler = processingEnvironment.getFiler();
        mElements = processingEnvironment.getElementUtils();
        messager = processingEnvironment.getMessager();
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
