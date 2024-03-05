package com.moc.vocabularywebapp.service.result;

import com.moc.vocabularywebapp.model.Vocabulary;
import java.util.Map;

public interface ResultService {

    void setResult(Vocabulary vocabulary, String result);
    Map<Vocabulary, String> getResults();


}
