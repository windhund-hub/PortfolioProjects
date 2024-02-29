package com.moc.vocabularywebapp.service.result;

import com.moc.vocabularywebapp.model.Vocabulary;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ResultServiceImpl implements ResultService{

    private final Map<Vocabulary, String> results;

    public ResultServiceImpl(){
        results = new LinkedHashMap<>();
    }


    public void setResult(Vocabulary vocabulary, String result) {
        results.put(vocabulary,result);
    }

    public Map<Vocabulary, String> getResults() {
        return results;
    }
}
