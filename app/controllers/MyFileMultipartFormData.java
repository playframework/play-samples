package controllers;

import play.api.mvc.MultipartFormData;
import play.mvc.Http;
import scala.Function1;
import scala.collection.Seq;
import scala.compat.java8.OptionConverters;
import scala.runtime.AbstractFunction1;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static scala.collection.JavaConverters.mapAsJavaMapConverter;
import static scala.collection.JavaConverters.seqAsJavaListConverter;

/**
 * Extends Http.MultipartFormData to use File specifically,
 * converting from Scala API to Java API.
 */
class MyFileMultipartFormData extends Http.MultipartFormData<File> {

    private play.api.mvc.MultipartFormData<File> fileMultipartFormData;

    MyFileMultipartFormData(play.api.mvc.MultipartFormData<File> fileMultipartFormData) {
        this.fileMultipartFormData = fileMultipartFormData;
    }

    @Override
    public Map<String, String[]> asFormUrlEncoded() {
        return mapAsJavaMapConverter(
                fileMultipartFormData
                        .asFormUrlEncoded()
                        .mapValues(arrayFunction())
        ).asJava();
    }

    // maps from Scala Seq to String array
    private Function1<Seq<String>, String[]> arrayFunction() {
        return new AbstractFunction1<Seq<String>, String[]>() {
            @Override
            public String[] apply(Seq<String> v1) {
                String[] array = new String[v1.size()];
                v1.copyToArray(array);
                return array;
            }
        };
    }

    @Override
    public List<FilePart<File>> getFiles() {
        return seqAsJavaListConverter(fileMultipartFormData.files())
                .asJava()
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private FilePart<File> convert(MultipartFormData.FilePart<File> filePart) {
        return new FilePart<>(
                filePart.key(),
                filePart.filename(),
                OptionConverters.toJava(filePart.contentType()).orElse(null),
                filePart.ref()
        );
    }
}
